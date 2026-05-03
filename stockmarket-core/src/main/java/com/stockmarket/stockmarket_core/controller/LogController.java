package com.stockmarket.stockmarket_core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmarket.stockmarket_core.dto.log.AuditLogsResponse;
import static com.stockmarket.stockmarket_core.dto.log.AuditLogsResponse.createAuditLogsResponse;
import com.stockmarket.stockmarket_core.dto.log.LogDTO;
import com.stockmarket.stockmarket_core.service.AuditLogService;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/log")
@Tag(name = "Audit Logs", description = "Endpoints for monitoring system activities, transaction history, and operational audits.")
public class LogController {

    private final AuditLogService logService;


    @Operation(
        summary = "Retrieve system audit logs",
        description = "Fetches a list of historical system logs. Returns up to the 10,000 most recent informational logs."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Audit logs successfully retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuditLogsResponse.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error while accessing audit logs")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditLogsResponse> checkLogs(){
        Integer limit = 10_000;
        log.info("Received request for %d logs".formatted(limit));
        List<LogDTO> logs = logService.getLimitedLogsWithStatus(LogStatus.INFO, limit);

        return ResponseEntity.status(HttpStatus.OK).body(createAuditLogsResponse(logs));
    }

}
