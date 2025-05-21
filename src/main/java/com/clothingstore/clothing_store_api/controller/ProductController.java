package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    public ResponseEntity<ResponseObject<Map<String, List<Map<String, Object>>>>> getAllProducts(@RequestParam(required = false) Long userId) {
        Map<String, List<Map<String, Object>>> data = productService.getProductsByCategory(userId, null, true);
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
        Map<String, List<Map<String, Object>>> data = productService.getProductsByCategory(userId, categoryId, false);
        ResponseObject<Map<String, List<Map<String, Object>>>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Okkk",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject<Map<String, Object>>> searchProducts(
            @RequestParam String productName,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> result = productService.searchProducts(productName, userId);
        String message = (String) result.get("message");
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        ResponseObject<Map<String, Object>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                message,
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<ResponseObject<Map<String, Object>>> getProductById(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId) {
        Map<String, Object> result = productService.getProductDetails(productId, userId);
        ResponseObject<Map<String, Object>> response = new ResponseObject<>();
        if (result == null) {
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("Product not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Product found");
        response.setData(result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}