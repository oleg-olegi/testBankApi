package org.olegi.testbankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.AccountUpdateDTO;
import org.olegi.testbankapi.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        accountDTO = new AccountDTO("G1234567890", new BigDecimal("1000.00"));
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        String accountId = "G1234567890";
        when(accountService.createAccount(any(AccountDTO.class))).thenReturn(accountId);

        mockMvc.perform(post("/api/account/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/accounts/" + accountId));
    }

    @Test
    void testCreateAccount_InvalidRequest() throws Exception {
        AccountDTO invalidAccountDTO = new AccountDTO("G1234567891", new BigDecimal("1000.00"));

        when(accountService.createAccount(any(AccountDTO.class))).thenThrow(new IllegalArgumentException("Invalid account number"));

        mockMvc.perform(post("/api/account/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAccountDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAccount_Success() throws Exception {
        AccountUpdateDTO updateDTO = new AccountUpdateDTO("G1234567891", new BigDecimal("2000.00"));

        mockMvc.perform(put("/api/account/updateAccount/{accountId}", "12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        Mockito.verify(accountService).updateAccount(eq("12345"), any(AccountUpdateDTO.class));
    }

    @Test
    void testGetAccountInfo_Success() throws Exception {
        when(accountService.getAccountInfo("G1234567890")).thenReturn(accountDTO);

        mockMvc.perform(get("/api/account/getAccountInfo/{accountId}", "G1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber").value("G1234567890"))
                .andExpect(jsonPath("$.balance").value(1000.00));
    }

    @Test
    void testGetAccountInfo_NotFound() throws Exception {
        when(accountService.getAccountInfo("G1234567890"))
                .thenThrow(new IllegalArgumentException("Account not found"));

        mockMvc.perform(get("/api/account/getAccountInfo/{accountId}", "G1234567890"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Account not found"));
    }

    @Test
    void testDeleteAccount_Success() throws Exception {
        mockMvc.perform(delete("/api/account/delete/{accountId}", "G1234567890"))
                .andExpect(status().isNoContent());

        Mockito.verify(accountService).deleteAccount("G1234567890");
    }
}
