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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final AuditLogService logService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditLogsResponse> checkLogs(){
        Integer limit = 10_000;
        log.info("Received request for %d logs".formatted(limit));
        List<LogDTO> logs = logService.getLimitedLogsWithStatus(LogStatus.INFO, limit);

        return ResponseEntity.status(HttpStatus.OK).body(createAuditLogsResponse(logs));
    }

}
