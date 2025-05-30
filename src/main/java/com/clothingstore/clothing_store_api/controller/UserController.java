package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.config.CustomUserDetails;
import com.clothingstore.clothing_store_api.dto.*;
import com.clothingstore.clothing_store_api.dto.LoginRequestDTO;
import com.clothingstore.clothing_store_api.dto.LoginResponseDTO;
import com.clothingstore.clothing_store_api.dto.RefreshResponseDTO;
import com.clothingstore.clothing_store_api.dto.RegisterDTO;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.SendMailService;
import com.clothingstore.clothing_store_api.service.TokenService;
import com.clothingstore.clothing_store_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;


    @PostMapping("/register")
    public ResponseEntity<ResponseObject<RegisterDTO>> register(@Valid @RequestBody RegisterDTO registerRequest) {
        userService.register(registerRequest);
        return ResponseEntity.ok(new ResponseObject<>(200, "Register successful", registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(new ResponseObject<>(200, "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject<RefreshResponseDTO>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, "Refresh token is missing", null));
        }
        String newAccessToken = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new ResponseObject<>(200, "Token refreshed successfully", new RefreshResponseDTO(newAccessToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObject<String>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, "Refresh token is missing", null));
        }
        tokenService.blacklistRefreshToken(refreshToken);
        return ResponseEntity.ok(new ResponseObject<>(200, "Logged out successfully", null));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseObject<String>> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ChangePasswordRequestDTO request
    ) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseObject<>(401, "User not authenticated", null));
        }
        User user = userDetails.getUser();
        userService.changePassword(user, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(new ResponseObject<>(200, "Password changed successfully", null));
    }
}
