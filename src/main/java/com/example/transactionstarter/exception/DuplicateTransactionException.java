package com.example.transactionstarter.exception;

// Thrown when someone tries to create a transaction with an ID that already exists
public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String id) {
        super("Transaction with ID '" + id + "' already exists");
    }
}
