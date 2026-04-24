package com.stockmarket.stockmarket_core.dto.wallet;

import com.stockmarket.stockmarket_core.utils.types.Action;

import jakarta.validation.constraints.NotNull;


public record TradeActionRequest(
    @NotNull(message = "Action type is required") Action type)
{

}
