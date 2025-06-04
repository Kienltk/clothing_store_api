package com.clothingstore.clothing_store_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductDTO {
    @NotBlank(message = "Product name must not be blank")
    private String productName;
    @NotNull(message = "Price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    private String status;
    @NotEmpty(message = "At least one category ID is required")
    private List<@NotNull(message = "Category ID must not be null") Long> categoryIds;
    @NotBlank(message = "Main image URL must not be blank")
    private String imgMain;
    @NotEmpty(message = "At least one variant (stock detail) is required")
    private List<@Valid StockDetailDTO> variants;
    private String slug;
}
