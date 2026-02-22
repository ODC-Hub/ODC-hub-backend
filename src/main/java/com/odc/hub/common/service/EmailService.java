package com.odc.hub.common.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }
    @Async
    public void sendActivationEmail(String to, String token) {
        try {
            String link = "http://localhost:5173/activate?token=" + token;

            Context context = new Context();
            context.setVariable("link", link);

            String html = templateEngine.process("email/activation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Activate your ODC Hub account");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send activation email to {}", to, e);
        }
    }


    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            String link = "http://localhost:5173/reset-password/" + token;

            Context context = new Context();
            context.setVariable("link", link);

            String html = templateEngine.process("email/reset-password", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Reset your password");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", to, e);
        }
    }

    @Async
    public void sendPlanningNotificationEmail(
            String to,
            String title,
            String message
    ) {
        try {
            Context context = new Context();
            context.setVariable("title", title);
            context.setVariable("message", message);
            context.setVariable("link", "http://localhost:5173/calendar");

            String html = templateEngine.process(
                    "email/planning-notification",
                    context
            );

            MimeMessage messageMail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(messageMail, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("ODC Hub – Planning update");
            helper.setText(html, true);

            mailSender.send(messageMail);
        } catch (Exception e) {
            log.error("Failed to send planning email to {}", to, e);
        }
    }

    @Async
    public void sendResourceNotificationEmail(
            String to,
            String title,
            String message,
            String link
    ) {
        try {
            Context context = new Context();
            context.setVariable("title", title);
            context.setVariable("message", message);
            context.setVariable("link", link);

            String html = templateEngine.process(
                    "email/resource-notification",
                    context
            );

            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("ODC Hub – Resource update");
            helper.setText(html, true);

            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Failed to send resource email to {}", to, e);
        }
    }

    @Async
    public void sendLivrableNotificationEmail(
            String to,
            String title,
            String message,
            String link
    ) {
        try {
            Context context = new Context();
            context.setVariable("title", title);
            context.setVariable("message", message);
            context.setVariable("link", link);

            String html = templateEngine.process(
                    "email/livrable-notification",
                    context
            );

            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("ODC Hub – Homework update");
            helper.setText(html, true);

            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Failed to send livrable email to {}", to, e);
        }
    }

    @Async
    public void sendProjectNotificationEmail(
            String to,
            String title,
            String message,
            String link
    ) {
        try {
            Context context = new Context();
            context.setVariable("title", title);
            context.setVariable("message", message);
            context.setVariable("link", link);

            String html = templateEngine.process(
                    "email/project-notification",
                    context
            );

            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("ODC Hub – Project update");
            helper.setText(html, true);

            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Failed to send project email to {}", to, e);
        }
    }

    @Async
    public void sendAdminRegistrationRequestEmail(
            String to,
            String userEmail,
            String link
    ) {
        try {
            Context context = new Context();
            context.setVariable("userEmail", userEmail);
            context.setVariable("link", link);

            String html = templateEngine.process(
                    "email/admin-registration-request",
                    context
            );

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("ODC Hub – New registration request");
            helper.setText(html, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error(
                    "Failed to send admin registration email to {}",
                    to,
                    e
            );
        }
    }
}
