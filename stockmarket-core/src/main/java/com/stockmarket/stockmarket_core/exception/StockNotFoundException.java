package com.stockmarket.stockmarket_core.exception;

public class StockNotFoundException extends RuntimeException{
    public StockNotFoundException(String message){
        super(message);
    }

    public static StockNotFoundException stockNotFoundException(String symbol){
        String message = "The stock with symbol: %s is unavailable in the Bank currently".formatted(symbol);
        return new StockNotFoundException(message);
    }
}
