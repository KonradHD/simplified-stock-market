package com.stockmarket.stockmarket_core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.stockmarket.stockmarket_core.dto.log.LogDTO;
import static com.stockmarket.stockmarket_core.dto.log.LogDTO.createLogDTO;
import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.repository.AuditLogRepository;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository logRepository;

    public List<LogDTO> getLimitedLogsWithStatus(LogStatus status, Integer limit){
        List<AuditLog> recentLogs = logRepository.findByStatusOrderByCreatedAtDesc(
            status, 
            Limit.of(limit)
        ); 

        return recentLogs.stream()
                    .map(auditLog -> createLogDTO(
                                auditLog.getActionType(), 
                                auditLog.getWallet().getId(), 
                                auditLog.getStock().getSymbol()
                            ))
                    .collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(Long walletId, String symbol, LogActionType actionType, String errorMessage, Integer quantity){

        Wallet proxyWallet = new Wallet();
        proxyWallet.setId(walletId);
        Stock proxyStock = new Stock();
        proxyStock.setSymbol(symbol);

        AuditLog auditLog = AuditLog.builder()
                                .wallet(proxyWallet)
                                .stock(proxyStock)
                                .actionType(actionType)
                                .status(LogStatus.ERROR)
                                .message(errorMessage)
                                .quantity(quantity)
                                .build();
        logRepository.save(auditLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWarning(Long walletId, String symbol, LogActionType actionType, String errorMessage, Integer quantity){

        Wallet proxyWallet = new Wallet();
        proxyWallet.setId(walletId);
        Stock proxyStock = new Stock();
        proxyStock.setSymbol(symbol);

        AuditLog auditLog = AuditLog.builder()
                                .wallet(proxyWallet)
                                .stock(proxyStock)
                                .actionType(actionType)
                                .status(LogStatus.WARN)
                                .message(errorMessage)
                                .quantity(quantity)
                                .build();
        logRepository.save(auditLog);
    }
}
