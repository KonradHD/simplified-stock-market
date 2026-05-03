package com.stockmarket.stockmarket_core.dto.stock;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockDTO (
    @Schema(description = "Unique stock symbol") @NotBlank @Size(max = 20, message = "Stock symbol cannot overcome 20 letters.") String name,
    @Schema(description = "Quantity of the concerned stock") @NotNull @Min(value = 0, message = "Stock quantity cannot be negative.") Integer quantity
){

    public StockDTO(String name, Integer quantity){
        this.quantity = quantity;
        this.name = name.toUpperCase(); 
    }

    public static StockDTO createStockDTO(String stockSymbol, Integer quantity){
        return new StockDTO(stockSymbol, quantity);
    }
}
