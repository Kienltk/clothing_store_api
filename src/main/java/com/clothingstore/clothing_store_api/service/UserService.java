package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.LoginRequestDTO;
import com.clothingstore.clothing_store_api.dto.LoginResponseDTO;
import com.clothingstore.clothing_store_api.dto.RegisterDTO;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.exception.InvalidRefreshTokenException;
import com.clothingstore.clothing_store_api.repository.UserRepository;
import com.clothingstore.clothing_store_api.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    public void register(RegisterDTO registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhoneNumber())
                .email(registerRequest.getEmail())
                .address(registerRequest.getAddress())
                .dob(registerRequest.getDob())
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role("USER")
                .build();
        userRepository.save(user);
    }

    public String refreshAccessToken(String refreshToken) {
        if (tokenService.isTokenBlacklisted(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        String username = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!jwtUtil.isTokenValid(refreshToken, username)) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        return jwtUtil.generateToken(username, user.getRole());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ValidationException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new ValidationException("Invalid username or password");
        }
        Map<String, String> tokens = jwtUtil.generateTokenPair(user.getUsername(), user.getRole());
        LoginResponseDTO response = new LoginResponseDTO();
        response.setAccessToken(tokens.get("access_token"));
        response.setRefreshToken(tokens.get("refresh_token"));
        return response;
    }
    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}