package com.stockmarket.stockmarket_core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmarket.stockmarket_core.dto.wallet.TradeActionRequest;
import com.stockmarket.stockmarket_core.service.StockService;
import com.stockmarket.stockmarket_core.service.TradingService;
import com.stockmarket.stockmarket_core.utils.ResponseMessage;
import com.stockmarket.stockmarket_core.utils.types.Action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final StockService stockService;
    private final TradingService tradingService;
    
    @PostMapping(value = "/{wallet_id}/stocks/{stock_name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> makeTransaction(
        @PathVariable Long wallet_id,
        @PathVariable String stock_name,
        @RequestBody TradeActionRequest requestBody){
            Action type = requestBody.type();
            log.info("Received transaction request, type: {}", type.toString());

            if(!stockService.stockExists(stock_name)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("Error", "Given stock symbol does not exist")
                );
            }

            if(type == Action.BUY){
                tradingService.buyStock(wallet_id, stock_name, 1);
            }

            if(type == Action.SELL){
                tradingService.sellStock(wallet_id, stock_name, 1);
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage("Success", "Transaction made successfully")
            );
    }
}
