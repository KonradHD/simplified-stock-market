package com.stockmarket.stockmarket_core.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import static com.stockmarket.stockmarket_core.exception.NotEnoughResourcesException.notEnoughResourcesException;
import static com.stockmarket.stockmarket_core.exception.StockNotFoundException.stockNotFoundException;
import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Bank;
import com.stockmarket.stockmarket_core.model.Transaction;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;
import com.stockmarket.stockmarket_core.repository.AuditLogRepository;
import com.stockmarket.stockmarket_core.repository.BankRepository;
import com.stockmarket.stockmarket_core.repository.TransactionRepository;
import com.stockmarket.stockmarket_core.repository.WalletInventoryRepository;
import com.stockmarket.stockmarket_core.repository.WalletRepository;
import com.stockmarket.stockmarket_core.utils.types.Action;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;
import com.stockmarket.stockmarket_core.utils.types.TransactionStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingService {
    private final WalletRepository walletRepository;
    private final AuditLogRepository logRepository;
    private final WalletInventoryRepository walletInventoryRepository;
    private final TransactionRepository transactionRepository;
    private final BankRepository bankRepository;

    @Transactional
    public void buyStock(Long walletId, String symbol, Integer quantity){
        log.info("Starting buying transaction for {}", symbol);

        Wallet wallet = walletRepository.findById(walletId)
        .orElseThrow(() -> new IllegalArgumentException("Wallet: %s does not exist".formatted(walletId)));
        
        Bank bankStock = bankRepository.findByIdLocked(symbol)
                    .orElseThrow(() -> stockNotFoundException(symbol));

        Integer stockQuantity = bankStock.getQuantity();
        if(stockQuantity < quantity){
            log.warn("Transaction was rejected: not enough bank resources. Claimed: {}, available: {}", quantity, stockQuantity);
            throw notEnoughResourcesException("Only %d of %s is left".formatted(stockQuantity, symbol));
        }

        bankStock.setQuantity(stockQuantity - quantity);

        WalletInventoryId inventoryId = new WalletInventoryId(
            walletId,
            symbol
        );
        Optional<WalletInventory> walletInventory = walletInventoryRepository.findById(inventoryId);
        if(walletInventory.isPresent()){
            WalletInventory existingInventory = walletInventory.get();
            existingInventory.setQuantity(existingInventory.getQuantity() + quantity);
        }
        else{
            WalletInventory newWalletInventory = WalletInventory.builder()
                                                    .id(inventoryId)
                                                    .wallet(wallet)
                                                    .stock(bankStock.getStock())
                                                    .quantity(quantity)
                                                    .build();
            walletInventoryRepository.save(newWalletInventory);
        }

        Transaction transaction = Transaction.builder()
                                    .wallet(wallet)
                                    .stock(bankStock.getStock())
                                    .action(Action.BUY)
                                    .status(TransactionStatus.SUCCESS)
                                    .quantity(quantity)
                                    .build();
        transactionRepository.save(transaction);

        AuditLog auditLog = AuditLog.builder()
                        .wallet(wallet)
                        .stock(bankStock.getStock())
                        .actionType(LogActionType.TRANSACTION_BUY)
                        .status(LogStatus.INFO)
                        .info("Bank quantity and wallet inventory were updated successfully, transaction saved")
                        .quantity(quantity)
                        .build();
        logRepository.save(auditLog);
        log.info("Buying process finished successfully");
    } 


    @Transactional
    public void sellStock(Long walletId, String symbol, Integer quantity){
        log.info("Starting selling transaction for {}", symbol);

        Wallet wallet = walletRepository.findById(walletId)
                            .orElseThrow(() -> new IllegalArgumentException("Wallet: %s does not exist".formatted(walletId)));
        
        Bank bankStock = bankRepository.findByIdLocked(symbol)
                            .orElseThrow(() -> stockNotFoundException(symbol));

        WalletInventoryId inventoryId = new WalletInventoryId(
            walletId,
            symbol
        );                 
        WalletInventory walletInventory = walletInventoryRepository.findById(inventoryId)
                            .orElseGet(() -> {
                                log.warn("Transaction was rejected: not enough wallet resources. Claimed: {}, available: 0", quantity);
                                throw notEnoughResourcesException("Wallet %s stock quantity equals 0.".formatted(symbol)); 
                            });
        
        Integer walletQuantity = walletInventory.getQuantity();
        if(walletQuantity < quantity){
            log.warn("Transaction was rejected: not enough wallet resources. Claimed: {}, available: {}", quantity, walletQuantity);
            throw notEnoughResourcesException("Wallet %s stock quantity equals %d.".formatted(symbol, walletQuantity));
        }

        walletInventory.setQuantity(walletQuantity - quantity);
        bankStock.setQuantity(bankStock.getQuantity() + quantity);

        Transaction transaction = Transaction.builder()
                                    .wallet(wallet)
                                    .stock(bankStock.getStock())
                                    .action(Action.SELL)
                                    .status(TransactionStatus.SUCCESS)
                                    .quantity(quantity)
                                    .build();
        transactionRepository.save(transaction);

        AuditLog auditLog = AuditLog.builder()
                        .wallet(wallet)
                        .stock(bankStock.getStock())
                        .actionType(LogActionType.TRANSACTION_SELL)
                        .status(LogStatus.INFO)
                        .info("Bank quantity and wallet inventory were updated successfully, transaction saved")
                        .quantity(quantity)
                        .build();
        logRepository.save(auditLog);
        log.info("Buying process finished successfully");

    }

}
