package com.clothingstore.clothing_store_api.validator.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword,String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {


        if (password.length() < 6) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must be at least 6 characters")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*[A-Za-z].*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one letter")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must contain at least one digit")
                    .addConstraintViolation();
            return false;
        }

        if (!password.matches("^[A-Za-z\\d@$.!%*#?&]*$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password contains invalid characters")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
