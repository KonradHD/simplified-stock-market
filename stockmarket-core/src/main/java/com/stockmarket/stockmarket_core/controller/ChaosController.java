package com.stockmarket.stockmarket_core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stockmarket.stockmarket_core.service.SystemTerminatorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/chaos")
@RequiredArgsConstructor
public class ChaosController {

    private final SystemTerminatorService terminatorService;
    
    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void killInstance() {
        log.error("Received chaos request. Current application instance will be killed.");
        terminatorService.scheduleShutdown();
    }
}
