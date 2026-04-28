package com.fraudsense.service;

import com.fraudsense.model.Transaction;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

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

        if ("payment_intent.succeeded".equals(event.getType())) {
            StripeObject stripeObject = event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (stripeObject instanceof PaymentIntent paymentIntent) {
                Transaction tx = new Transaction(
                        paymentIntent.getId(),
                        paymentIntent.getAmount(),
                        paymentIntent.getCurrency(),
                        paymentIntent.getCustomer(),
                        paymentIntent.getStatus(),
                        System.currentTimeMillis()
                );
                log.info("Transaction received: {}", tx);
                return Optional.of(tx);
            }
        }

        log.debug("Unhandled event type: {}", event.getType());
        return Optional.empty();
    }
}
