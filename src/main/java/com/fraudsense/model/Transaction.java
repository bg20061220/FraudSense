package com.fraudsense.model;

public class Transaction {
    private String id;
    private long amount;
    private String currency;
    private String customerId;
    private String status;
    private long timestamp;
    
    public Transaction() {} ; 
    public Transaction(String id, long amount, String currency, String customerId, String status, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.customerId = customerId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public long getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCustomerId() { return customerId; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Transaction{id='" + id + "', amount=" + amount + ", currency='" + currency +
               "', customerId='" + customerId + "', status='" + status + "', timestamp=" + timestamp + "}";
    }
}
