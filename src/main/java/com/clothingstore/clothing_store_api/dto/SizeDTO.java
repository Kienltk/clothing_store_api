package com.clothingstore.clothing_store_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SizeDTO {
    private Long id;
    @NotBlank(message = "Size must not be blank")
    @Size(max = 10, message = "Size must not exceed 10 characters")
    private String size;
}