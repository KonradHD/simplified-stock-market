package com.stockmarket.stockmarket_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmarket.stockmarket_core.model.Stock;

public interface StockRepository extends JpaRepository<Stock, String>{
    
}
