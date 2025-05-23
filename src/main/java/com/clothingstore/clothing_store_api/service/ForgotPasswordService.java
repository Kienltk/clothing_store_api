package com.clothingstore.clothing_store_api.service;

import com.clothingstore.clothing_store_api.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
public class ForgotPasswordService {

    private final SendMailService emailService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ForgotPasswordService(SendMailService emailService,
                                 UserRepository userRepository,
                                 RedisTemplate<String, String> redisTemplate, BCryptPasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    public void sendResetCode(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new ValidationException("Email does not exist.");
        }

        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        String key = "forgot_password:" + email;
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));
        emailService.sendVerificationCode(email, code);
    }

    public void verifyCode(String email, String code) {
        String key = "forgot_password:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new ValidationException("No code found or the code has expired.");
        }

        if (!storedCode.equals(code)) {
            throw new ValidationException("Invalid verification code.");
        }
    }

    public void resetPassword(String email, String newPassword) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("Email does not exist."));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        String key = "forgot_password:" + email;
        redisTemplate.delete(key);
    }
}
