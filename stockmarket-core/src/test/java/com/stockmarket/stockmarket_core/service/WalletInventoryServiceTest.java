package com.stockmarket.stockmarket_core.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmarket.stockmarket_core.dto.stock.StockDTO;
import com.stockmarket.stockmarket_core.exception.WalletNotFoundException;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;
import com.stockmarket.stockmarket_core.repository.WalletInventoryRepository;
import com.stockmarket.stockmarket_core.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
public class WalletInventoryServiceTest {

    @Mock
    private WalletInventoryRepository walletInventoryRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletInventoryService walletInventoryService;

    @Test
    public void shouldThrowWalletNotFoundExceptionTest(){
        Long nonExistentWalletId = 1L;
        when(walletRepository.findById(nonExistentWalletId)).thenReturn(Optional.empty());

        WalletNotFoundException e = assertThrows(
            WalletNotFoundException.class, 
            () -> walletInventoryService.getWalletInventoriesDTO(nonExistentWalletId)
        );
        assertEquals(
            "The wallet with id: %s does not exist or is inactive.".formatted(nonExistentWalletId),
            e.getMessage()
        );
        verifyNoInteractions(walletInventoryRepository);
    }   


    @Test
    public void shouldReturnStockDTOsTest(){
        Long existingWalletId = 1L;
        WalletInventory mockGoogle = WalletInventory.builder()
                                                .stock(Stock.builder().symbol("GOOG").name("Google").build())
                                                .quantity(3)
                                                .build();
         WalletInventory mockAmazon = WalletInventory.builder()
                                                .stock(Stock.builder().symbol("AMAZ").name("Amazon").build())
                                                .quantity(6)
                                                .build();
        WalletInventory mockNvidia = WalletInventory.builder()
                                                .stock(Stock.builder().symbol("NVID").name("Nvidia").build())
                                                .quantity(2)
                                                .build();

        List<WalletInventory> inventories = List.of(mockGoogle, mockAmazon, mockNvidia); 

        when(walletRepository.findById(existingWalletId)).thenReturn(Optional.of(new Wallet()));
        when(walletInventoryRepository.findAllByWalletId(existingWalletId)).thenReturn(inventories);
        
        List<StockDTO> testDTOs = walletInventoryService.getWalletInventoriesDTO(existingWalletId);
        
        assertEquals(3, testDTOs.size());
        
        assertEquals("GOOG", testDTOs.get(0).name());
        assertEquals(3, testDTOs.get(0).quantity());

        assertEquals("NVID", testDTOs.get(2).name());
        assertEquals(2, testDTOs.get(2).quantity());

        int totalQuantity = testDTOs.stream()
                        .mapToInt(StockDTO::quantity)
                        .sum();
        assertEquals(11, totalQuantity);
    }

    @Test
    public void shouldReturnZeroQuantityTest(){
        Long walletId = 1l;
        String symbol = "GOOG";
        WalletInventoryId expectedId = new WalletInventoryId(walletId, symbol);
        
        when(walletInventoryRepository.findById(expectedId)).thenReturn(Optional.empty());
        Integer quantity = walletInventoryService.getInventoryQuantity(walletId, symbol);

        assertEquals(0, quantity);
    }

    @Test
    public void shouldReturnActualQuantityTest(){
        Long walletId = 1l;
        String symbol = "GOOG";
        WalletInventoryId expectedId = new WalletInventoryId(walletId, symbol);
        WalletInventory mockInventory = WalletInventory.builder()
                                                .quantity(20)
                                                .build();
        
        when(walletInventoryRepository.findById(expectedId)).thenReturn(Optional.of(mockInventory));
        Integer quantity = walletInventoryService.getInventoryQuantity(walletId, symbol);

        assertEquals(20, quantity);
    }
}
