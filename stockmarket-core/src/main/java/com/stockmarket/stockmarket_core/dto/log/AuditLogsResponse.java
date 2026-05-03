package com.stockmarket.stockmarket_core.dto.log;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;


public record AuditLogsResponse (
    @Schema(description = "List of logs") List<LogDTO> log
){
    public static AuditLogsResponse createAuditLogsResponse(List<LogDTO> logs){
        return new AuditLogsResponse(logs);
    }
}
