package com.stockmarket.stockmarket_core.utils.types;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Action {
    BUY,
    SELL;

    @JsonCreator
    public static Action fromString(String value) {
        if (value == null) {
            return null;
        }

        return Action.valueOf(value.toUpperCase());
    }
}
