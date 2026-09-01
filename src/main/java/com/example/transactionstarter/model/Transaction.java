package com.example.transactionstarter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// This class represents a transaction in our database
// @Entity tells Spring this maps to a database table called "transactions"
@Entity
@Table(name = "transactions")
public class Transaction {

    // Unique ID for each transaction
    @Id
    private String id;

    // Customer who owns this transaction
    @Column(nullable = false)
    @NotBlank(message = "customerId must not be blank")
    private String customerId;

    // How much money is involved
    @Column(nullable = false)
    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;

    // Currency code like USD, EUR, GBP
    @Column(nullable = false)
    @Size(min = 3, max = 3, message = "currency must be exactly 3 characters")
    private String currency;

    // DEBIT, CREDIT, or TRANSFER
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    // PENDING, COMPLETED, FAILED, or CANCELLED
    // New transactions always start as PENDING
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    // Default constructor needed by JPA
    public Transaction() {
    }

    // Constructor to create a new transaction
    public Transaction(String customerId, BigDecimal amount,
                       String currency, TransactionType transactionType) {
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
        this.status = TransactionStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
