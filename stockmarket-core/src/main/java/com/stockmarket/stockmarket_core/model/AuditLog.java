package com.stockmarket.stockmarket_core.model;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "logs")
@Builder
@Getter @Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable=false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_symbol", nullable = true)
    private Stock stock;

    @Column(name = "action_type", nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private LogActionType actionType;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LogStatus status;

    @Column(nullable = false)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @Column(nullable = true, updatable = false)
    private Integer quantity;
}
