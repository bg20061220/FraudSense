package com.fraudsense.service;

import com.fraudsense.model.Transaction;
import com.fraudsense.model.ScoredTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;


@Service
public class ScoringEngine {
    private static final Logger log = LoggerFactory.getLogger(ScoringEngine.class) ;
    private static final long VELOCITY_WINDOW_MS = 10 * 60 * 1000 ; // 10 minutes
    private static final long GEO_WINDOW_MS = 30*60*1000 ; // 30 minutes
    private static final int VELOCITY_THRESHOLD = 5;
    private static final double SPIKE_MULTIPLIER = 3.0 ;
    private static final Duration STATE_TTL = Duration.ofHours(24);
    private static final String KEY_PREFIX = "user:";

    @Autowired
    private RedisTemplate<String, UserState> redisTemplate; 

    public ScoredTransaction score(Transaction tx){
        String key = KEY_PREFIX + tx.getCustomerId() + ":state";
        UserState state = redisTemplate.opsForValue().get(key);
        if (state == null) {
            state = new UserState(tx.getCustomerId());
        }

        List<String> flags = new ArrayList<>() ;
        int riskScore = 0 ;

        // Velocity check (pass current tx to count it explicitly without adding to state yet)
        if(checkVelocity(tx, state)){
            flags.add("velocity") ;
            riskScore += 35 ;
        }

          // Check 2: Amount Spike
          if (checkAmountSpike(tx, state)) {
              flags.add("amountSpike");
              riskScore += 30;
          }

          // Check 3: Geo Anomaly (check BEFORE adding current tx so lastCountry is previous country)
          if (checkGeoAnomaly(tx, state)) {
              flags.add("geoAnomaly");
              riskScore += 40;
          }

          // Add transaction to user state AFTER checks
          state.addTransaction(tx);

          // Save state back to Redis with 24-hour TTL
          redisTemplate.opsForValue().set(key, state, STATE_TTL);

          // Check 4: High-Risk Merchant
          if (checkHighRiskMerchant(tx)) {
              flags.add("highRiskMerchant");
              riskScore += 20;
          }

          String riskLevel = determineRiskLevel(riskScore); 
          log.info("Scored transaction: id={}, customerId={}, riskScore={}, riskLevel={}, flags={}",
                   tx.getId(), tx.getCustomerId(), riskScore, riskLevel, flags)  ; 

          return new ScoredTransaction(
              tx.getId(),
              tx.getAmount(),
              tx.getCurrency(),
              tx.getCustomerId(),
              tx.getStatus(),
              tx.getTimestamp(),
              tx.getMerchantCategory(),
              tx.getCountry(),
              riskScore,
              riskLevel,
              flags
          );
    }

    private boolean checkVelocity(Transaction currentTx, UserState state) {
        List<Transaction> recentTxs = state.getTransactionsInWindow(VELOCITY_WINDOW_MS);
        // Include current transaction in the count (it's not in state yet)
        return (recentTxs.size() + 1) >= VELOCITY_THRESHOLD;
    }

    private boolean checkAmountSpike(Transaction tx, UserState state) {
        double avgAmount = state.getAverageAmount();
        return avgAmount > 0 && tx.getAmount() > (avgAmount * SPIKE_MULTIPLIER);
    }

    private boolean checkGeoAnomaly(Transaction tx, UserState state) {
        String lastCountry = state.getLastCountry();
        long timeSinceLastTx = System.currentTimeMillis() - state.getLastTransactionTime();

        if (lastCountry == null) return false;
        if (timeSinceLastTx > GEO_WINDOW_MS) return false;

        return !lastCountry.equals(tx.getCountry());
    }

    private boolean checkHighRiskMerchant(Transaction tx) {
        if (tx.getMerchantCategory() == null) return false;
        String category = tx.getMerchantCategory().toLowerCase();
        return category.contains("crypto") || category.contains("gambling") || category.contains("wire");
    }

    private String determineRiskLevel(int riskScore) {
        if (riskScore >= 70) return "FRAUDULENT";
        if (riskScore >= 40) return "SUSPICIOUS";
        return "CLEAN";
    }
}