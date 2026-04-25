package com.stockmarket.stockmarket_core.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stocks")
@Builder
@Getter @Setter
public class Stock {
    @Id
    private String symbol;
    
    @Column(nullable = true)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ONE;

    @OneToOne(mappedBy="stock", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Bank bank;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WalletInventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AuditLog> logs = new ArrayList<>();
}
