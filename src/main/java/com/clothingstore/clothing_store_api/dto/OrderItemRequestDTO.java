package com.clothingstore.clothing_store_api.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long productId;
    private String color;
    private String size;
    private Integer quantity;
    private Double discount;
}
