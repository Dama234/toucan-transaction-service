package com.example.transactionstarter.model;

// Status of a transaction through its lifecycle
// PENDING is the only status that can change
// COMPLETED, FAILED, and CANCELLED are final - they cannot change
public enum TransactionStatus {
    PENDING,    // Just created, not processed yet
    COMPLETED,  // Successfully done
    FAILED,     // Something went wrong
    CANCELLED   // Cancelled before processing
}
