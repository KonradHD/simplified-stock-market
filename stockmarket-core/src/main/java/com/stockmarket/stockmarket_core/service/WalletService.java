package com.stockmarket.stockmarket_core.service;

import org.springframework.stereotype.Service;

import com.stockmarket.stockmarket_core.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;


    public boolean walletExists(Long walletId){
        return walletRepository.existsById(walletId);
    }
}
