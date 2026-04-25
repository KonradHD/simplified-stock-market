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

import com.stockmarket.stockmarket_core.dto.StockDTO;
import com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryInitRequest;
import com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryResponse;
import static com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryResponse.createBankInventoryResponse;
import com.stockmarket.stockmarket_core.service.BankService;
import com.stockmarket.stockmarket_core.utils.ResponseMessage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController {
    private final BankService bankService;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankInventoryResponse> inspectBankInventory(){
        log.info("Received request for all bank inventory");
        List<StockDTO> stocks = bankService.getInventory();

        return ResponseEntity.status(HttpStatus.OK).body(createBankInventoryResponse(stocks));
    }

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
