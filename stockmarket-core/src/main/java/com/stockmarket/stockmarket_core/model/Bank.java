package com.stockmarket.stockmarket_core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
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
@Table(name = "bank_inventory")
@Builder
@Getter @Setter
public class Bank {
    @Id
    @Column(name = "stock_symbol")
    private String stockSymbol;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_symbol")
    private Stock stock;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;
}
