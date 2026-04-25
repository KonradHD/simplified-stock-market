package com.stockmarket.stockmarket_core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/chaos")
public class ChaosController {
    
    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void killInstance() {
        log.error("Received chaos request. Current application instance will be killed.");
        
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(1);
        }).start();
    }
}
