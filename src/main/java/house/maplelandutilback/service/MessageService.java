package house.maplelandutilback.service;


import house.maplelandutilback.domain.Block;
import house.maplelandutilback.domain.BlockRequest;
import house.maplelandutilback.domain.Message;
import house.maplelandutilback.repository.BlockRepository;
import house.maplelandutilback.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    // keyword 포함된 문장 2000개 찾기, limit 이 0이면 무제한
    public List<Message> performSearch(String keyword, int limit, boolean unique) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Criteria> criteriaList = new ArrayList<>();
        String[] contentKeywords = keyword.split(" ");
        for (String k : contentKeywords){
            criteriaList.add(Criteria.where("content").regex(".*" + k + ".*", "i"));
        }

        Criteria combinedCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(combinedCriteria).with(Sort.by(Sort.Direction.DESC, "timeStamp")).limit(2000);

        List<Message> messageList = mongoTemplate.find(query, Message.class);

        if (unique){
            return getUniqueContentMessages(messageList);
        }

        return messageList;
    }

    public void saveListNonDuplicate(List<Message> messageList) {

        // 유니크한 content 추출
        List<Message> uniqueContentMessages = getUniqueContentMessages(messageList);
        List<Message> nonBlockList = new ArrayList<>();

        // 블랙리스트가 아닌것만 추가
        for (Message message : uniqueContentMessages){
            Optional<Block> blockOptional = blockRepository.findByContent(message.getContent());

            // 블랙리스트가 아닐경우
            if (blockOptional.isEmpty()){
                nonBlockList.add(message);
            }
        }

        // 이후 모든 데이터 업로드
        messageRepository.saveAll(nonBlockList);
    }


    // 모두 하나씩 리턴, 주의 : 개수가 2개인 content 도 한개가 리턴됨
    private static List<Message> getUniqueContentMessages(List<Message> messageList) {

        // 중복 없이 순서를 유지하는 Set 생성하여 각 content 첫 등장을 추적
        Set<String> uniqueContents = new LinkedHashSet<>();

        // 중복된 content 제거 & 순서를 유지한 채로 메시지를 선택
        return messageList.stream()
                .filter(message -> uniqueContents.add(message.getContent()))
                .collect(Collectors.toList());
    }


    public Long deleteOrdMessage(){
        // 현재 시간으로부터 48시간 이전 시간 계산
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -48);
        Date cutoffDate = cal.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String format = dateFormat.format(cutoffDate);

        Query query = new Query(Criteria.where("timeStamp").lt(format));
        List<Message> oldMessages = mongoTemplate.find(query, Message.class);


        if (oldMessages.size() < 400000){
            return mongoTemplate.remove(query, Message.class).getDeletedCount();
        }
        else{
            return 0L;
        }

    }


    public Long blockDetectAndDelete(String keyword){
        List<Message> messageList = performSearch(keyword,0, false);

        Map<String, Integer> contentFrequency = new HashMap<>();

        // 각 메시지에 대해 content 빈도수 계산
        for (Message message : messageList) {
            String content = message.getContent();
            contentFrequency.put(content, contentFrequency.getOrDefault(content, 0) + 1);
        }
        Long cnt = 0L;
        // 빈도수가 100 이상인 content 블랙리스트에 추가
        for (Map.Entry<String, Integer> entry : contentFrequency.entrySet()) {
            if (entry.getValue() >= 10) {
                String content = entry.getKey();

                // content 블랙리스트에 추가하고
                 blockRepository.save(new Block(content));
//
                // content 가진 메시지를 모두 삭제
                Long l = messageRepository.deleteByContent(content);
                cnt += l;

            }
        }
//        System.out.println("keyword : " + keyword + " 삭제된 갯수 : " + cnt);
        return cnt;
    }

    public Long keywordBlack(BlockRequest blockRequest){
        Long cnt = 0L;
        List<Message> messageList = performSearch(blockRequest.getKeyword(),0,false);

        for (Message message : messageList){
            // content 블랙리스트에 추가하고
            blockRepository.save(new Block(message.getContent()));
            Long l = messageRepository.deleteByContent(message.getContent());
            cnt += l;
        }

        return cnt;
    }



    /* 블랙리스트 로직

    1. 저장시 블랙리스트 탐색 -> 아니면 추가하기 : indexing 검색

    2. 주기적으로 "장공" 검색

    3. 동일한 content 개수가 100 이상일 경우 블랙리스트 추가

    4.

     */
}
