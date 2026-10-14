package com.houseauction.demo.service;

import com.houseauction.demo.model.User;
import com.houseauction.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final HttpMailSender mailSender;

    @Value("${app.otp-expiry-minutes}")
    private int otpExpiryMinutes;

    public OtpService(UserRepository userRepository, HttpMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    public void generateAndSendOtp(User user) {
        String otp = String.valueOf(100000 + RANDOM.nextInt(900000));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        userRepository.save(user);
        sendOtpEmail(user.getEmail(), otp);
    }

    public boolean validateOtp(User user, String enteredOtp) {
        if (user.getOtp() == null || user.getOtpExpiry() == null) return false;
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) return false;
        return user.getOtp().equals(enteredOtp);
    }

    public void clearOtp(User user) {
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    private void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("House Auction - Your OTP");
        message.setText("Your OTP is: " + otp + "\n\nValid for " + otpExpiryMinutes
                + " minutes.\nDo not share this with anyone.");
        mailSender.send(message);
    }
}