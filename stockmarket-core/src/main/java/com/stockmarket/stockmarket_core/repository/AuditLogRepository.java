package com.stockmarket.stockmarket_core.repository;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stockmarket.stockmarket_core.model.AuditLog;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByStatusOrderByCreatedAtDesc(LogStatus status, Limit limit);
}
