package com.clothingstore.clothing_store_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ColorDTO {
    private Long id;
    @NotBlank(message = "Color must not be blank")
    private String color;
}
