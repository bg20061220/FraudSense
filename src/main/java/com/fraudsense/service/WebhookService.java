package com.fraudsense.service;

import com.fraudsense.model.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public Optional<Transaction> processEvent(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature: {}", e.getMessage());
            return Optional.empty();
        }

        log.info("Received event type :{}" , event.getType()) ; 

        if ("payment_intent.succeeded".equals(event.getType())) {
            try {
                JsonNode root = objectMapper.readTree(payload);
                JsonNode data = root.get("data").get("object");
                log.info("Raw event data: {}", data);

                String id = data.get("id").asText();
                long amount = data.get("amount").asLong();
                String currency = data.get("currency").asText();
                String customer = data.get("customer").asText();
                String status = data.get("status").asText();

                Transaction tx = new Transaction(
                        id,
                        amount,
                        currency,
                        customer,
                        status,
                        System.currentTimeMillis()
                );
                log.info("Transaction received: {}", tx);
                return Optional.of(tx);
            } catch (Exception e) {
                log.error("Failed to parse event payload", e);
                return Optional.empty();
            }
        }

        log.debug("Unhandled event type: {}", event.getType());
        return Optional.empty();
    }
}
