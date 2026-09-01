package com.example.transactionstarter.controller;

import com.example.transactionstarter.dto.CreateTransactionRequest;
import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.model.TransactionStatus;
import com.example.transactionstarter.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This class handles HTTP requests for transactions
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // POST /api/transactions - Create a new transaction
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        try {
            Transaction transaction = transactionService.createTransaction(
                    request.getId(),
                    request.getCustomerId(),
                    request.getAmount(),
                    request.getCurrency(),
                    request.getType()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (DuplicateTransactionException e) {
            // Return 409 if ID already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // GET /api/transactions/{id} - Get a transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id) {
        Transaction transaction = transactionService.getTransaction(id);

        if (transaction == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transaction);
    }

    // PUT /api/transactions/{id}/status?status=COMPLETED - Update transaction status
    @PutMapping("/{id}/status")
    public ResponseEntity<Transaction> updateStatus(
            @PathVariable String id,
            @RequestParam TransactionStatus status) {

        try {
            Transaction transaction = transactionService.updateStatus(id, status);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            // Transaction not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            // Invalid status transition
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // GET /api/transactions/customer/{customerId} - Get all transactions for a customer
    @GetMapping("/customer/{customerId}")
    public List<Transaction> getTransactionsByCustomerId(
            @PathVariable String customerId) {
        return transactionService.getTransactionsByCustomerId(customerId);
    }
}
