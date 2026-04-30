package com.stockmarket.stockmarket_core.service;

import org.springframework.stereotype.Service;

@Service
public class SystemTerminatorService {
    
    public void scheduleShutdown() {
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