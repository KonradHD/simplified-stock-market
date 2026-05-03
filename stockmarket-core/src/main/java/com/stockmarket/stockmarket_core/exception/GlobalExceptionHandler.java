package com.stockmarket.stockmarket_core.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stockmarket.stockmarket_core.utils.ResponseMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgumentExceptions(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleStockNotFoundExceptions(StockNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }

    @ExceptionHandler(NotEnoughResourcesException.class)
    public ResponseEntity<ResponseMessage> handleNotEnoughResourcesExceptions(NotEnoughResourcesException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalStateException.class)
     public ResponseEntity<ResponseMessage> handleIllegalStateExceptions(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ResponseMessage("Error", "A database error occurred")
            );
    }

    @ExceptionHandler(WalletNotFoundException.class)
     public ResponseEntity<ResponseMessage> handleWalletNotFoundException(WalletNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage("Error", e.getMessage())
            );
    }
}
