package com.stockmarket.stockmarket_core.dto.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LogDTO (
    @Schema(description = "Type of action performed") @NotNull LogActionType type,
    @Schema(description = "Unique identifier of the affected wallet") @NotNull @JsonProperty("wallet_id") Long walletId,
    @Schema(description = "Unique stock symbol") @Size(max = 20, message = "Stock symbol cannot overcome 20 letters.") String name
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
