package com.clothingstore.clothing_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long productId;
    private String productName;
    private String color;
    private String size;
    private Integer quantity;
    private Integer stock;
    private String imageUrl;
    private Double discountPercent;
    private String price;
    private String slug;
}
