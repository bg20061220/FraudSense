package com.fraudsense.service;

import com.fraudsense.model.Transaction;
import com.fraudsense.model.ScoredTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ScoringEngine scoringEngine;

    public KafkaConsumerService(ScoringEngine scoringEngine) {
        this.scoringEngine = scoringEngine;
    }

    @KafkaListener(topics = "transactions", groupId = "fraudsense-group")
    public void consumeTransaction(Transaction transaction) {
        log.info("Consumer received transaction: {}", transaction);

        ScoredTransaction scored = scoringEngine.score(transaction);
        log.info("Scored: {}", scored);
    }
}
