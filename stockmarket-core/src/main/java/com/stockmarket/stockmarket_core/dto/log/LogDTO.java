package com.stockmarket.stockmarket_core.dto.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LogDTO (
    @NotNull LogActionType type,
    @NotNull @JsonProperty("wallet_id") Long walletId,
    @NotBlank @Size(max = 20, message = "Stock symbol cannot overcome 20 letters.") String name
){

    public static LogDTO createLogDTO(LogActionType type, Long walletId, String name){
        return new LogDTO(type, walletId, name);
    }
}
