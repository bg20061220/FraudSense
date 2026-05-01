package com.fraudsense.model;

import java.util.List ; 

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
      public long getAmount() { return amount; }
      public String getCurrency() { return currency; }
      public String getCustomerId() { return customerId; }
      public String getStatus() { return status; }
      public long getTimestamp() { return timestamp; }
      public String getMerchantCategory() { return merchantCategory; }
      public String getCountry() { return country; }
      public int getRiskScore() { return riskScore; }
      public String getRiskLevel() { return riskLevel; }
      public List<String> getFlags() { return flags; }

      @Override
      public String toString() {
          return "ScoredTransaction{id='" + id + "', customerId='" + customerId +
                 "', amount=" + amount + ", riskScore=" + riskScore + ", riskLevel='" + riskLevel +
                 "', flags=" + flags + ", timestamp=" + timestamp + "}";
      }
}
