package com.example.transactionstarter.dto;

import com.example.transactionstarter.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// This is the data we receive when someone creates a transaction
public class CreateTransactionRequest {

    // Optional - if not provided, server generates a UUID
    @Size(max = 64, message = "id must be at most 64 characters")
    private String id;

    // Must not be empty
    @NotBlank(message = "customerId must not be blank")
    private String customerId;

    // Must be greater than zero
    @Positive(message = "amount must be greater than zero")
    private double amount;

    // Must be exactly 3 letters like USD, EUR
    @Size(min = 3, max = 3, message = "currency must be exactly 3 characters")
    private String currency;

    // Must be DEBIT, CREDIT, or TRANSFER
    @NotNull(message = "type must not be null")
    private TransactionType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
