package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.ChatMessageDTO;
import com.clothingstore.clothing_store_api.dto.UserDTO;
import com.clothingstore.clothing_store_api.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users-with-admin")
    public List<UserDTO> getUsersWithAdminChat() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Long adminId = chatService.getAdminIdByUsername(username);
        return chatService.getUsersWithAdminChat(adminId);
    }
}