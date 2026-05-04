package com.fraudsense.controller;

import com.fraudsense.model.ScoredTransaction;
import com.fraudsense.service.DynamoDbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private DynamoDbService dynamoDbService;

    @GetMapping
    public ResponseEntity<List<ScoredTransaction>> getAllTransactions() {
        log.info("GET /api/transactions");
        List<ScoredTransaction> transactions = dynamoDbService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<ScoredTransaction>> getFlaggedTransactions() {
        log.info("GET /api/transactions/flagged");
        List<ScoredTransaction> flagged = dynamoDbService.getFlaggedTransactions();
        return ResponseEntity.ok(flagged);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id) {
        log.info("GET /api/transactions/{}", id);
        ScoredTransaction transaction = dynamoDbService.getTransactionById(id);

        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transaction);
    }
}
