package house.maplelandutilback.controller;

import house.maplelandutilback.domain.BlockRequest;
import house.maplelandutilback.domain.Message;
import house.maplelandutilback.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HttpServletRequest request;

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);


    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {

        String ipAddress = request.getRemoteAddr();

        logger.info("Received search request for keyword: " + keyword.trim() + " IP : " + ipAddress);

        // 처리된 검색 결과 반환
        List<Message> messageList = messageService.performSearch(keyword.trim(),2000);
        return ResponseEntity.ok(messageList);
    }

    @PostMapping("/socket/addMessage")
    public ResponseEntity<?> addMessage(@RequestBody List<Message> messageList) {
        logger.info("Received addMessage request");

        messageService.saveListNonDuplicate(messageList);

        return ResponseEntity.ok().body("Messages saved successfully");
    }

    @PostMapping("/block/addBlock")
    public ResponseEntity<?> addBlock() {

        Long cnt = messageService.blockDetectAndDelete();
        logger.info("Received addBlock request cnt : "+ String.valueOf(cnt));
        return ResponseEntity.ok().body("삭제된 갯수 : " + cnt);
    }

}