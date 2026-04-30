package com.stockmarket.stockmarket_core.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;

import com.stockmarket.stockmarket_core.dto.log.LogDTO;
import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.repository.AuditLogRepository;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {
    
    @Mock
    private AuditLogRepository logRepository;

    @InjectMocks
    private AuditLogService logService;

    @Captor
    private ArgumentCaptor<AuditLog> logCaptor;

    @Test
    public void getEmptyLogsTest(){
        LogStatus status = LogStatus.INFO;
        Integer limit = 10;

        when(logRepository.findByStatusOrderByCreatedAtDesc(eq(status), eq(Limit.of(limit))))
                .thenReturn(List.of());

        List<LogDTO> logs = logService.getLimitedLogsWithStatus(status, limit);

        assertTrue(logs.isEmpty(), "Result list should be empty");
        verify(logRepository).findByStatusOrderByCreatedAtDesc(eq(status), eq(Limit.of(limit)));
    }

    @Test 
    public void getLogsListTest(){
        LogStatus status = LogStatus.ERROR;
        Integer limit = 5;

        Wallet mockWallet = new Wallet();
        mockWallet.setId(1L);
        Stock mockStock = Stock.builder().symbol("GOOG").build();

        AuditLog dummyLog = AuditLog.builder()
                .actionType(LogActionType.WALLET_CREATE)
                .wallet(mockWallet)
                .stock(mockStock)
                .build();

        when(logRepository.findByStatusOrderByCreatedAtDesc(eq(status), eq(Limit.of(limit))))
                .thenReturn(List.of(dummyLog));

        List<LogDTO> logs = logService.getLimitedLogsWithStatus(status, limit);

        assertEquals(1, logs.size(), "Result list should containt one object");
        
        assertEquals(LogActionType.WALLET_CREATE, logs.get(0).type());
        assertEquals(1L, logs.get(0).walletId());
        assertEquals("GOOG", logs.get(0).name());
    }

    @Test
    public void createCorrectLogErrorTest(){
        Long walletId = 1L;
        String symbol = "AMAZ";
        LogActionType actionType = LogActionType.TRANSACTION_BUY;
        Integer quantity = 13;
        String errorMessage = "Transaction failed due to database error";

        logService.logError(walletId, symbol, actionType, errorMessage, quantity);

        verify(logRepository).save(logCaptor.capture());
        AuditLog savedLog = logCaptor.getValue();

        assertEquals(LogStatus.ERROR, savedLog.getStatus());
        assertEquals(actionType, savedLog.getActionType());
        assertEquals(quantity, savedLog.getQuantity());

        assertNotNull(savedLog.getWallet(), "Proxy Wallet cannot be null");
        assertEquals(walletId, savedLog.getWallet().getId());

        assertNotNull(savedLog.getStock(), "Proxy Stock cannot be null");
        assertEquals(symbol, savedLog.getStock().getSymbol());
    }


    @Test
    public void createCorrectWarningTest(){
        Long walletId = 1L;
        String symbol = "AMAZ";
        LogActionType actionType = LogActionType.TRANSACTION_SELL;
        Integer quantity = 13;
        String warnMessage = "Not enough resources to buy %d stocks of %s".formatted(quantity, symbol);

        logService.logWarning(walletId, symbol, actionType, warnMessage, quantity);

        verify(logRepository).save(logCaptor.capture());
        AuditLog savedLog = logCaptor.getValue();

        assertEquals(LogStatus.WARN, savedLog.getStatus());
        assertEquals(actionType, savedLog.getActionType());
        assertEquals(quantity, savedLog.getQuantity());

        assertNotNull(savedLog.getWallet(), "Proxy Wallet cannot be null");
        assertEquals(walletId, savedLog.getWallet().getId());

        assertNotNull(savedLog.getStock(), "Proxy Stock cannot be null");
        assertEquals(symbol, savedLog.getStock().getSymbol());
    }
}
