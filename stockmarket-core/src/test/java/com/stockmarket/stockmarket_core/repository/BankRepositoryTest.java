package com.stockmarket.stockmarket_core.repository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.stockmarket.stockmarket_core.model.Bank;
import com.stockmarket.stockmarket_core.model.Stock;

@DataJpaTest    
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class BankRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankRepository bankRepository;

    @Test
    void findByIdLockedSuccessTest() {
        String symbol = "GOOG";
        Stock stock = Stock.builder()
                .symbol(symbol)
                .build();
        stock = entityManager.persistAndFlush(stock);

        Bank bank = new Bank();
        bank.setStock(stock); 
        bank.setStockSymbol(symbol);
        entityManager.persistAndFlush(bank);

        entityManager.clear(); 
        Optional<Bank> bankStock = bankRepository.findByIdLocked(symbol);

        assertTrue(bankStock.isPresent());
        assertEquals(symbol, bankStock.get().getStockSymbol());
        
        assertNotNull(bankStock.get().getStock());
        assertEquals(symbol, bankStock.get().getStock().getSymbol());
    }

    @Test
    void findByIdLockedEmptyTest() {
        Optional<Bank> bankStock = bankRepository.findByIdLocked("TEST");
        assertTrue(bankStock.isEmpty());
    }
}
