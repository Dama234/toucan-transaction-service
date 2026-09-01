package com.example.transactionstarter.repository;

import com.example.transactionstarter.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// This interface lets us talk to the database
// Spring Data JPA creates the implementation automatically
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Check if a transaction with this ID already exists
    boolean existsById(String id);

    // Find all transactions for a specific customer, newest first
    List<Transaction> findByCustomerIdOrderByIdDesc(String customerId);
}
