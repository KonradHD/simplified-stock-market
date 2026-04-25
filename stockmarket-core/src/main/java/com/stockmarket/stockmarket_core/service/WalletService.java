package com.stockmarket.stockmarket_core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse;
import static com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse.createWalletResponse;
import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.repository.AuditLogRepository;
import com.stockmarket.stockmarket_core.repository.WalletRepository;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final AuditLogRepository logRepository;


    public boolean walletExists(Long walletId){
        return walletRepository.existsById(walletId);
    }

    @Transactional
    public NewWalletResponse createWallet(){
        Wallet wallet = new Wallet();
        walletRepository.save(wallet);

        AuditLog auditLog = AuditLog.builder()
                                .wallet(wallet)
                                .actionType(LogActionType.WALLET_CREATE)
                                .status(LogStatus.INFO)
                                .message("New wallet was created")
                                .build();
        logRepository.save(auditLog);
        return createWalletResponse(wallet);
    }

    @Transactional
    public void deleteWallet(Long walletId){
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new IllegalArgumentException("Wallet: %s does not exist".formatted(walletId)));
        wallet.setIsActive(false);

        AuditLog auditLog = AuditLog.builder()
                                .wallet(wallet)
                                .actionType(LogActionType.WALLET_DELETE)
                                .status(LogStatus.INFO)
                                .message("Wallet was deleted")
                                .build();
        logRepository.save(auditLog);
    }
}
