package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<ResponseObject<Map<String, List<Map<String, Object>>>>> getAllProducts(@RequestParam(required = false) Long userId) {
        Map<String, List<Map<String, Object>>> data = productService.getProductsByParentCategory(userId);
        ResponseObject<Map<String, List<Map<String, Object>>>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Okkk",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseObject<Map<String, List<Map<String, Object>>>>> getProductsBySubCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Long userId) {
        Map<String, List<Map<String, Object>>> data = productService.getProductsBySubCategory(categoryId, userId);
        ResponseObject<Map<String, List<Map<String, Object>>>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Okkk",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}