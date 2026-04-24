package com.stockmarket.stockmarket_core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stockmarket.stockmarket_core.model.Bank;

import jakarta.persistence.LockModeType;

public interface BankRepository extends JpaRepository<Bank, String> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bank b JOIN FETCH b.stock WHERE b.stockSymbol = :symbol")
    Optional<Bank> findByIdLocked(@Param("symbol") String symbol);
}
