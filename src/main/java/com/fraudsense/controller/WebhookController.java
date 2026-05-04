package com.fraudsense.controller;

import com.fraudsense.service.KafkaProducerService;
import com.fraudsense.service.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {

    private final WebhookService webhookService;
    private final KafkaProducerService kafkaProducerService;

    public WebhookController(WebhookService webhookService, KafkaProducerService kafkaProducerService) {
        this.webhookService = webhookService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestParam(required = false) String testCustomerId,
            @RequestParam(required = false) String testCountry,
            @RequestParam(required = false) Long testAmount) {

        var result = webhookService.processEvent(payload, sigHeader, testCustomerId, testCountry, testAmount);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature or unhandled event");
        }

        kafkaProducerService.publishTransaction(result.get());
        return ResponseEntity.ok("Event processed");
    }
}
