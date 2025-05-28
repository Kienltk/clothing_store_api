package com.clothingstore.clothing_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private String productName;
    private BigDecimal price;
    private String img;
    private String slug;
    private String color;
    private String size;
    private Integer quantity;
    private Double discount;
    private BigDecimal total;
}
