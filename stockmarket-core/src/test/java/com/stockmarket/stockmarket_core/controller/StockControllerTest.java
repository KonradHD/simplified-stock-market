package com.stockmarket.stockmarket_core.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmarket.stockmarket_core.dto.bankInventory.BankInventoryInitRequest;
import com.stockmarket.stockmarket_core.dto.stock.StockDTO;
import com.stockmarket.stockmarket_core.exception.StockNotFoundException;
import com.stockmarket.stockmarket_core.service.BankService;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    
    @Test
    public void inspectEmptyBankInventoryTest() throws Exception {
        when(bankService.getInventory()).thenReturn(List.of());

        mockMvc.perform(get("/stocks")
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stocks").isEmpty()); 
    }

    @Test
    public void inspectBankInventoryTest() throws Exception {

        List<StockDTO> mockStocks = List.of(
            new StockDTO("GOOG", 15),
            new StockDTO("AMAZ", 20)
        );
        when(bankService.getInventory()).thenReturn(mockStocks);

        mockMvc.perform(get("/stocks")
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stocks[0].name").value("GOOG"))
                .andExpect(jsonPath("$.stocks[0].quantity").value(15))
                .andExpect(jsonPath("$.stocks[1].name").value("AMAZ"))
                .andExpect(jsonPath("$.stocks[1].quantity").value(20));
    }


    @Test
    public void setBankInventoryInvalidRequestTest() throws Exception {
        BankInventoryInitRequest badRequest = new BankInventoryInitRequest(null); 
        String jsonRequest = objectMapper.writeValueAsString(badRequest);

        mockMvc.perform(post("/stocks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest)
                )
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bankService, never()).initBankState(any());
    }

    @Test
    public void setBankInventoryValidRequestTest() throws Exception {
        List<StockDTO> mockStocks = List.of(
            new StockDTO("GOOG", 15),
            new StockDTO("AMAZ", 20)
        );

        BankInventoryInitRequest request = new BankInventoryInitRequest(mockStocks);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
            post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
            )
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("Success"))
            .andExpect(jsonPath("$.message").value("Bank state was initialized"));

        verify(bankService).initBankState(mockStocks);
    }

    @Test
    public void setBankInventoryThrowsStockNotFoundExceptionTest() throws Exception {
        String symbol = "GOOG";
        List<StockDTO> mockStocks = List.of(new StockDTO(symbol, 10));
        BankInventoryInitRequest request = new BankInventoryInitRequest(mockStocks);
        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new StockNotFoundException("Trigger was not executed: %s".formatted(symbol)))
            .when(bankService).initBankState(any());

        mockMvc.perform(
            post("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonRequest)
            )
            .andExpect(status().isNotFound()) 
            .andExpect(jsonPath("$.status").value("Error"))
            .andExpect(jsonPath("$.message").value("Trigger was not executed: %s".formatted(symbol)));
    }
}
