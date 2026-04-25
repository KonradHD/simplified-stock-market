package com.stockmarket.stockmarket_core.dto.wallet;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record WalletsResponse (
    @NotNull Long id,
    List<WalletInventoryDTO> stocks
){
    public static WalletsResponse createWalletsResponse(Long walletId, List<WalletInventoryDTO> inventories){
        return new WalletsResponse(walletId, inventories);
    }
}
