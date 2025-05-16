package com.clothingstore.clothing_store_api.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T>  {
    private int code;
    private String message;
    private T data;
}