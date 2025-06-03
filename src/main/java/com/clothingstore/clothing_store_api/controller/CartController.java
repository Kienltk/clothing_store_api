package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.config.CustomUserDetails;
import com.clothingstore.clothing_store_api.dto.CartItemDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.CartRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartRedisService cartService;

    @GetMapping
    public ResponseEntity<ResponseObject<List<CartItemDTO>>> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();
        List<CartItemDTO> cartItems = cartService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Successfully fetched cart items.", cartItems));
    }

    @PostMapping
    public ResponseEntity<ResponseObject<CartItemDTO>> addCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CartItemDTO addCartItemDTO) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();
        CartItemDTO cartItem = cartService.addOrUpdateCartItem(userId, addCartItemDTO);
        return ResponseEntity.ok(
                new ResponseObject<>(200, "Cart item added successfully.", cartItem));
    }

//    @PutMapping
//    public ResponseEntity<ResponseObject<CartItemDTO>> editCartItem(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestBody CartItemDTO editCartItemDTO) {
//        if (userDetails == null) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new ResponseObject<>(401, "User not authenticated", null));
//        }
//        Long userId = userDetails.getUser().getId();
//        CartItemDTO cartItem = cartService.editCartItem(userId, editCartItemDTO);
//        return ResponseEntity.ok(
//                new ResponseObject<>(200, "Cart item updated successfully.", cartItem));
//    }
    
//    @DeleteMapping
//    public ResponseEntity<ResponseObject<String>> deleteCartItem(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestParam Long productSizeId) {
//        if (userDetails == null) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(new ResponseObject<>(401, "User not authenticated", null));
//        }
//        Long userId = userDetails.getUser().getId();
//        cartService.deleteCartItem(userId, productSizeId);
//        return ResponseEntity.ok(
//                new ResponseObject<>(200, "Cart item deleted successfully.", null));
//    }
}
