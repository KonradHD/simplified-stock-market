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

import com.stockmarket.stockmarket_core.dto.stock.StockDTO;
import com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse;
import com.stockmarket.stockmarket_core.dto.wallet.TradeActionRequest;
import com.stockmarket.stockmarket_core.dto.wallet.WalletsResponse;
import static com.stockmarket.stockmarket_core.dto.wallet.WalletsResponse.createWalletsResponse;
import com.stockmarket.stockmarket_core.service.StockService;
import com.stockmarket.stockmarket_core.service.TradingService;
import com.stockmarket.stockmarket_core.service.WalletInventoryService;
import com.stockmarket.stockmarket_core.service.WalletService;
import com.stockmarket.stockmarket_core.utils.ResponseMessage;
import com.stockmarket.stockmarket_core.utils.types.Action;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "Endpoints for managing investment wallets, executing trades and checking inventories.")
public class WalletController {

    private final StockService stockService;
    private final TradingService tradingService;
    private final WalletService walletService;
    private final WalletInventoryService walletInventoryService;
    

    @Operation(
        summary = "Execute a transaction",
        description = "Performs a BUY or SELL action for exactly 1 unit of a specific stock. Validates wallet funds and stock availability."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction successfully executed"),
        @ApiResponse(responseCode = "400", description = "Insufficient funds or stock quantity"),
        @ApiResponse(responseCode = "404", description = "Wallet or Stock symbol not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error - database failure")
    })
    @PostMapping(value = "/{wallet_id}/stocks/{stock_name}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> makeTransaction(
        @Parameter(description = "ID of the wallet")
        @PathVariable("wallet_id") Long walletId,
        @Parameter(description = "The stock symbol", example = "AMAZ")
        @PathVariable("stock_name") String stockSymbolInput,
        @RequestBody @Valid TradeActionRequest requestBody){

            Action type = requestBody.type();
            String stockSymbol = stockSymbolInput.toUpperCase();
            log.info("Received transaction request, type: {}, concerning wallet with id: {} and stock: {}", type.toString(), walletId, stockSymbol);

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


    @Operation(
        summary = "Get wallet details",
        description = "Retrieves the full list of stock inventories owned by the specified wallet."
    )
    @ApiResponses(value={
        @ApiResponse(
            responseCode = "200", 
            description = "Inventories data was retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WalletsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Wallet does not exist",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error while getting inventories data")
    })
    @GetMapping(value = "/{wallet_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> walletData(
        @Parameter(description = "ID of the wallet to retrieve")
        @PathVariable("wallet_id") Long walletId){
        log.info("Received request for wallet inventories data with id: {}", walletId);

        List<StockDTO> walletInventories = walletInventoryService.getWalletInventoriesDTO(walletId);

        return ResponseEntity.status(HttpStatus.OK).body(createWalletsResponse(walletId, walletInventories));
    }


    @Operation(
        summary = "Checks stock quantity",
        description = "Returns the specific amount of a single stock held in a particular wallet."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Current quantity returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Stock symbol or Wallet ID does not exist",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error while getting stock quantity")
    })
    @GetMapping(value = "/{wallet_id}/stocks/{stock_name}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> walletQuantity(
        @Parameter(description = "ID of the wallet to check stock quantity")
        @PathVariable("wallet_id") Long walletId, 
        @Parameter(description = "Stock symbol")
        @PathVariable("stock_name") String stockSymbolInput){

        String stockSymbol = stockSymbolInput.toUpperCase();
        log.info("Received request for wallet quantity with id: {}, for stock: {}", walletId, stockSymbol);

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


    @Operation(
        summary = "Create a new wallet",
        description = "Initializes a new investment wallet and returns its unique ID with creation date."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description= "Wallet was created."),
        @ApiResponse(responseCode = "500", description = "Internal server error while creating new wallet")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewWalletResponse> createWallet(){
        log.info("Received request to create new wallet");

        NewWalletResponse newWallet = walletService.createWallet();
        return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
    }


    @Operation(
        summary = "Delete a wallet",
        description = "Sets wallets status to inactive, so the wallet becomes unobtainable."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode="204", description = "Wallet has been deleted."),
        @ApiResponse(responseCode="404", description = "Wallet id not found."),
        @ApiResponse(responseCode = "500", description = "Internal server error while deleting the wallet")
    })
    @DeleteMapping(value = "/{wallet_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteWallet(@PathVariable("wallet_id") Long walletId){
        log.info("Received request to delete wallet with id: {}", walletId);

        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }
}
