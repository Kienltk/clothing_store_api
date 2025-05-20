package com.clothingstore.clothing_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
public class VerificationCode {
    private String code;
    private LocalDateTime expiryTime;
}