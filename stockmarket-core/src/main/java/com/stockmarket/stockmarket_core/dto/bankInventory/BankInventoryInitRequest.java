package com.stockmarket.stockmarket_core.dto.bankInventory;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockmarket.stockmarket_core.dto.StockDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record BankInventoryInitRequest (
    @NotEmpty(message = "Initialization stock list cannot be empty.") 
    @Valid
    List<StockDTO> stocks
) {
    @JsonCreator
    public BankInventoryInitRequest(@JsonProperty("stocks") List<StockDTO> stocks) {
        this.stocks = stocks;
    }
}
