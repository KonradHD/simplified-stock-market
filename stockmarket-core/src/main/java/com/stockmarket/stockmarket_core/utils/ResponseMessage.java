package com.stockmarket.stockmarket_core.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseMessage {
    private String status;
    private String message; 
}
