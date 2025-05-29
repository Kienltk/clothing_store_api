package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.config.CustomUserDetails;
import com.clothingstore.clothing_store_api.dto.ProductDTO;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.FavoriteService;
import com.clothingstore.clothing_store_api.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/list")
    public  ResponseEntity<ResponseObject<List<ProductDTO>>> getFavoriteProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();

        List<ProductDTO> data = favoriteService.getFavoriteProducts(userId);
        ResponseObject<List<ProductDTO>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                "List favorite products",
                data
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ResponseObject<Map<String, Object>>> addFavorite(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                           @PathVariable Long productId) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();
        Map<String, String> data = favoriteService.addFavorite(userId, productId);
        ResponseObject<Map<String, Object>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                data.get("message"),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ResponseObject<Map<String, Object>>> removeFavorite(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                              @PathVariable Long productId) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        Long userId = userDetails.getUser().getId();
        Map<String, String> data = favoriteService.removeFavorite(userId, productId);
        ResponseObject<Map<String, Object>> response = new ResponseObject<>(
                HttpStatus.OK.value(),
                data.get("message"),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
