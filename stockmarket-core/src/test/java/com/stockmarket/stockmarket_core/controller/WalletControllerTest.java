package com.stockmarket.stockmarket_core.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmarket.stockmarket_core.dto.StockDTO;
import com.stockmarket.stockmarket_core.dto.wallet.TradeActionRequest;
import com.stockmarket.stockmarket_core.exception.WalletNotFoundException;

import static com.stockmarket.stockmarket_core.exception.StockNotFoundException.stockNotFoundException;
import static com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse.createWalletResponse;
import static com.stockmarket.stockmarket_core.exception.NotEnoughResourcesException.notEnoughResourcesException;
import com.stockmarket.stockmarket_core.service.StockService;
import com.stockmarket.stockmarket_core.service.TradingService;
import com.stockmarket.stockmarket_core.service.WalletInventoryService;
import com.stockmarket.stockmarket_core.service.WalletService;
import com.stockmarket.stockmarket_core.utils.types.Action;
import com.stockmarket.stockmarket_core.dto.wallet.NewWalletResponse;

import org.springframework.test.web.servlet.MockMvc;

import static com.stockmarket.stockmarket_core.exception.WalletNotFoundException.walletNotFoundException;
import com.stockmarket.stockmarket_core.model.Wallet;

@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private TradingService tradingService;

    @MockitoBean
    private WalletService walletService;

    @MockitoBean
    private WalletInventoryService walletInventoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void sellTransactionSuccessTest() throws Exception {
        Long walletId = 1L;
        String lowerCaseSymbol = "aapl"; 
        String expectedSymbol = "AAPL";
        TradeActionRequest request = new TradeActionRequest(Action.SELL);
        
        mockMvc.perform(
                post("/wallets/{wallet_id}/stocks/{stock_name}", walletId, lowerCaseSymbol)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Transaction made successfully"));

        verify(tradingService).sellStock(walletId, expectedSymbol, 1);
        verify(tradingService, never()).buyStock(any(), any(), any());
    }

    @Test
    public void buyTransactionSuccessTest() throws Exception {
        Long walletId = 1L;
        String lowerCaseSymbol = "aapl"; 
        String expectedSymbol = "AAPL";
        TradeActionRequest request = new TradeActionRequest(Action.BUY);
        
        mockMvc.perform(
                post("/wallets/{wallet_id}/stocks/{stock_name}", walletId, lowerCaseSymbol)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Transaction made successfully"));

        verify(tradingService).buyStock(walletId, expectedSymbol, 1);
        verify(tradingService, never()).sellStock(any(), any(), any());
    }

    @Test 
    public void makeTransactionThrowsWalletNotFoundException() throws Exception {
        Long nonExistingWalletId = 1L;
        String symbol = "GOOG";
        TradeActionRequest request = new TradeActionRequest(Action.SELL);
        
        doThrow(walletNotFoundException(nonExistingWalletId)).when(tradingService).sellStock(nonExistingWalletId, symbol, 1);

        mockMvc.perform(
                post("/wallets/{wallet_id}/stocks/{stock_name}", nonExistingWalletId, symbol)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("The wallet with id: %s does not exist or is inactive.".formatted(nonExistingWalletId)));
    }

    @Test 
    public void makeTransactionThrowsStockNotFoundException() throws Exception {
        Long walletId = 1L;
        String nonExistingSymbol = "GOOG";
        TradeActionRequest request = new TradeActionRequest(Action.BUY);
        
        doThrow(stockNotFoundException(nonExistingSymbol)).when(tradingService).buyStock(walletId, nonExistingSymbol, 1);

        mockMvc.perform(
                post("/wallets/{wallet_id}/stocks/{stock_name}", walletId, nonExistingSymbol)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("The stock with symbol: %s is unavailable in the Bank currently".formatted(nonExistingSymbol)));

    }

    @Test 
    public void makeTransactionThrowsNotEnoughResourcesException() throws Exception {
        Long walletId = 1L;
        String symbol = "GOOG";
        Integer stockQuantity = 0;
        TradeActionRequest request = new TradeActionRequest(Action.BUY);
        
        doThrow(notEnoughResourcesException("Only %d of %s is left".formatted(stockQuantity, symbol))).when(tradingService).buyStock(walletId, symbol, 1);

        mockMvc.perform(
                post("/wallets/{wallet_id}/stocks/{stock_name}", walletId, symbol)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("Not enough existing resources: Only %d of %s is left".formatted(stockQuantity, symbol)));

    }

    @Test
    public void gettingWalletDataSuccessTest() throws Exception {
        Long walletId = 1L;
        List<StockDTO> mockInventories = List.of(
            new StockDTO("GOOG", 15),
            new StockDTO("AMAZ", 5)
        );
        
        when(walletInventoryService.getWalletInventoriesDTO(walletId))
                .thenReturn(mockInventories);

        mockMvc.perform(
                get("/wallets/{wallet_id}", walletId)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(walletId))
            .andExpect(jsonPath("$.stocks[0].name").value("GOOG"))
            .andExpect(jsonPath("$.stocks[0].quantity").value(15))
            .andExpect(jsonPath("$.stocks[1].name").value("AMAZ"))
            .andExpect(jsonPath("$.stocks[1].quantity").value(5));
        
        verify(walletInventoryService).getWalletInventoriesDTO(walletId);
    }

    @Test
    public void gettingWalletDataThrowsWalletNotFoundExceptionTest() throws Exception {
        Long nonExistingWalletId = 1L;
     
        doThrow(walletNotFoundException(nonExistingWalletId)).when(walletInventoryService).getWalletInventoriesDTO(nonExistingWalletId);

        mockMvc.perform(
                get("/wallets/{wallet_id}", nonExistingWalletId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value("The wallet with id: %s does not exist or is inactive.".formatted(nonExistingWalletId)));
    }

    @Test
    public void getWalletQuantitySuccessTest() throws Exception {
        Long walletId = 1L;
        String lowercaseSymbol = "amaz";
        String expectedSymbol = "AMAZ";
        Integer expectedQuantity = 10;

        when(stockService.stockExists(expectedSymbol)).thenReturn(true);
        when(walletService.walletExists(walletId)).thenReturn(true);
        when(walletInventoryService.getInventoryQuantity(walletId, expectedSymbol)).thenReturn(expectedQuantity);

        mockMvc.perform(
                        get("/wallets/{wallet_id}/stocks/{stock_name}", walletId, lowercaseSymbol)
                        .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().string(expectedQuantity.toString()));

        verify(walletInventoryService).getInventoryQuantity(walletId, expectedSymbol);
    }

    @Test
    public void getWalletQuantityThrowsWalletNotFoundExceptionTest() throws Exception {
        Long walletId = 1L;
        String symbol = "GOOG";

        when(stockService.stockExists(symbol)).thenReturn(true);
        when(walletService.walletExists(walletId)).thenReturn(false);
        mockMvc.perform(
                        get("/wallets/{wallet_id}/stocks/{stock_name}", walletId, symbol)
                        .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("Error"))
                    .andExpect(jsonPath("$.message").value("Wallet: %d does not exist".formatted(walletId)));

        verify(walletInventoryService, never()).getInventoryQuantity(any(), any());
    }

    @Test
    public void getWalletQuantityThrowsStockNotFoundExceptionTest() throws Exception {
        Long walletId = 1L;
        String invalidSymbol = "TEST";

        when(stockService.stockExists(invalidSymbol)).thenReturn(false);

        mockMvc.perform(
                        get("/wallets/{wallet_id}/stocks/{stock_name}", walletId, invalidSymbol)
                        .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value("Error"))
                    .andExpect(jsonPath("$.message").value("Stock: %s symbol does not exist".formatted(invalidSymbol)));

        verify(walletService, never()).walletExists(any());
        verify(walletInventoryService, never()).getInventoryQuantity(any(), any());
    }

    @Test 
    public void walletCreateTest() throws Exception {
        Long expectedWalletId = 100L;
        Wallet wallet = Wallet.builder()
                        .id(expectedWalletId)
                        .build();
        NewWalletResponse mockResponse = createWalletResponse(wallet);

        when(walletService.createWallet()).thenReturn(mockResponse);

        mockMvc.perform(
                        post("/wallets") 
                        .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isCreated()) 
                    .andExpect(jsonPath("$.id").value(expectedWalletId));

        verify(walletService).createWallet();
    }

    @Test 
    public void walletDeleteSuccessTest() throws Exception {
        Long walletId = 1L;
        mockMvc.perform(
                    delete("/wallets/{wallet_id}", walletId)
                )
                .andExpect(status().isNoContent());

        verify(walletService).deleteWallet(walletId);
    }

    @Test 
    public void walletDeleteThrowsWalletNotFoundExceptionTest() throws Exception {
        Long walletId = 99L;
        String errorMessage = "The wallet with id: %s does not exist or is inactive.".formatted(walletId);

        doThrow(new WalletNotFoundException(errorMessage))
                .when(walletService).deleteWallet(walletId);

        mockMvc.perform(
                    delete("/wallets/{wallet_id}", walletId)
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Error"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
    
}
