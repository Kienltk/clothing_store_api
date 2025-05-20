package com.clothingstore.clothing_store_api.controller;

import com.clothingstore.clothing_store_api.response.ResponseObject;
import com.clothingstore.clothing_store_api.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseObject<String>> sendResetCode(@RequestParam String email) {
        try {
            forgotPasswordService.sendResetCode(email);
            return ResponseEntity.ok(new ResponseObject<>(200, "Verification code has been sent to your email.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ResponseObject<String>> verifyCode(@RequestParam String email, @RequestParam String code) {
        try {
            forgotPasswordService.verifyCode(email, code);
            return ResponseEntity.ok(new ResponseObject<>(200, "Code is valid. You can now reset your password.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseObject<String>> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        try {
            forgotPasswordService.resetPassword(email, newPassword);
            return ResponseEntity.ok(new ResponseObject<>(200, "Password has been reset successfully!", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(400, e.getMessage(), null));
        }
    }

}
