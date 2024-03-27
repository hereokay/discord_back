package house.maplelandutilback.schedule;

import house.maplelandutilback.controller.SearchController;
import house.maplelandutilback.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class DetectBlock {

    @Autowired
    private MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(DetectBlock.class);

    @Scheduled(fixedRate = 1000*60*5) // 5분 마다 실행
    public void detect() {
        String[] keywordArr = {
                "장공",
                "노우"
        };
        for (String keyword : keywordArr) {
            Long cnt = messageService.blockDetectAndDelete(keyword);
            logger.info("The blacklist deletion process has been executed : " + String.valueOf(cnt));
        }
    }

    @Scheduled(fixedRate = 1000*60*60) // 0분 마다 실행
    public void deleteOld() {
        Long cnt = messageService.deleteOrdMessage();
        logger.info("The oldMessage deletion process has been executed : "+ String.valueOf(cnt));
    }
}