package com.stockmarket.stockmarket_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StockDTO (
    @NotBlank String name,
    @NotNull Integer quantity
){
    public static StockDTO createStockDTO(String stockSymbol, Integer quantity){
        return new StockDTO(stockSymbol, quantity);
    }
}
