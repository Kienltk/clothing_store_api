package com.clothingstore.clothing_store_api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class DiscountResponseDTO {
    private Long id;
    private BigDecimal discountPercent;
    private OffsetDateTime startSale;
    private OffsetDateTime endSale;
    private Long productId;
    private String productName;
}