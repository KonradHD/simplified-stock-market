package com.stockmarket.stockmarket_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmarket.stockmarket_core.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
}
