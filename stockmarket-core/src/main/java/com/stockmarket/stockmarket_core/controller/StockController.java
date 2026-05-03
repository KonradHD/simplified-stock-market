package com.stockmarket.stockmarket_core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryInitRequest;
import com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryResponse;
import com.stockmarket.stockmarket_core.dto.stock.StockDTO;

import static com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryResponse.createBankInventoryResponse;
import com.stockmarket.stockmarket_core.service.BankService;
import com.stockmarket.stockmarket_core.utils.ResponseMessage;

import io.swagger.v3.oas.annotations.Operation;
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
@RequiredArgsConstructor
@RequestMapping("/stocks")
@Tag(name = "Bank inventory", description = "Endpoints for managing the central bank's stock reserves and initial setup.")
public class StockController {

    private final BankService bankService;
    

    @Operation(
        summary = "Retrieve all bank inventory",
        description = "Fetches the current list of all stocks available in the central banks inventory."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Bank inventory successfully retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankInventoryResponse.class))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error while fetching inventory")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> inspectBankInventory(){
        log.info("Received request for all bank inventory");
        List<StockDTO> stocks = bankService.getInventory();

        return ResponseEntity.status(HttpStatus.OK).body(createBankInventoryResponse(stocks));
    }

    @Operation(
        summary = "Initialize bank state",
        description = "Sets the initial stock levels for the bank inventory, typically used for system initialization."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bank state successfully initialized"),
        @ApiResponse(responseCode = "404", description = "Some stock symbols are invalid."),
        @ApiResponse(responseCode = "500", description = "Database error during initialization")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> setBankInventory(@RequestBody @Valid BankInventoryInitRequest request){
        log.info("Received request for initializating bank state");

        List<StockDTO> stocks = request.stocks();
        bankService.initBankState(stocks);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(
            "Success",
            "Bank state was initialized"
        ));
    }
}
