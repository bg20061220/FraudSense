package com.fraudsense.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import java.util.List ;

@DynamoDbBean
public class ScoredTransaction {
    private String id ; 
    private long amount ; 
    private String currency ; 
    private String customerId ; 
    private String status ; 
    private long timestamp ; 
    private String merchantCategory ; 
    private String country ; 
    private int riskScore ; 
    private String riskLevel ;
    private List<String> flags ; 

    public ScoredTransaction() {} ; 
    public ScoredTransaction(String id , long amount , String currency , String customerId , String status , long timestamp , 
        String merchantCategory , String country , int riskScore , String riskLevel , List<String> flags
    ){
        this.id = id ; 
        this.amount = amount ; 
        this.currency = currency  ; 
        this.customerId = customerId ; 
        this.status = status ; 
        this.timestamp = timestamp ; 
        this.merchantCategory = merchantCategory ; 
        this.country  = country ; 
        this.riskScore = riskScore ; 
        this.riskLevel = riskLevel ; 
        this.flags = flags ; 
    }

      public String getId() { return id; }
      public void setId(String id) { this.id = id; }

      public long getAmount() { return amount; }
      public void setAmount(long amount) { this.amount = amount; }

      public String getCurrency() { return currency; }
      public void setCurrency(String currency) { this.currency = currency; }

      
      @DynamoDbPartitionKey
      public String getCustomerId() { return customerId; }
      public void setCustomerId(String customerId) { this.customerId = customerId; }

      public String getStatus() { return status; }
      public void setStatus(String status) { this.status = status; }

      @DynamoDbSortKey
      public long getTimestamp() { return timestamp; }
      public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

      public String getMerchantCategory() { return merchantCategory; }
      public void setMerchantCategory(String merchantCategory) { this.merchantCategory = merchantCategory; }

      public String getCountry() { return country; }
      public void setCountry(String country) { this.country = country; }

      public int getRiskScore() { return riskScore; }
      public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

      public String getRiskLevel() { return riskLevel; }
      public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

      public List<String> getFlags() { return flags; }
      public void setFlags(List<String> flags) { this.flags = flags; }

      @Override
      public String toString() {
          return "ScoredTransaction{id='" + id + "', customerId='" + customerId +
                 "', amount=" + amount + ", riskScore=" + riskScore + ", riskLevel='" + riskLevel +
                 "', flags=" + flags + ", timestamp=" + timestamp + "}";
      }
}
