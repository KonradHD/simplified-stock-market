package com.stockmarket.stockmarket_core.exception;

public class NotEnoughResourcesException extends RuntimeException{
    public NotEnoughResourcesException(String message){
        super(message);
    }

    public static NotEnoughResourcesException notEnoughResourcesException(String message){
        return new NotEnoughResourcesException("Not enough existing resources: " + message);
    }
}
