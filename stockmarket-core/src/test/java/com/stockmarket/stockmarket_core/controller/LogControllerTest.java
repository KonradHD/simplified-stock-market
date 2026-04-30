package com.stockmarket.stockmarket_core.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stockmarket.stockmarket_core.dto.log.LogDTO;
import static com.stockmarket.stockmarket_core.dto.log.LogDTO.createLogDTO;
import static com.stockmarket.stockmarket_core.dto.log.LogDTO.createLogWalletDTO;
import com.stockmarket.stockmarket_core.service.AuditLogService;
import com.stockmarket.stockmarket_core.utils.types.LogActionType;
import com.stockmarket.stockmarket_core.utils.types.LogStatus;

@WebMvcTest(LogController.class)
public class LogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogService logService;

    @Test
    public void checkEmptyLogsTest() throws Exception {
        when(logService.getLimitedLogsWithStatus(LogStatus.INFO, 10_000)).thenReturn(List.of());

        mockMvc.perform(get("/log")
                            .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.log").isEmpty());
    }


    @Test
    public void checkLogsSuccessTest() throws Exception {
        List<LogDTO> logs = List.of(
            createLogDTO(LogActionType.TRANSACTION_BUY, 1l, "GOOG"),
            createLogWalletDTO(LogActionType.WALLET_DELETE, 1L)
        );

        when(logService.getLimitedLogsWithStatus(LogStatus.INFO, 10_000)).thenReturn(logs);

        mockMvc.perform(get("/log")
                            .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.log.length()").value(2))
                        .andExpect(jsonPath("$.log[0].type").value("TRANSACTION_BUY"))
                        .andExpect(jsonPath("$.log[0].name").value("GOOG"))
                        .andExpect(jsonPath("$.log[1].wallet_id").value(1L))
                        .andExpect(jsonPath("$.log[1].name").doesNotExist());
    }
}
