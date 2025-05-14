package com.clothingstore.clothing_store_api.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseObject <T> {
    private String message;
    private T data;
}