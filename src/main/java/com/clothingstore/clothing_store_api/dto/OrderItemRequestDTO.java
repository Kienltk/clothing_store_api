package com.clothingstore.clothing_store_api.dto;

import lombok.Data;

@Data
public class OrderItemRequestDTO {
    private Long productSizeId;
    private Integer quantity;
    private Double discount;
}
