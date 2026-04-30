package com.stockmarket.stockmarket_core.repository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.stockmarket.stockmarket_core.model.Stock;
import com.stockmarket.stockmarket_core.model.Wallet;
import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class WalletInventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WalletInventoryRepository walletInventoryRepository;

    @Test
    void findAllByWalletIdTest() {
        Wallet wallet1 = entityManager.persistAndFlush(new Wallet());
        Wallet wallet2 = entityManager.persistAndFlush(new Wallet());

        Stock amazonStock = Stock.builder().symbol("AMAZ").build();
        Stock googleStock = Stock.builder().symbol("GOOG").build();
        entityManager.persist(amazonStock);
        entityManager.persist(googleStock);

        WalletInventory inv1 = WalletInventory.builder()
                .id(new WalletInventoryId(wallet1.getId(), "AAPL"))
                .wallet(wallet1)
                .stock(amazonStock)
                .quantity(10)
                .build();

        WalletInventory inv2 = WalletInventory.builder()
                .id(new WalletInventoryId(wallet1.getId(), "GOOG"))
                .wallet(wallet1)
                .stock(googleStock)
                .quantity(5)
                .build();

        WalletInventory inv3 = WalletInventory.builder()
                .id(new WalletInventoryId(wallet2.getId(), "AAPL"))
                .wallet(wallet2)
                .stock(amazonStock)
                .quantity(100)
                .build();

        entityManager.persist(inv1);
        entityManager.persist(inv2);
        entityManager.persist(inv3);
        entityManager.flush();
        entityManager.clear();

        List<WalletInventory> inventories = walletInventoryRepository.findAllByWalletId(wallet1.getId());

        assertEquals(2, inventories.size());
        assertEquals("AMAZ", inventories.get(0).getId().getStockSymbol());
        assertEquals("GOOG", inventories.get(1).getId().getStockSymbol());
        assertTrue(inventories.stream().allMatch(inv -> inv.getWallet().getId().equals(wallet1.getId())));
    }

    @Test
    void findAllByWalletIdEmptyTest() {
        Wallet emptyWallet = entityManager.persistAndFlush(new Wallet());
        List<WalletInventory> inventories = walletInventoryRepository.findAllByWalletId(emptyWallet.getId());

        assertTrue(inventories.isEmpty());
    }
}
