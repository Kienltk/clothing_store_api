package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.CartItemGetDTO;
import com.clothingstore.clothing_store_api.dto.CartItemStoreDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.CartRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartRedisService cartService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<CartItemGetDTO>>> getCart(@RequestParam Long userId) {
        List<CartItemGetDTO> cartItems = cartService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Successfully fetched cart items.", cartItems));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<CartItemGetDTO>> addCartItem(
            @RequestParam Long userId,
            @RequestBody CartItemStoreDTO addCartItemDTO) {
        CartItemGetDTO cartItem = cartService.addCartItem(userId, addCartItemDTO);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Cart item added successfully.", cartItem));
    }

    @PutMapping
    public ResponseEntity<ResponseObject<CartItemGetDTO>> editCartItem(
            @RequestParam Long userId,
            @RequestBody CartItemStoreDTO editCartItemDTO) {
        CartItemGetDTO cartItem = cartService.editCartItem(userId, editCartItemDTO);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Cart item updated successfully.", cartItem));
    }
}
