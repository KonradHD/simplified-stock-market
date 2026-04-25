package com.stockmarket.stockmarket_core.dto.wallet;

import java.util.List;

import com.stockmarket.stockmarket_core.dto.StockDTO;

import jakarta.validation.constraints.NotNull;

public record WalletsResponse (
    @NotNull Long id,
    List<StockDTO> stocks
){
    public static WalletsResponse createWalletsResponse(Long walletId, List<StockDTO> inventories){
        return new WalletsResponse(walletId, inventories);
    }
}
