package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.config.CustomUserDetails;
import com.clothingstore.clothing_store_api.dto.CreateProductDTO;
import com.clothingstore.clothing_store_api.dto.ProductDTO;
import com.clothingstore.clothing_store_api.dto.ProductDetailDTO;
import com.clothingstore.clothing_store_api.dto.SearchProductDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ResponseObject<Map<String, List<ProductDTO>>>> getAllProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        Map<String, List<ProductDTO>> data = productService.getProductsByCategory(userId, null);
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.OK.value(), "Success", data), HttpStatus.OK);
    }

    @GetMapping("/category/{slug}")
    public ResponseEntity<ResponseObject<Map<String, List<ProductDTO>>>> getProductsBySubCategory(
            @PathVariable String slug, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        Map<String, List<ProductDTO>> data = productService.getProductsByCategory(userId, slug);
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.OK.value(), "Success", data), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject<Map<String, Object>>> searchProducts(
            @RequestParam String productName, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        SearchProductDTO result = productService.searchProducts(productName, userId);
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.OK.value(), result.getMessage(), result.getData()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseObject<ProductDTO>> addProduct(
           @Valid @RequestBody CreateProductDTO createProductDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        productService.addNewProduct(createProductDTO, userId);
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.CREATED.value(), "Product created successfully", null),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductDTO createProductDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        productService.editProduct(id, createProductDTO, userId);
        return new ResponseEntity<>(
                new ResponseObject<>(HttpStatus.OK.value(), "Product updated successfully", null),
                HttpStatus.OK
        );
    }

    @GetMapping("/detail/{slug}")
    public ResponseEntity<ResponseObject<ProductDetailDTO>> getProductDetails(
            @PathVariable String slug, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? userDetails.getUser().getId() : null;
        ProductDetailDTO result = productService.getProductDetails(slug, userId);
        if (result == null) {
            return new ResponseEntity<>(new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Product not found", null), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.OK.value(), "Product details retrieved", result), HttpStatus.OK);
    }


}