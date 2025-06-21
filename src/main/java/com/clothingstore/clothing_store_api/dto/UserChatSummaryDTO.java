package com.clothingstore.clothing_store_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserChatSummaryDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String latestMessage;
    private LocalDateTime latestTimestamp;
}
