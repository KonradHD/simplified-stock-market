package com.stockmarket.stockmarket_core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmarket.stockmarket_core.dto.StockDTO;
import static com.stockmarket.stockmarket_core.dto.StockDTO.createStockDTO;
import com.stockmarket.stockmarket_core.exception.StockNotFoundException;
import com.stockmarket.stockmarket_core.model.Bank;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.repository.BankRepository;
import com.stockmarket.stockmarket_core.repository.StockRepository;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {
    
    @Mock
    private BankRepository bankRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private BankService bankService;

    @Captor
    private ArgumentCaptor<List<Stock>> stocksCaptor;

    @Test
    public void availabilityCheckThrowStockNotFoundExceptionTest(){
        String symbol = "AMAZ";
        when(bankRepository.findById(symbol)).thenReturn(Optional.empty());

        StockNotFoundException e = assertThrows(
            StockNotFoundException.class,
            () -> bankService.checkStockAvailability(symbol, 20)
        );
        assertEquals("The stock with symbol: %s is unavailable in the Bank currently".formatted(symbol), e.getMessage());
    }

    @Test
    public void availabilityCheckSuccessfullTest(){
        String symbol = "AMAZ";
        Bank bankStock = new Bank();
        bankStock.setQuantity(20);
        when(bankRepository.findById(symbol)).thenReturn(Optional.of(bankStock));

        assertFalse(bankService.checkStockAvailability(symbol, 24));
        assertTrue(bankService.checkStockAvailability(symbol, 20));
    }

    @Test
    public void getEmptyInventoryTest(){
        when(bankRepository.findAll()).thenReturn(List.of());
        assertEquals(Integer.valueOf(0), bankService.getInventory().size());
    }

    @Test
    public void getInventoryListTest(){
        List<Bank> stocks = List.of(
                Bank.builder()
                    .stockSymbol("GOOG")
                    .quantity(12)
                    .build(),
                Bank.builder()
                    .stockSymbol("AMAZ")
                    .quantity(20)
                    .build()
            );
        when(bankRepository.findAll()).thenReturn(stocks);
        var inventory = bankService.getInventory();

        assertEquals(2, inventory.size());
        
        assertEquals("GOOG", inventory.get(0).name()); 
        assertEquals(12, inventory.get(0).quantity());
        
        assertEquals("AMAZ", inventory.get(1).name());
        assertEquals(20, inventory.get(1).quantity());
    }


    @Test
    public void updatingBankQuantityTest(){
        List<StockDTO> incomingStocks = List.of(
            createStockDTO("GOOG", 12),
            createStockDTO("AMAZ", 9)
        );
        List<String> symbols = incomingStocks.stream()  
                                .map(StockDTO::name)
                                .toList();
        Stock googStock = Stock.builder()
                            .symbol("GOOG")
                            .build();
        Stock amazStock = Stock.builder()
                            .symbol("AMAZ")
                            .build();

        when(stockRepository.findAllById(symbols)).thenReturn(List.of(
            googStock,
            amazStock
        ));
        
        Bank targetGoogBank = Bank.builder()
                .stockSymbol("GOOG")
                .quantity(20)
                .build();
        Bank targetAmazBank = Bank.builder()
                .stockSymbol("AMAZ")
                .quantity(20)
                .build();
        when(bankRepository.findById("GOOG")).thenReturn(Optional.of(targetGoogBank));
        when(bankRepository.findById("AMAZ")).thenReturn(Optional.of(targetAmazBank));
        bankService.initBankState(incomingStocks);

        assertEquals(12, targetGoogBank.getQuantity());
        assertEquals(9, targetAmazBank.getQuantity());
        verify(stockRepository, never()).saveAll(any());
    }

    @Test
    public void bankInitializationThrowStockNotFoundExceptionArgumentTest(){
        String symbol = "AMAZ";
        StockDTO incomingStock = new StockDTO(symbol, 20); 
        List<StockDTO> requestList = List.of(incomingStock);

        Stock existingStock = Stock.builder().symbol(symbol).build();
        when(stockRepository.findAllById(List.of(symbol))).thenReturn(List.of(existingStock));

        when(bankRepository.findById(symbol)).thenReturn(Optional.empty());

        StockNotFoundException exception = assertThrows(
            StockNotFoundException.class, 
            () -> bankService.initBankState(requestList)
        );
        assertEquals("Trigger was not executed: %s".formatted(symbol), exception.getMessage());
    }

    @Test 
    public void savingNewStocksAndUpdatingBankQuantityTest(){
        List<StockDTO> incomingStocks = List.of(
            createStockDTO("GOOG", 12),
            createStockDTO("AMAZ", 9)
        );
        List<String> symbols = incomingStocks.stream()  
                                .map(StockDTO::name)
                                .toList();

        when(stockRepository.findAllById(symbols)).thenReturn(List.of());

        Bank targetGoogBank = Bank.builder()
            .stockSymbol("GOOG")
            .quantity(20)
            .build();
        Bank targetAmazBank = Bank.builder()
            .stockSymbol("AMAZ")
            .quantity(20)
            .build();
        
        when(bankRepository.findById("GOOG")).thenReturn(Optional.of(targetGoogBank));
        when(bankRepository.findById("AMAZ")).thenReturn(Optional.of(targetAmazBank));
        bankService.initBankState(incomingStocks);
        
        verify(stockRepository).saveAll(stocksCaptor.capture());
        List<Stock> saveStocks = stocksCaptor.getValue();
        
        // Saving stocks tests
        assertEquals(2, saveStocks.size());
        assertEquals("GOOG", saveStocks.get(0).getSymbol());
        assertEquals(BigDecimal.valueOf(1), saveStocks.get(0).getPrice());
        assertEquals("AMAZ", saveStocks.get(1).getSymbol());

        // Updating quantity tests
        assertEquals(12, targetGoogBank.getQuantity());
        assertEquals(9, targetAmazBank.getQuantity());
    }
}
