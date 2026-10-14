package com.houseauction.demo.service;

import com.houseauction.demo.model.User;
import com.houseauction.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final HttpMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public PasswordResetService(UserRepository userRepository, HttpMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    // Step 1: Generate token and send email
    public void sendResetLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String base = frontendUrl.replaceAll("/+$", "");
        String resetLink = base + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("House Auction - Password Reset");
        message.setText("Click the link below to reset your password:\n\n" + resetLink +
                "\n\nThis link expires in 15 minutes.\nIgnore this if you didn't request a reset.");
        mailSender.send(message);
    }

    // Step 2: Validate token and update password
    public void resetPassword(String token, String newPassword, PasswordEncoder encoder) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

        if (LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new RuntimeException("Reset link has expired. Please request a new one.");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}