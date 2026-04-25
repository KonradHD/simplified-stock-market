package com.stockmarket.stockmarket_core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;

public interface WalletInventoryRepository extends JpaRepository<WalletInventory, WalletInventoryId>{
    
    List<WalletInventory> findAllByWalletId(Long walletId);
}
