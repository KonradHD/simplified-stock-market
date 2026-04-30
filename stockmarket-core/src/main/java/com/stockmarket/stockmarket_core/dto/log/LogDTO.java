package com.stockmarket.stockmarket_core.dto.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LogDTO (
    @NotNull LogActionType type,
    @NotNull @JsonProperty("wallet_id") Long walletId,
    @Size(max = 20, message = "Stock symbol cannot overcome 20 letters.") String name
){

    public LogDTO(LogActionType type, Long walletId){
        this(type, walletId, null);
    }

    public static LogDTO createLogDTO(LogActionType type, Long walletId, String name){
        return new LogDTO(type, walletId, name);
    }

    public static LogDTO createLogWalletDTO(LogActionType type, Long walletId){
        return new LogDTO(type, walletId);
    }
}
