package com.clothingstore.clothing_store_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendMailService {
    private final JavaMailSender mailSender;
    @Autowired
    public SendMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ndc7571@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset Code");
            message.setText("Your password reset code is: " + code);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
