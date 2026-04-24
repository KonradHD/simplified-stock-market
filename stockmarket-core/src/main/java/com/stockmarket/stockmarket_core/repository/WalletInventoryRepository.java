package com.stockmarket.stockmarket_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;

public interface WalletInventoryRepository extends JpaRepository<WalletInventory, WalletInventoryId>{
    
}
