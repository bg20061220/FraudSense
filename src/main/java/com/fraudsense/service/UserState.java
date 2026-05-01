package com.fraudsense.service;

import com.fraudsense.model.Transaction;
import java.util.* ; 

public class UserState {
       private String customerId ; 
       private List<Transaction> recentTransactions ; 
       private String lastCountry ; 
       private long lastTransactionTime ;     

       public UserState(String customerId) {
        this.customerId = customerId ; 
        this.recentTransactions = new LinkedList<>() ; 
        this.lastCountry = null  ; 
        this.lastTransactionTime = 0 ; 
       }

       public void addTransaction(Transaction tx){
        recentTransactions.add(tx) ; 
        this.lastCountry = tx.getCountry() ; 
        this.lastTransactionTime = tx.getTimestamp() ; 
       }

       public List<Transaction> getTransactionsInWindow(long windowMs){
        long now = System.currentTimeMillis() ; 
        return recentTransactions.stream()
               .filter(tx -> (now - tx.getTimestamp()) <= windowMs) 
               .toList() ; 
       }

       public double getAverageAmount(){
        if(recentTransactions.isEmpty()) return 0 ; 
        return recentTransactions.stream()
               .mapToLong(Transaction::getAmount)
               .average()
               .orElse(0) ; 
       }
        public String getLastCountry() { return lastCountry; }
        public long getLastTransactionTime() { return lastTransactionTime; }
        public String getCustomerId() { return customerId; }
}

