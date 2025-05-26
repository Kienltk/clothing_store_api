package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.config.CustomUserDetails;
import com.clothingstore.clothing_store_api.dto.ProductDTO;
import com.clothingstore.clothing_store_api.dto.ProductDetailDTO;
import com.clothingstore.clothing_store_api.dto.SearchProductDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ResponseObject<Map<String, List<ProductDTO>>>> getAllProducts(@RequestParam(required = false) Long userId) {
        Map<String, List<ProductDTO>> data = productService.getProductsByCategory(userId, null);
        ResponseObject<Map<String, List<ProductDTO>>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Okkk",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseObject<Map<String, List<ProductDTO>>>> getProductsBySubCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Long userId) {
        Map<String, List<ProductDTO>> data = productService.getProductsByCategory(userId, categoryId);
        ResponseObject<Map<String, List<ProductDTO>>> response = new ResponseObject<>(
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
        SearchProductDTO result = productService.searchProducts(productName, userId);
        ResponseObject<Map<String, Object>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                result.getMessage(),
                result.getData()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/detail/{productId}")
    public ResponseEntity<ResponseObject<ProductDetailDTO>> getProductDetails(
            @PathVariable Long productId,
            @RequestParam(required = false) Long userId) {
        ProductDetailDTO result = productService.getProductDetails(productId, userId);
        if (result == null) {
            return new ResponseEntity<>(new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Product not found", null), HttpStatus.NOT_FOUND);
        }
        ResponseObject<ProductDetailDTO> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "Product details retrieved",
                result
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/favorite")
    public  ResponseEntity<ResponseObject<List<ProductDTO>>> getFavoriteProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();

        List<ProductDTO> data = productService.getFavoriteProducts(userId);
        ResponseObject<List<ProductDTO>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "List favorite products",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}