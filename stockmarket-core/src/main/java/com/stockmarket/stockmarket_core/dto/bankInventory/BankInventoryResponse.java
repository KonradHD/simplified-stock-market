package com.stockmarket.stockmarket_core.dto.bankInventory;

import java.util.List;

import com.stockmarket.stockmarket_core.dto.stock.StockDTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record BankInventoryResponse (
    @Schema(description = "List of stocks affected") List<StockDTO> stocks
){
    public static BankInventoryResponse createBankInventoryResponse(List<StockDTO> stocks){
        return new BankInventoryResponse(stocks);
    }
}
