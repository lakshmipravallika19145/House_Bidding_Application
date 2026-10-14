package com.houseauction.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class HttpMailSender {

    private static final Logger log = LoggerFactory.getLogger(HttpMailSender.class);

    @Value("${brevo.api-key}")      private String apiKey;
    @Value("${brevo.sender-email}") private String senderEmail;
    @Value("${brevo.sender-name:House Auction}") private String senderName;

    private final RestTemplate rest;

    public HttpMailSender() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5000);
        f.setReadTimeout(10000);
        this.rest = new RestTemplate(f);
    }

    public void send(SimpleMailMessage msg) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        List<Map<String, String>> to = new ArrayList<>();
        for (String addr : Objects.requireNonNull(msg.getTo())) {
            to.add(Map.of("email", addr));
        }

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", to,
                "subject", msg.getSubject() == null ? "" : msg.getSubject(),
                "textContent", msg.getText() == null ? "" : msg.getText()
        );

        try {
            rest.postForEntity("https://api.brevo.com/v3/smtp/email",
                    new HttpEntity<>(body, headers), String.class);
        } catch (RestClientException e) {
            log.error("Email send failed: {}", e.getMessage());
            throw new IllegalStateException("Could not send email", e);
        }
    }
}