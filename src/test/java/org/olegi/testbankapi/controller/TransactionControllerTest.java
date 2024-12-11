package org.olegi.testbankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.olegi.testbankapi.dto.*;
import org.olegi.testbankapi.enums.TransactionTypes;
import org.olegi.testbankapi.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepositDTO depositDTO;
    private WithdrawDTO withdrawDTO;
    private AccountDTO accountDTO;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        depositDTO = new DepositDTO("1234567890", new BigDecimal("500.00"));
        withdrawDTO = new WithdrawDTO("1234567890", new BigDecimal("500.00"));
        accountDTO = new AccountDTO("1234567890", new BigDecimal("1500.00"));
        transactionDTO = new TransactionDTO(new BigDecimal("500.00"), LocalDateTime.now(), TransactionTypes.DEPOSIT);
    }

    @Test
    void testDeposit_Success() throws Exception {
        Mockito.when(transactionService.deposit(any(DepositDTO.class))).thenReturn(accountDTO);

        mockMvc.perform(post("/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accountDTO)));
    }

    @Test
    void testWithdraw_Success() throws Exception {
        Mockito.when(transactionService.withdraw(any(WithdrawDTO.class))).thenReturn(accountDTO);

        mockMvc.perform(post("/api/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accountDTO)));
    }

    @Test
    void testGetBalance_Success() throws Exception {
        Mockito.when(transactionService.getBalance("1234567890")).thenReturn(new BigDecimal("1500.00"));

        mockMvc.perform(get("/api/transaction/balance")
                        .param("accountNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.00"));
    }

    @Test
    void testGetOperationsHistory_Success() throws Exception {
        Mockito.when(transactionService.getOperationHistory(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(transactionDTO));

        mockMvc.perform(get("/api/transaction/transactions")
                        .param("accountId", "1")
                        .param("from", LocalDateTime.now().minusDays(1).toString())
                        .param("to", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(transactionDTO))));
    }
}
