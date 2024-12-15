package org.olegi.testbankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.service.impl.TransferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @Autowired
    private TransferServiceImpl transferServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    private TransferRequestDTO transferRequestDTO;

    @BeforeEach
    void setUp() {
        transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setFromAccountNumber("1234567890");
        transferRequestDTO.setToAccountNumber("0987654321");
        transferRequestDTO.setAmount(new BigDecimal("500.00"));
    }

    @Test
    void testDoTransfer_Success() throws Exception {
        Mockito.doNothing().when(transferServiceImpl).transferMoney(any(TransferRequestDTO.class));

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transfer successful"));
    }

    @Test
    void testDoTransfer_Failed() throws Exception {
        Mockito.doThrow(IllegalArgumentException.class).when(transferServiceImpl).transferMoney(any(TransferRequestDTO.class));

        mockMvc.perform(post("/api/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: null"));
    }
}
