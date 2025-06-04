package com.clothingstore.clothing_store_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private Long parentId;
    @NotBlank(message = "Category name must not be blank")
    private String categoryName;
    private String slug;
}