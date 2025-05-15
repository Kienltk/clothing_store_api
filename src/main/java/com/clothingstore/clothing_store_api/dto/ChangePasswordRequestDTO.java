package com.clothingstore.clothing_store_api.dto;

import com.clothingstore.clothing_store_api.validator.password.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDTO {
    @ValidPassword
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    @ValidPassword
    @NotBlank(message = "New password is required")
    private String newPassword;
}
