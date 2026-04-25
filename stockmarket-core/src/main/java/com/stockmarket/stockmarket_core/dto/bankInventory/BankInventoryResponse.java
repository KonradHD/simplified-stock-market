package com.stockmarket.stockmarket_core.dto.bankInventory;

import java.util.List;

import com.stockmarket.stockmarket_core.dto.StockDTO;

public record BankInventoryResponse (
    List<StockDTO> stocks
){
    public static BankInventoryResponse createBankInventoryResponse(List<StockDTO> stocks){
        return new BankInventoryResponse(stocks);
    }
}
