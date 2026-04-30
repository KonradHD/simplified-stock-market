package com.stockmarket.stockmarket_core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmarket.stockmarket_core.dto.StockDTO;
import com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse;
import com.stockmarket.stockmarket_core.dto.wallet.TradeActionRequest;
import static com.stockmarket.stockmarket_core.dto.wallet.WalletsResponse.createWalletsResponse;
import com.stockmarket.stockmarket_core.service.StockService;
import com.stockmarket.stockmarket_core.service.TradingService;
import com.stockmarket.stockmarket_core.service.WalletInventoryService;
import com.stockmarket.stockmarket_core.service.WalletService;
import com.stockmarket.stockmarket_core.utils.ResponseMessage;
import com.stockmarket.stockmarket_core.utils.types.Action;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final StockService stockService;
    private final TradingService tradingService;
    private final WalletService walletService;
    private final WalletInventoryService walletInventoryService;
    
    @PostMapping(value = "/{wallet_id}/stocks/{stock_name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> makeTransaction(
        @PathVariable("wallet_id") Long walletId,
        @PathVariable("stock_name") String stockSymbolInput,
        @RequestBody @Valid TradeActionRequest requestBody){

            Action type = requestBody.type();
            String stockSymbol = stockSymbolInput.toUpperCase();
            log.info("Received transaction request, type: {}", type.toString());

            if(type == Action.BUY){
                tradingService.buyStock(walletId, stockSymbol, 1);
            }

            if(type == Action.SELL){
                tradingService.sellStock(walletId, stockSymbol, 1);
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseMessage("Success", "Transaction made successfully")
            );
    }

    @GetMapping(value = "/{wallet_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> walletData(@PathVariable("wallet_id") Long walletId){
        log.info("Received request for wallet data");

        List<StockDTO> walletInventories = walletInventoryService.getWalletInventoriesDTO(walletId);

        return ResponseEntity.status(HttpStatus.OK).body(createWalletsResponse(walletId, walletInventories));
    }

    @GetMapping(value = "/{wallet_id}/stocks/{stock_name}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> walletQuantity(
        @PathVariable("wallet_id") Long walletId, 
        @PathVariable("stock_name") String stockSymbolInput){

        String stockSymbol = stockSymbolInput.toUpperCase();
        log.info("Received request for wallet quantity for stock: {}", stockSymbol);

        if(!stockService.stockExists(stockSymbol)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseMessage("Error", "Stock: %s symbol does not exist".formatted(stockSymbol))
                );
            }

        if(!walletService.walletExists(walletId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseMessage("Error", "Wallet: %d does not exist".formatted(walletId))
            );
        }

        Integer quantity = walletInventoryService.getInventoryQuantity(walletId, stockSymbol);

        return ResponseEntity.status(HttpStatus.OK).body(quantity);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewWalletResponse> createWallet(){
        log.info("Received request for creating new wallet");

        NewWalletResponse newWallet = walletService.createWallet();
        return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
    }

    @DeleteMapping(value = "/{wallet_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteWallet(@PathVariable("wallet_id") Long walletId){
        log.info("Received request for deleting wallet");

        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }
}
