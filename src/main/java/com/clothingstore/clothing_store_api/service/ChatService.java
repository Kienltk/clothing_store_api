package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.ChatMessageDTO;
import com.clothingstore.clothing_store_api.dto.UserDTO;
import com.clothingstore.clothing_store_api.entity.ChatMessage;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.repository.ChatMessageRepository;
import com.clothingstore.clothing_store_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendMessage(ChatMessage message) {
        var sender = userRepository.findById(message.getSender().getId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        var receiver = userRepository.findById(message.getReceiver().getId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(message);

        ChatMessageDTO dto = mapToDto(message);

        messagingTemplate.convertAndSend("/topic/" + receiver.getId(), dto);
        messagingTemplate.convertAndSend("/topic/" + sender.getId(), dto);
    }

    public List<ChatMessageDTO> getChatHistory(Long userId, Long otherId) {
        List<ChatMessage> messages = chatMessageRepository.findAll().stream()
                .filter(m -> (m.getSender().getId().equals(userId) && m.getReceiver().getId().equals(otherId)) ||
                        (m.getSender().getId().equals(otherId) && m.getReceiver().getId().equals(userId)))
                .toList();

        return messages.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

private ChatMessageDTO mapToDto(ChatMessage message) {
    ChatMessageDTO dto = new ChatMessageDTO();
    dto.setId(message.getId());
    dto.setContent(message.getContent());


    if (message.getTimestamp() != null) {
        // Giả sử timestamp trong DB là UTC
        var vietnamTime = message.getTimestamp()
                .atZone(java.time.ZoneOffset.UTC)
                .withZoneSameInstant(java.time.ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDateTime();
        dto.setTimestamp(vietnamTime);
    }

    dto.setSender(mapUserToDto(message.getSender()));
    dto.setReceiver(mapUserToDto(message.getReceiver()));

    return dto;
}
    public List<UserDTO> getUsersWithAdminChat(Long adminId) {
        if (adminId == null) {
            return Collections.emptyList();
        }

        List<ChatMessage> messages = chatMessageRepository.findAll().stream()
                .filter(m -> m.getSender().getId().equals(adminId) || m.getReceiver().getId().equals(adminId))
                .toList();

        return messages.stream()
                .map(m -> m.getSender().getId().equals(adminId) ? m.getReceiver() : m.getSender())
                .distinct()
                .map(this::mapUserToDto)
                .collect(Collectors.toList());
    }
    public Long getAdminIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
    private UserDTO mapUserToDto(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }
}

