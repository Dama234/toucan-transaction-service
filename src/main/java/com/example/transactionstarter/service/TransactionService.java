package com.example.transactionstarter.service;

import com.example.transactionstarter.exception.DuplicateTransactionException;
import com.example.transactionstarter.model.Transaction;
import com.example.transactionstarter.model.TransactionStatus;
import com.example.transactionstarter.model.TransactionType;
import com.example.transactionstarter.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// This class contains the business logic for transactions
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Create a new transaction
    @Transactional
    public Transaction createTransaction(String id, String customerId, double amount,
                                         String currency, TransactionType type) {
        // If client gives us an ID, use it; otherwise generate a new one
        String finalId = (id != null && !id.isBlank()) ? id : UUID.randomUUID().toString();

        // Reject if this ID already exists in the database
        if (transactionRepository.existsById(finalId)) {
            throw new DuplicateTransactionException(finalId);
        }

        // Create the transaction object
        Transaction transaction = new Transaction(
                customerId,
                BigDecimal.valueOf(amount),
                currency.toUpperCase(),
                type
        );
        transaction.setId(finalId);
        return transactionRepository.save(transaction);
    }

    // Get a single transaction by ID
    public Transaction getTransaction(String id) {
        return transactionRepository.findById(id).orElse(null);
    }

    // Change the status of a transaction
    @Transactional
    public Transaction updateStatus(String id, TransactionStatus newStatus) {
        // Find the transaction first
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Transaction not found with id: " + id));

        TransactionStatus currentStatus = transaction.getStatus();

        // Only PENDING transactions can change status
        if (currentStatus == TransactionStatus.PENDING) {
            transaction.setStatus(newStatus);
            return transactionRepository.save(transaction);
        }

        // If already in the same status, do nothing
        if (currentStatus == newStatus) {
            return transaction;
        }

        // Cannot change status once it's COMPLETED, FAILED, or CANCELLED
        throw new IllegalStateException(
                "Invalid status transition from " + currentStatus + " to " + newStatus);
    }

    // Get all transactions for a customer
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionRepository.findByCustomerIdOrderByIdDesc(customerId);
    }
}
