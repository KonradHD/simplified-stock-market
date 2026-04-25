package com.stockmarket.stockmarket_core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stockmarket.stockmarket_core.dto.wallet.WalletInventoryDTO;
import static com.stockmarket.stockmarket_core.dto.wallet.WalletInventoryDTO.createWalletInventoryDTO;
import com.stockmarket.stockmarket_core.model.WalletInventory;
import com.stockmarket.stockmarket_core.repository.WalletInventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletInventoryService {
    private final WalletInventoryRepository walletInventoryRepository;

    public List<WalletInventoryDTO> getWalletInventoriesDTO(Long walletId){
        List<WalletInventory> inventories = walletInventoryRepository.findAllByWalletId(walletId);

        return inventories.stream()
                    .map(inv -> createWalletInventoryDTO(inv.getStock().getSymbol(), inv.getQuantity()))
                    .collect(Collectors.toList());
    }
}
