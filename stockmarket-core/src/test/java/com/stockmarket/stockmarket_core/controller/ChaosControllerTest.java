package com.stockmarket.stockmarket_core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.stockmarket.stockmarket_core.service.SystemTerminatorService;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChaosController.class)
class ChaosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SystemTerminatorService terminatorService;

    @Test
    void shouldReturnAcceptedAndScheduleShutdown() throws Exception {
        mockMvc.perform(post("/chaos"))
                    .andExpect(status().isAccepted()); 

        verify(terminatorService).scheduleShutdown();
    }
}
