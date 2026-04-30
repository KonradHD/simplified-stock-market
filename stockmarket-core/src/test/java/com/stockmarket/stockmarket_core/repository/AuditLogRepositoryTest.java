package com.stockmarket.stockmarket_core.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Limit;
import org.springframework.test.context.TestPropertySource;

import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuditLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    void findByStatusOrderByCreatedAtDesc_returnsFilteredSortedAndLimitedLogs() {
        LocalDateTime now = LocalDateTime.now();
        Wallet proxyWallet = new Wallet();
        Stock proxyStock = Stock.builder()
                                .symbol("GOOG")
                                .build();

        proxyWallet = entityManager.persistAndFlush(proxyWallet);
        proxyStock = entityManager.persistAndFlush(proxyStock);

        AuditLog log1 = AuditLog.builder()
                .wallet(proxyWallet)
                .stock(proxyStock)
                .actionType(LogActionType.TRANSACTION_BUY)
                .status(LogStatus.ERROR)
                .message("Old error")
                .createdAt(now.minusDays(2))
                .build();

                
        AuditLog log2 = AuditLog.builder()
                .wallet(proxyWallet)
                .stock(proxyStock)
                .actionType(LogActionType.WALLET_CREATE)
                .status(LogStatus.ERROR)
                .message("New error")
                .createdAt(now)
                .build();
                
        AuditLog log3 = AuditLog.builder()
                .wallet(proxyWallet)
                .stock(proxyStock)
                .actionType(LogActionType.TRANSACTION_BUY)
                .status(LogStatus.ERROR)
                .message("Mid error")
                .createdAt(now.minusHours(12))
                .build();
                
        AuditLog log4 = AuditLog.builder()
                .wallet(proxyWallet)
                .stock(proxyStock)
                .actionType(LogActionType.TRANSACTION_SELL)
                .status(LogStatus.INFO)
                .message("Info")
                .createdAt(now.minusDays(1))
                .build();

        entityManager.persist(log1);
        entityManager.persist(log2);
        entityManager.persist(log3);
        entityManager.persist(log4);
        entityManager.flush();

        List<AuditLog> logs = auditLogRepository.findByStatusOrderByCreatedAtDesc(LogStatus.ERROR, Limit.of(2));

        assertEquals(2, logs.size());
        assertEquals("New error", logs.get(0).getMessage());
        assertEquals("Mid error", logs.get(1).getMessage());
    }

    @Test
    void findByStatusOrderByCreatedAtDesc_whenStatusNotFound_returnsEmptyList() {
        Wallet proxyWallet = new Wallet();
        Stock proxyStock = Stock.builder()
                                .symbol("GOOG")
                                .build();

        proxyWallet = entityManager.persistAndFlush(proxyWallet);
        proxyStock = entityManager.persistAndFlush(proxyStock);

        AuditLog log1 = AuditLog.builder()
                .wallet(proxyWallet)
                .stock(proxyStock)
                .status(LogStatus.INFO)
                .actionType(LogActionType.TRANSACTION_SELL)
                .message("Info")
                .createdAt(LocalDateTime.now())
                .build();
        
        entityManager.persistAndFlush(log1);
        List<AuditLog> logs = auditLogRepository.findByStatusOrderByCreatedAtDesc(LogStatus.ERROR, Limit.of(10));
        assertTrue(logs.isEmpty());
    }
}
