package com.fraudsense.service;

import com.fraudsense.model.ScoredTransaction;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionHistoryService {

    private final int MAX_HISTORY = 20;
    private final List<ScoredTransaction> history = Collections.synchronizedList(new ArrayList<>());

    public void addTransaction(ScoredTransaction transaction) {
        history.add(0, transaction);
        if (history.size() > MAX_HISTORY) {
            history.remove(history.size() - 1);
        }
    }

    public List<ScoredTransaction> getRecentTransactions() {
        return new ArrayList<>(history);
    }

    public void clear() {
        history.clear();
    }
}
