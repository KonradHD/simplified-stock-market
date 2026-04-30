package com.stockmarket.stockmarket_core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockmarket.stockmarket_core.dto.StockDTO;
import static com.stockmarket.stockmarket_core.dto.StockDTO.createStockDTO;
import com.stockmarket.stockmarket_core.exception.StockNotFoundException;
import static com.stockmarket.stockmarket_core.exception.StockNotFoundException.stockNotFoundException;
import com.stockmarket.stockmarket_core.model.Bank;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.repository.BankRepository;
import com.stockmarket.stockmarket_core.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankService {
    private final BankRepository bankRepository;
    private final StockRepository stockRepository;

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

    @Transactional
    public void initBankState(List<StockDTO> stocksDTO){
        List<String> incomingSymbols = stocksDTO.stream()
                .map(StockDTO::name)
                .toList();

        List<String> existingSymbols = stockRepository.findAllById(incomingSymbols).stream()
                .map(Stock::getSymbol)
                .toList();

        List<StockDTO> newStocksDTO = stocksDTO.stream()
                .filter(dto -> !existingSymbols.contains(dto.name()))
                .toList();

        if (!newStocksDTO.isEmpty()) {
                List<Stock> stocksToSave = newStocksDTO.stream()
                    .map(dto -> Stock.builder()
                            .symbol(dto.name())
                            .build())
                            .toList();
                    stockRepository.saveAll(stocksToSave);
                    stockRepository.flush();
        }else{
           log.info("Every stock has been added already. Updating quantity...");
        }

        for (StockDTO dto : stocksDTO) {
            Bank inventory = bankRepository.findById(dto.name())
                    .orElseThrow(() -> new StockNotFoundException("Trigger was not executed: %s".formatted(dto.name())));
            
            inventory.setQuantity(dto.quantity());
        }
    }
}
