package com.houseauction.demo.config;

import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

/**
 * When SPRING_MAIL_USERNAME is not set, provide a no-op mail sender so the app can start.
 * OTP text is printed to logs instead of sent (fine for initial Render deploy).
 */
@Configuration
@ConditionalOnExpression("'${spring.mail.username:}'.isEmpty()")
@EnableAutoConfiguration(exclude = MailSenderAutoConfiguration.class)
public class DevMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {
            @Override
            public void send(SimpleMailMessage simpleMessage) {
                System.out.println("=== MAIL (dev mode — not sent) ===");
                if (simpleMessage.getTo() != null) {
                    System.out.println("To: " + String.join(", ", simpleMessage.getTo()));
                }
                System.out.println("Subject: " + simpleMessage.getSubject());
                System.out.println(simpleMessage.getText());
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) {
                for (SimpleMailMessage message : simpleMessages) {
                    send(message);
                }
            }

            @Override
            public MimeMessage createMimeMessage() {
                return new JavaMailSenderImpl().createMimeMessage();
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) {
                return createMimeMessage();
            }

            @Override
            public void send(MimeMessage mimeMessage) {
                System.out.println("=== MAIL (dev mode — mime not sent) ===");
            }

            @Override
            public void send(MimeMessage... mimeMessages) {
                for (MimeMessage mimeMessage : mimeMessages) {
                    send(mimeMessage);
                }
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator) {
                System.out.println("=== MAIL (dev mode — preparator not sent) ===");
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) {
                for (MimeMessagePreparator preparator : mimeMessagePreparators) {
                    send(preparator);
                }
            }
        };
    }
}
