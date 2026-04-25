package com.stockmarket.stockmarket_core.dto.wallet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WalletInventoryDTO (
    @NotBlank String name,
    @NotNull Integer quantity
){
    public static WalletInventoryDTO createWalletInventoryDTO(String stockSymbol, Integer quantity){
        return new WalletInventoryDTO(stockSymbol, quantity);
    }
}
