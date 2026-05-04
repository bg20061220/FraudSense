package com.fraudsense.controller;

import com.fraudsense.model.ScoredTransaction;
import com.fraudsense.service.TransactionHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionHistoryController {

    private final TransactionHistoryService historyService;

    public TransactionHistoryController(TransactionHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/recent-transactions")
    public List<ScoredTransaction> getRecentTransactions() {
        return historyService.getRecentTransactions();
    }

    @PostMapping("/clear-history")
    public String clearHistory() {
        historyService.clear();
        return "History cleared";
    }
}
