package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.dto.LoginRequestDTO;
import com.clothingstore.clothing_store_api.dto.LoginResponseDTO;
import com.clothingstore.clothing_store_api.dto.RefreshResponseDTO;
import com.clothingstore.clothing_store_api.dto.RegisterDTO;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.service.TokenService;
import com.clothingstore.clothing_store_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> register(@Valid @RequestBody RegisterDTO registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDTO> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String newAccessToken = userService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(new RefreshResponseDTO(newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        tokenService.blacklistRefreshToken(refreshToken);

        return ResponseEntity.ok().build();
    }
}
