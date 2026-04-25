package com.stockmarket.stockmarket_core.dto.log;

import java.util.List;


public record AuditLogsResponse (
    List<LogDTO> log
){
    public static AuditLogsResponse createAuditLogsResponse(List<LogDTO> logs){
        return new AuditLogsResponse(logs);
    }
}
