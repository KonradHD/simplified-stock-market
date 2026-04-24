package com.stockmarket.stockmarket_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmarket.stockmarket_core.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long>{
    
}
