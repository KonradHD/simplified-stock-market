package com.stockmarket.stockmarket_core.service;

import org.springframework.stereotype.Service;

import com.stockmarket.stockmarket_core.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public boolean stockExists(String symbol){
        return stockRepository.existsById(symbol);
    }
}
