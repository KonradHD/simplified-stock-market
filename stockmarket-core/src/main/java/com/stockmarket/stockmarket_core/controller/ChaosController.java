package com.stockmarket.stockmarket_core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stockmarket.stockmarket_core.service.SystemTerminatorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/chaos")
@RequiredArgsConstructor
@Tag(name = "Chaos Controller", description = "Endpoint for resilience testing and simulating infrastructure failures.")
public class ChaosController {

    private final SystemTerminatorService terminatorService;
    

    @Operation(
        summary = "Kill application instance",
        description = "Schedules a graceful shutdown of the current application instance. " +
                      "WARNING: This is a destructive action used to test auto-recovery and orchestration resilience."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "202", 
            description = "Shutdown request accepted. The instance will terminate shortly."
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Failed to shutdown the instance"
        )
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void killInstance() {
        log.error("Received chaos request. Current application instance will be killed.");
        terminatorService.scheduleShutdown();
    }
}
