package com.fraudsense.service;

import com.fraudsense.model.ScoredTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DynamoDbService {
    private static final Logger log = LoggerFactory.getLogger(DynamoDbService.class);

    @Autowired
    private DynamoDbTable<ScoredTransaction> scoredTransactionTable;

    public void saveTransaction(ScoredTransaction transaction) {
        try {
            scoredTransactionTable.putItem(transaction);
            log.info("Saved transaction to DynamoDB: id={}, customerId={}, riskLevel={}",
                    transaction.getId(), transaction.getCustomerId(), transaction.getRiskLevel());
        } catch (Exception e) {
            log.error("Failed to save transaction to DynamoDB: {}", transaction.getId(), e);
            throw new RuntimeException("DynamoDB save failed", e);
        }
    }

    public ScoredTransaction getTransactionById(String id) {
        try {
            List<ScoredTransaction> transactions = new ArrayList<>();

            // Scan and filter by id
            PageIterable<ScoredTransaction> pages = scoredTransactionTable.scan();
            pages.items().forEach(transactions::add);

            return transactions.stream()
                    .filter(tx -> id.equals(tx.getId()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.error("Failed to retrieve transaction from DynamoDB: {}", id, e);
            return null;
        }
    }

    public List<ScoredTransaction> getAllTransactions() {
        try {
            List<ScoredTransaction> transactions = new ArrayList<>();

            // Scan entire table, return as-is
            PageIterable<ScoredTransaction> pages = scoredTransactionTable.scan();
            pages.items().forEach(transactions::add);

            return transactions;
        } catch (Exception e) {
            log.error("Failed to retrieve all transactions from DynamoDB", e);
            return new ArrayList<>();
        }
    }

    public List<ScoredTransaction> getFlaggedTransactions() {
        try {
            List<ScoredTransaction> flaggedTransactions = new ArrayList<>();

            // Server-side FilterExpression: only fetch SUSPICIOUS or FRAUDULENT
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":suspicious", AttributeValue.builder().s("SUSPICIOUS").build());
            expressionValues.put(":fraudulent", AttributeValue.builder().s("FRAUDULENT").build());

            Expression filterExpression = Expression.builder()
                    .expression("riskLevel = :suspicious OR riskLevel = :fraudulent")
                    .expressionValues(expressionValues)
                    .build();

            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                    .filterExpression(filterExpression)
                    .build();

            // Scan with filter - DynamoDB only returns matching items
            PageIterable<ScoredTransaction> pages = scoredTransactionTable.scan(scanRequest);
            pages.items().forEach(flaggedTransactions::add);

            return flaggedTransactions;
        } catch (Exception e) {
            log.error("Failed to retrieve flagged transactions from DynamoDB", e);
            return new ArrayList<>();
        }
    }
}
