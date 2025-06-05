package com.clothingstore.clothing_store_api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
public class DiscountDTO {
    private Long id;

    @NotNull(message = "Discount percent must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount percent must be greater than 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Discount percent must not exceed 100")
    private BigDecimal discountPercent;

    @NotNull(message = "Start date must not be null")
    private OffsetDateTime startSale;

    @NotNull(message = "End date must not be null")
    @Future(message = "End date must be in the future")
    private OffsetDateTime  endSale;

    @NotNull(message = "Product ID must not be null")
    private Long productId;
}
