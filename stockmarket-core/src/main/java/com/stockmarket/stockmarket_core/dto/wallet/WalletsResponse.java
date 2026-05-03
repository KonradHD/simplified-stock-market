package com.stockmarket.stockmarket_core.dto.wallet;

import java.util.List;

import com.stockmarket.stockmarket_core.dto.stock.StockDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WalletsResponse (
    @Schema(description = "Unique identifier of the wallet") @NotNull Long id,
    @Schema(description = "List of stocks affected") List<StockDTO> stocks
){
    public static WalletsResponse createWalletsResponse(Long walletId, List<StockDTO> inventories){
        return new WalletsResponse(walletId, inventories);
    }
}
