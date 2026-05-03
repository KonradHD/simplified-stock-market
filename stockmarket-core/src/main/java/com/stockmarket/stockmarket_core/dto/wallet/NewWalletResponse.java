package com.stockmarket.stockmarket_core.dto.wallet;

import java.time.LocalDateTime;

import com.stockmarket.stockmarket_core.model.Wallet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record NewWalletResponse (
    @Schema(description = "Unique identifier of the wallet") @NotNull Long id,
    @Schema(description = "Timestamp of the wallet creation") @PastOrPresent LocalDateTime createdAt
) {
    public static NewWalletResponse createWalletResponse(Wallet wallet){
        return new NewWalletResponse(wallet.getId(), wallet.getCreatedAt());
    }
}
