package com.stockmarket.stockmarket_core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stockmarket.stockmarket_core.dto.StockDTO;
import static com.stockmarket.stockmarket_core.dto.StockDTO.createStockDTO;
import static com.stockmarket.stockmarket_core.exception.StockNotFoundException.stockNotFoundException;
import com.stockmarket.stockmarket_core.model.Bank;
import com.stockmarket.stockmarket_core.repository.BankRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankService {
    private final BankRepository bankRepository;

    public boolean checkStockAvailability(String symbol, Integer quantity){
        Bank bankStock = bankRepository.findById(symbol)
                    .orElseThrow(() -> stockNotFoundException(symbol));

        return bankStock.getQuantity() >= quantity;
    }

    public List<StockDTO> getInventory(){
        return bankRepository.findAll().stream()
                    .map(inv -> createStockDTO(inv.getStockSymbol(), inv.getQuantity()))
                    .collect(Collectors.toList());
    }
}
