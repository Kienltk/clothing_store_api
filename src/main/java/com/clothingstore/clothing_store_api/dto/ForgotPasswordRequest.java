package com.clothingstore.clothing_store_api.dto;

import com.clothingstore.clothing_store_api.validator.password.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;
    @NotBlank(message = "Info is required")
    private String info;
    @ValidPassword
    @NotBlank(message = "Password is required")
    private String newPassword;
}
