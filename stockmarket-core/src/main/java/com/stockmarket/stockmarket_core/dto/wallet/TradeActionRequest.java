package com.stockmarket.stockmarket_core.dto.wallet;

import com.stockmarket.stockmarket_core.utils.types.Action;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;


public record TradeActionRequest(
    @Schema(description = "Sell or buy action") @NotNull(message = "Action type is required") Action type)
{

}
