package com.clothingstore.clothing_store_api.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemStoreDTO {
    private Long productId;
    private String color;
    private String size;
    private Integer quantity;
}
