package com.fraudsense.service;

import com.fraudsense.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "transactions";

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTransaction(Transaction transaction) {
        log.info("Publishing transaction to Kafka: {}", transaction);
        kafkaTemplate.send(TOPIC, transaction.getId(), transaction);
    }
}
