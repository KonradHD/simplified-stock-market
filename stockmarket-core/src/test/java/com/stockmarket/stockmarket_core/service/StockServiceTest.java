package com.stockmarket.stockmarket_core.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmarket.stockmarket_core.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {
    
    @Mock 
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    public void stockExistingTest(){
        String symbol = "GOOG";
        when(stockRepository.existsById(symbol)).thenReturn(true);

        assertTrue(stockService.stockExists(symbol));
    }

    @Test
    public void stockNonExistingTest(){
        String symbol = "GOOG";
        when(stockRepository.existsById(symbol)).thenReturn(false);

        assertFalse(stockService.stockExists(symbol));
    }
}
