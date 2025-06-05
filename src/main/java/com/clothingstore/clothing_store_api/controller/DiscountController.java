package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.DiscountDTO;
import com.clothingstore.clothing_store_api.dto.DiscountResponseDTO;
import com.clothingstore.clothing_store_api.entity.Discount;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/discounts")
@PreAuthorize("hasRole('ADMIN')")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject<DiscountResponseDTO>> createDiscount(
            @Valid @RequestBody DiscountDTO discountDTO) {
        DiscountResponseDTO createdDiscount = discountService.addDiscount(discountDTO);
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.CREATED.value(), "Discount created successfully", createdDiscount),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Discount>> update(
            @PathVariable Long id,
         @Valid @RequestBody DiscountDTO dto) {
        Discount discount = discountService.updateDiscount(id, dto);
        if (discount == null) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Discount not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Discount updated successfully", discount),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<String>> delete(
            @PathVariable Long id) {
        boolean deleted = discountService.deleteDiscount(id);
        if (!deleted) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Discount not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Discount deleted successfully", "Deleted discount with id = " + id),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<Discount>> getById(
            @PathVariable Long id) {
        Discount discount = discountService.getDiscountById(id);
        if (discount == null) {
            return new ResponseEntity<>(
                    new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Discount not found", null),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Discount retrieved successfully", discount),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<Discount>>> getAll() {
        List<Discount> discounts = discountService.getAllDiscounts();
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Discounts retrieved successfully", discounts),
                HttpStatus.OK
        );
    }
}
