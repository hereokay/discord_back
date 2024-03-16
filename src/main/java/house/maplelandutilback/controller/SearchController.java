package house.maplelandutilback.controller;

import house.maplelandutilback.domain.Chat;
import house.maplelandutilback.domain.SearchRequest;
import house.maplelandutilback.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired
    ChatService chatService;
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest searchRequest) {
        // 검색어 처리 로직
        // service
        List<Chat> chatList = chatService.performSearch(searchRequest);

        // 처리된 검색 결과 반환
        return ResponseEntity.ok(chatList);
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody SearchRequest searchRequest) {
        // 검색어 처리 로직
        // service
//        List<Chat> chatList = chatService.findChatsContainingKeyword(searchRequest);

        // 처리된 검색 결과 반환
        return ResponseEntity.ok(chatService.getLatestChat());
    }




}
