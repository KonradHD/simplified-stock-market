package com.stockmarket.stockmarket_core.dto.wallet;

import java.time.LocalDateTime;

import com.stockmarket.stockmarket_core.model.Wallet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record NewWalletResponse (
    @NotNull Long id,
    @PastOrPresent LocalDateTime createdAt
) {
    public static NewWalletResponse createWalletResponse(Wallet wallet){
        return new NewWalletResponse(wallet.getId(), wallet.getCreatedAt());
    }
}
