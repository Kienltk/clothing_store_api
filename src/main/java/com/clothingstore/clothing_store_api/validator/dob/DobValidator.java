package com.clothingstore.clothing_store_api.validator.dob;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class DobValidator implements ConstraintValidator<DobConstraint, Date> {
    @Override
    public boolean isValid(Date dob, ConstraintValidatorContext context) {
        if (dob == null) return false;

        LocalDate birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        int age = Period.between(birthDate, today).getYears();

        return !birthDate.isAfter(today)
                && !birthDate.isBefore(today.minusYears(100))
                && age >= 10;
    }
}
