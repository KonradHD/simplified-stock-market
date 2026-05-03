package com.stockmarket.stockmarket_core.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stockmarket.stockmarket_core.exception.NotEnoughResourcesException;
import com.stockmarket.stockmarket_core.exception.StockNotFoundException;
import com.stockmarket.stockmarket_core.exception.WalletNotFoundException;
import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Bank;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.model.Transaction;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;
import com.stockmarket.stockmarket_core.repository.AuditLogRepository;
import com.stockmarket.stockmarket_core.repository.BankRepository;
import com.stockmarket.stockmarket_core.repository.TransactionRepository;
import com.stockmarket.stockmarket_core.repository.WalletInventoryRepository;
import com.stockmarket.stockmarket_core.repository.WalletRepository;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.TransactionStatus;

@ExtendWith(MockitoExtension.class)
public class TradingServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AuditLogRepository logRepository;

    @Mock
    private WalletInventoryRepository walletInventoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private AuditLogService logService;

    @InjectMocks
    private TradingService tradingService;

    @Captor
    private ArgumentCaptor<WalletInventory> walletInventoryCaptor;

    @Captor
    private ArgumentCaptor<AuditLog> logCaptor;

    @Captor 
    private ArgumentCaptor<Transaction> transactionCaptor;


    @Nested
    class BuyStockTests {

        @Test
        public void throwWalletNotFoundExceptionTest(){
            Long walletId = 1L;
            String symbol = "TEST";
            Integer quantity = 5;

            when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
            WalletNotFoundException e = assertThrows(
                WalletNotFoundException.class,
                () -> tradingService.buyStock(walletId, symbol, quantity)
            );
            assertEquals("The wallet with id: %s does not exist or is inactive.".formatted(walletId), e.getMessage());
            verifyNoInteractions(walletInventoryRepository, transactionRepository, bankRepository, logRepository);
        }

        @Test
        public void throwStockNotFoundExceptionTest(){
            Long walletId = 1L;
            String symbol = "TEST";
            Integer quantity = 5;
        
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.empty());
        
            StockNotFoundException e = assertThrows(
                StockNotFoundException.class,
                () -> tradingService.buyStock(walletId, symbol, quantity)
            );
            assertEquals("The stock with symbol: %s is unavailable in the Bank currently".formatted(symbol), e.getMessage());
            verify(logService).logWarning(
                eq(walletId), isNull(), eq(LogActionType.TRANSACTION_BUY), anyString(), eq(quantity)
            );
            verifyNoInteractions(transactionRepository, walletInventoryRepository);
        }


        @Test 
        public void throwNotEnoughResourcesException(){
            Long walletId = 1L;
            String symbol = "GOOG";
            Integer buyingQuantity = 100;

            when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
            
            Bank mockBank = Bank.builder().quantity(10).build();
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.of(mockBank));

            NotEnoughResourcesException e = assertThrows(
                NotEnoughResourcesException.class,
                () -> tradingService.buyStock(walletId, symbol, buyingQuantity)
            );

            assertEquals("Not enough existing resources: Only %d of %s is left".formatted(10, symbol), e.getMessage());
            verify(logService).logWarning(
                eq(walletId), eq(symbol), eq(LogActionType.TRANSACTION_BUY), anyString(), eq(buyingQuantity)
            );
            verifyNoInteractions(transactionRepository, walletInventoryRepository);
        }

        @Test
        public void successNewInventoryTest(){
            Long walletId = 1L;
            String symbol = "GOOG";
            Integer buyingQuantity = 5;

            Wallet mockWallet = new Wallet();
            mockWallet.setId(walletId);

            Stock mockStock = Stock.builder().symbol(symbol).build();
            Bank mockBank = Bank.builder().stockSymbol(symbol).stock(mockStock).quantity(20).build();

            when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.of(mockBank));
            
            WalletInventoryId expectedInventoryId = new WalletInventoryId(walletId, symbol);
            when(walletInventoryRepository.findById(expectedInventoryId)).thenReturn(Optional.empty());

            tradingService.buyStock(walletId, symbol, buyingQuantity);

            verify(walletInventoryRepository).save(walletInventoryCaptor.capture());
            WalletInventory inventory = walletInventoryCaptor.getValue();

            verify(transactionRepository).save(transactionCaptor.capture());
            Transaction transaction = transactionCaptor.getValue();

            verify(logRepository).save(logCaptor.capture());
            AuditLog auditLog = logCaptor.getValue();
    
            assertEquals(buyingQuantity, inventory.getQuantity());      
            assertEquals(15, mockBank.getQuantity());
            assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
            assertEquals(transaction.getStock(), mockStock);
            assertEquals(LogActionType.TRANSACTION_BUY, auditLog.getActionType());
            assertEquals(mockWallet, auditLog.getWallet());
        }

        @Test 
        public void successUpdatingInventoryTest(){
            Long walletId = 1L;
            String symbol = "GOOG";
            Integer quantityToBuy = 5;

            Wallet mockWallet = new Wallet();
            mockWallet.setId(walletId);

            Stock mockStock = Stock.builder().symbol(symbol).build();
            Bank mockBank = Bank.builder().stockSymbol(symbol).stock(mockStock).quantity(20).build();

            WalletInventory existingInventory = WalletInventory.builder().quantity(10).build();

            when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.of(mockBank));
            when(walletInventoryRepository.findById(any(WalletInventoryId.class))).thenReturn(Optional.of(existingInventory));

            tradingService.buyStock(walletId, symbol, quantityToBuy);

            verify(transactionRepository).save(transactionCaptor.capture());
            Transaction transaction = transactionCaptor.getValue();

            verify(logRepository).save(logCaptor.capture());
            AuditLog auditLog = logCaptor.getValue();

            assertEquals(15, mockBank.getQuantity());
            assertEquals(15, existingInventory.getQuantity());
            assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
            assertEquals(transaction.getStock(), mockStock);
            assertEquals(LogActionType.TRANSACTION_BUY, auditLog.getActionType());
            assertEquals(mockWallet, auditLog.getWallet());
        }
    }


    @Nested
    class SellStockTests {

        @Test
        public void throwWalletNotFoundExceptionTest(){
            Long walletId = 1L;
            String symbol = "TEST";
            Integer quantity = 5;

            when(walletRepository.findById(walletId)).thenReturn(Optional.empty());
            WalletNotFoundException e = assertThrows(
                WalletNotFoundException.class,
                () -> tradingService.sellStock(walletId, symbol, quantity)
            );
            assertEquals("The wallet with id: %s does not exist or is inactive.".formatted(walletId), e.getMessage());
            verifyNoInteractions(walletInventoryRepository, transactionRepository, bankRepository, logRepository);
        }

        @Test
        public void throwStockNotFoundExceptionTest(){
            Long walletId = 1L;
            String symbol = "TEST";
            Integer quantity = 5;
        
            when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.empty());
        
            StockNotFoundException e = assertThrows(
                StockNotFoundException.class,
                () -> tradingService.sellStock(walletId, symbol, quantity)
            );
            assertEquals("The stock with symbol: %s is unavailable in the Bank currently".formatted(symbol), e.getMessage());
            verify(logService).logWarning(
                eq(walletId), isNull(), eq(LogActionType.TRANSACTION_SELL), anyString(), eq(quantity)
            );
            verifyNoInteractions(transactionRepository, walletInventoryRepository);
        }


        @Test 
        public void throwNotEnoughResourcesWithNoInventoryException(){
            Long walletId = 1L;
            String symbol = "GOOG";
            Integer sellingQuantity = 100;

            when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.of(new Bank()));

            NotEnoughResourcesException e = assertThrows(
                NotEnoughResourcesException.class,
                () -> tradingService.sellStock(walletId, symbol, sellingQuantity)
            );
            assertEquals("Not enough existing resources: Wallet %s stock quantity equals 0.".formatted(symbol), e.getMessage());
            verify(logService).logWarning(
                eq(walletId), eq(symbol), eq(LogActionType.TRANSACTION_SELL), anyString(), eq(sellingQuantity)
            );
            verifyNoInteractions(transactionRepository);
        }

        @Test 
        public void throwNotEnoughResourcesWithExistingInventoryException(){
            Long walletId = 1L;
            String symbol = "GOOG";
            Integer sellingQuantity = 100;

            when(walletRepository.findById(walletId)).thenReturn(Optional.of(new Wallet()));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.of(new Bank()));
            WalletInventoryId invId = new WalletInventoryId(walletId, symbol);
            WalletInventory inventory = WalletInventory.builder()
                                                .id(invId)
                                                .quantity(12)
                                                .build();

            when(walletInventoryRepository.findById(invId)).thenReturn(Optional.of(inventory));
            NotEnoughResourcesException e = assertThrows(
                NotEnoughResourcesException.class,
                () -> tradingService.sellStock(walletId, symbol, sellingQuantity)
            );
            assertEquals("Not enough existing resources: Wallet %s stock quantity equals %d.".formatted(symbol, 12), e.getMessage());
            verify(logService).logWarning(
                eq(walletId), eq(symbol), eq(LogActionType.TRANSACTION_SELL), anyString(), eq(sellingQuantity)
            );
            verifyNoInteractions(transactionRepository);
        }


        @Test 
        public void successUpdatingInventoryTest(){
            Long walletId = 1L;
            String symbol = "GOOG";
            Integer quantityToSell = 5;

            Wallet mockWallet = new Wallet();
            mockWallet.setId(walletId);

            Stock mockStock = Stock.builder().symbol(symbol).build();
            Bank mockBank = Bank.builder()
                                .stockSymbol(symbol)
                                .stock(mockStock)
                                .quantity(100)
                                .build();

            WalletInventory existingInventory = WalletInventory.builder().quantity(10).build();

            when(walletRepository.findById(walletId)).thenReturn(Optional.of(mockWallet));
            when(bankRepository.findByIdLocked(symbol)).thenReturn(Optional.of(mockBank));
            when(walletInventoryRepository.findById(any(WalletInventoryId.class))).thenReturn(Optional.of(existingInventory));

            tradingService.sellStock(walletId, symbol, quantityToSell);

            verify(transactionRepository).save(transactionCaptor.capture());
            Transaction transaction = transactionCaptor.getValue();

            verify(logRepository).save(logCaptor.capture());
            AuditLog auditLog = logCaptor.getValue();

            assertEquals(105, mockBank.getQuantity());
            assertEquals(5, existingInventory.getQuantity());
            assertEquals(TransactionStatus.SUCCESS, transaction.getStatus());
            assertEquals(transaction.getStock(), mockStock);
            assertEquals(LogActionType.TRANSACTION_SELL, auditLog.getActionType());
            assertEquals(mockWallet, auditLog.getWallet());
        }

    }

}
