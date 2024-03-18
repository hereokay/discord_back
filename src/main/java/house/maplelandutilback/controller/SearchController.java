package house.maplelandutilback.controller;

import house.maplelandutilback.domain.Message;
import house.maplelandutilback.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String keyword) {

        // 처리된 검색 결과 반환
        List<Message> messageList = messageService.performSearch(keyword);
        System.out.println(messageList.get(0).getContent());
        return ResponseEntity.ok(messageList);
    }

    @PostMapping("/socket/addMessage")
    public ResponseEntity<?> addMessage(@RequestBody List<Message> messageList) {

        messageService.checkAndSaveAllMessages(messageList);

        return ResponseEntity.ok().body("Messages saved successfully");
    }

}