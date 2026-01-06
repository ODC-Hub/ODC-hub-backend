package com.odc.hub.common.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String to, String token) {

        String link = "http://localhost:5173/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Activate your ODC Hub account");
        message.setText(
                "Your account has been approved.\n\n" +
                        "Click the link below to activate your account:\n" +
                        link +
                        "\n\nThis link expires in 24 hours."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {

        String link = "http://localhost:5173/reset-password/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your password");
        message.setText(
                "Click the link below to reset your password:\n\n" +
                        link +
                        "\n\nThis link expires in 15 minutes.\n" +
                        "If you didn’t request this, ignore this email."
        );

        mailSender.send(message);
    }


}
