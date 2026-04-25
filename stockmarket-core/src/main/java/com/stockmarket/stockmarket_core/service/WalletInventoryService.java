package com.stockmarket.stockmarket_core.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stockmarket.stockmarket_core.dto.StockDTO;
import static com.stockmarket.stockmarket_core.dto.StockDTO.createStockDTO;
import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.model.WalletInventoryId;
import com.stockmarket.stockmarket_core.repository.WalletInventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletInventoryService {
    private final WalletInventoryRepository walletInventoryRepository;

    public List<StockDTO> getWalletInventoriesDTO(Long walletId){
        List<WalletInventory> inventories = walletInventoryRepository.findAllByWalletId(walletId);

        return inventories.stream()
                    .map(inv -> createStockDTO(inv.getStock().getSymbol(), inv.getQuantity()))
                    .collect(Collectors.toList());
    }


    public Integer getInventoryQuantity(Long walletId, String stockSymbol){
        WalletInventoryId inventoryId = new WalletInventoryId(walletId, stockSymbol);

        Optional<WalletInventory> inventory = walletInventoryRepository.findById(inventoryId);

        if(inventory.isPresent()){
            return inventory.get().getQuantity();
        }
        return 0;
    }
}
