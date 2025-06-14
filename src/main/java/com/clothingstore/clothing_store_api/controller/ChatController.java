package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.ChatMessageDTO;
import com.clothingstore.clothing_store_api.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/history")
    public List<ChatMessageDTO> getHistory(@RequestParam Long userId, @RequestParam Long otherId) {
        return chatService.getChatHistory(userId, otherId);
    }
}