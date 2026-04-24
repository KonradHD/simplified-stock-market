package com.stockmarket.stockmarket_core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stockmarket.stockmarket_core.utils.ResponseMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleStockNotFoundException(StockNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }

    @ExceptionHandler(NotEnoughResourcesException.class)
    public ResponseEntity<ResponseMessage> handleNotEnoughResourcesException(NotEnoughResourcesException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }
}
