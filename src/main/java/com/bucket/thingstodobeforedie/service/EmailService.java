package com.bucket.thingstodobeforedie.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Send a password reset email asynchronously
     */
    @Async
    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Reset Your Password");
            
            // Create context variables for the template
            Context context = new Context();
            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("token", token);
            
            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process("reset-password-email", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }
    
    /**
     * Send a password change confirmation email asynchronously
     */
    @Async
    public void sendPasswordChangeConfirmationEmail(String to) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Your Password Has Been Changed");
            
            // Create context variables for the template
            Context context = new Context();
            String loginUrl = frontendUrl + "/login";
            context.setVariable("loginUrl", loginUrl);
            
            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process("password-change-confirmation", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Password change confirmation email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send password change confirmation email to {}: {}", to, e.getMessage());
        }
    }
} 