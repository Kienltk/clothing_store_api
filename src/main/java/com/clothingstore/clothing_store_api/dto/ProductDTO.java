package com.clothingstore.clothing_store_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;
    private BigDecimal price;
    private BigDecimal discount;
    private String status;
    private String img;
    private List<StockDetailDTO> stockDetails;
    private Boolean isFavorite;
    private String slug;
}
