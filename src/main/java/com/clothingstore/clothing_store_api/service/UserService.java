package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.dto.LoginRequestDTO;
import com.clothingstore.clothing_store_api.dto.LoginResponseDTO;
import com.clothingstore.clothing_store_api.dto.RegisterRequestDTO;
import com.clothingstore.clothing_store_api.entity.User;
import com.clothingstore.clothing_store_api.exception.ResourceNotFoundException;
import com.clothingstore.clothing_store_api.repository.UserRepository;
import com.clothingstore.clothing_store_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(RegisterRequestDTO registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        return response;
    }
}
