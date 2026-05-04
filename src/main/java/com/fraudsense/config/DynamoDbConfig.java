package com.fraudsense.config;

import com.fraudsense.model.ScoredTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import jakarta.annotation.PostConstruct;

import java.net.URI;

@Configuration
public class DynamoDbConfig {
    private static final Logger log = LoggerFactory.getLogger(DynamoDbConfig.class);

    @Value("${aws.dynamodb.endpoint:http://localhost:8000}")
    private String dynamoDbEndpoint;

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.dynamodb.table:scored-transactions}")
    private String tableName;

    private DynamoDbClient dynamoDbClient;
    private DynamoDbEnhancedClient enhancedClient;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbEndpoint))
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fake", "fake")))
                .build();
        return dynamoDbClient;
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        return enhancedClient;
    }

    @Bean
    public DynamoDbTable<ScoredTransaction> scoredTransactionTable(DynamoDbEnhancedClient enhancedClient) {
        return enhancedClient.table(tableName, TableSchema.fromClass(ScoredTransaction.class));
    }

    @PostConstruct
    public void createTable() {
        try {
            DynamoDbTable<ScoredTransaction> table = enhancedClient.table(tableName,
                    TableSchema.fromClass(ScoredTransaction.class));

            // Create table with composite key: customerId (PK) + timestamp (SK)
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(table.tableSchema().keyAttributes())
                    .attributeDefinitions(table.tableSchema().allAttributes())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

            dynamoDbClient.createTable(createTableRequest);
            log.info("Created DynamoDB table: {} with PK=customerId, SK=timestamp", tableName);
        } catch (ResourceInUseException e) {
            log.info("Table {} already exists", tableName);
        } catch (Exception e) {
            log.error("Failed to create DynamoDB table: {}", tableName, e);
        }
    }
}
