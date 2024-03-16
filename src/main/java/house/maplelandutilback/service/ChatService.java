package house.maplelandutilback.service;

import house.maplelandutilback.domain.Chat;
import house.maplelandutilback.domain.SearchRequest;
import house.maplelandutilback.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ChatService {

    @Autowired
    private  ChatRepository chatRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Chat> performSearch(SearchRequest request) {
        Query query = new Query();

        // 'content' 필드에 대한 검색 조건 추가 (키워드가 포함되어 있는 경우)
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            query.addCriteria(Criteria.where("content").regex(request.getContent(), "i")); // 대소문자 구분 없음
        }

        query.limit(2000);


        return mongoTemplate.find(query, Chat.class);
    }

    public List<Chat> findChatsContainingKeyword(SearchRequest request) {

        String keyword = request.getContent();
        Query query = new Query();
        query.addCriteria(Criteria.where("content").regex(keyword, "i")); // 'i'는 대소문자를 구분하지 않음
        return mongoTemplate.find(query, Chat.class);
    }

    public Chat getLatestChat() {
        Query query = new Query();
        // 'timeStamp' 필드를 기준으로 내림차순 정렬하여 가장 최신의 문서를 가져옵니다.
        query.with(Sort.by(Sort.Direction.DESC, "timeStamp"));
        // 결과 중에서 첫 번째 문서만 가져옵니다.
        query.limit(1);

        List<Chat> chatList = mongoTemplate.find(query, Chat.class);
        if (!chatList.isEmpty()) {
            return chatList.get(0); // 리스트가 비어 있지 않다면 첫 번째 요소 반환
        }
        return null; // 조회된 문서가 없다면 null 반환
    }
}

