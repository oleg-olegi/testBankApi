package org.olegi.testbankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.DepositDTO;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.dto.WithdrawDTO;
import org.olegi.testbankapi.enums.TransactionTypes;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.exceptions.DepositMustBePositiveException;
import org.olegi.testbankapi.exceptions.GlobalExceptionHandler;
import org.olegi.testbankapi.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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

    @Autowired
    private TransactionController transactionController;

    @MockitoBean
    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

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
        accountDTO = new AccountDTO("1234567890", new BigDecimal("500.00"));
        transactionDTO = new TransactionDTO(new BigDecimal("500.00"), LocalDateTime.now(), TransactionTypes.DEPOSIT);
    }

    @Test
    void testDeposit_Success() throws Exception {
        Mockito.when(transactionServiceImpl.deposit(any(DepositDTO.class))).thenReturn(accountDTO);

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accountDTO)));
    }

    @Test
    void testWithdraw_Success() throws Exception {
        Mockito.when(transactionServiceImpl.withdraw(any(WithdrawDTO.class))).thenReturn(accountDTO);

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(accountDTO)));
    }

    @Test
    void testGetBalance_Success() throws Exception {
        Mockito.when(transactionServiceImpl.getBalance("1234567890"))
                .thenReturn(new BigDecimal("1500.00"));

        mockMvc.perform(get("/api/transactions/balance")
                        .param("accountNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.00"));
    }

    @Test
    void testGetOperationsHistory_Success() throws Exception {
        Mockito.when(transactionServiceImpl.getOperationHistory(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(transactionDTO));

        mockMvc.perform(get("/api/transactions")
                        .param("accountId", "1")
                        .param("from", LocalDateTime.now().minusDays(1).toString())
                        .param("to", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(transactionDTO))));
    }

    @Test
    void testDeposit_InvalidAmount() throws Exception {
        Mockito.when(transactionController.deposit(any(DepositDTO.class)))
                .thenThrow(DepositMustBePositiveException.class);

        DepositDTO invalidDepositDTO = new DepositDTO("1234567890", new BigDecimal("-500.00"));

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDepositDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeposit_InvalidAccountNumber() throws Exception {
        Mockito.when(transactionServiceImpl.deposit(any(DepositDTO.class))).thenThrow(IllegalArgumentException.class);
        DepositDTO invalidDepositDTO = new DepositDTO("invalid_account", new BigDecimal("500.00"));

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDepositDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithdraw_InsufficientFunds() throws Exception {
        Mockito.when(transactionServiceImpl.withdraw(any(WithdrawDTO.class)))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: null"));
    }

    @Test
    void testWithdraw_InvalidAccountNumber() throws Exception {
        Mockito.when(transactionServiceImpl.withdraw(any(WithdrawDTO.class))).thenThrow(IllegalArgumentException.class);

        WithdrawDTO invalidWithdrawDTO = new WithdrawDTO("invalid_account", new BigDecimal("500.00"));

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWithdrawDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetBalance_AccountNotFound() throws Exception {
        Mockito.when(transactionServiceImpl.getBalance("nonexistent_account"))
                .thenThrow(AccountNotFoundException.class);
        mockMvc.perform(get("/api/transactions/balance")
                        .param("accountNumber", "nonexistent_account"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: null"));
    }

    @Test
    void testGetOperationsHistory_InvalidDateRange() throws Exception {
        LocalDateTime from = LocalDateTime.parse("2024-12-15T10:00:00");
        LocalDateTime to = LocalDateTime.parse("2024-12-14T10:00:00");

        Mockito.when(transactionServiceImpl.getOperationHistory(1L, from, to))
                .thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/transactions")
                        .param("accountId", "1")
                        .param("from", "2024-12-15T10:00:00")
                        .param("to", "2024-12-14T10:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: null"));
    }

    @Test
    void testGetOperationsHistory_AccountNotFound() throws Exception {
        Mockito.when(transactionServiceImpl.getOperationHistory(
                        any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get("/api/transactions")
                        .param("accountId", "999")
                        .param("from", LocalDateTime.now().minusDays(1).toString())
                        .param("to", LocalDateTime.now().toString()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found: Account not found"));
    }
}
