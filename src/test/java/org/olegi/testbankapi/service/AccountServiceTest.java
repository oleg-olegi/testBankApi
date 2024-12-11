package org.olegi.testbankapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.AccountUpdateDTO;
import org.olegi.testbankapi.exceptions.AccountAlreadyExistsException;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.repository.AccountRepository;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @InjectMocks
    private AccountService accountService;
    private Account account;
    private AccountDTO accountDTO;
    private AccountUpdateDTO accountUpdateDTO;

    @BeforeEach
    void setUp() {
        account = new Account(1L, "1234567890", BigDecimal.valueOf(1000), null);
        accountDTO = new AccountDTO("1234567890", BigDecimal.valueOf(1000));
        accountUpdateDTO = new AccountUpdateDTO("0987654321", BigDecimal.valueOf(2000));
    }

    @Test
    void createAccount_Success() {
        when(accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())).thenReturn(false);
        when(accountMapper.accountDTOToAccount(accountDTO)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        String accountNumber = accountService.createAccount(accountDTO);
        assertEquals(account.getAccountNumber(), accountNumber);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void createAccount_AccountAlreadyExists() {
        when(accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())).thenReturn(true);
        assertThrows(AccountAlreadyExistsException.class, () -> accountService.createAccount(accountDTO));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateAccount_Success() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        accountService.updateAccount(account.getAccountNumber(), accountUpdateDTO);
        assertEquals(accountUpdateDTO.getAccountNumber(), account.getAccountNumber());
        assertEquals(accountUpdateDTO.getBalance(), account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void updateAccount_AccountNotFound() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.updateAccount(account.getAccountNumber(), accountUpdateDTO));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountInfo_Success() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDTO(account)).thenReturn(accountDTO);
        AccountDTO result = accountService.getAccountInfo(account.getAccountNumber());
        assertEquals(accountDTO, result);
    }

    @Test
    void getAccountInfo_AccountNotFound() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountInfo(account.getAccountNumber()));
    }

    @Test
    void deleteAccount_Success() {
        when(accountRepository.existsByAccountNumber(account.getAccountNumber())).thenReturn(true);
        accountService.deleteAccount(account.getAccountNumber());
        verify(accountRepository, times(1)).deleteByAccountNumber(account.getAccountNumber());
    }

    @Test
    void deleteAccount_AccountNotFound() {
        when(accountRepository.existsByAccountNumber(account.getAccountNumber())).thenReturn(false);
        assertThrows(AccountNotFoundException.class, () -> accountService.deleteAccount(account.getAccountNumber()));
        verify(accountRepository, never()).deleteByAccountNumber(anyString());
    }
    @Test
    public void testCreateAccount_NullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createAccount(null);
        });

        assertEquals("Account can not be null", exception.getMessage());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    public void testUpdateAccount_NullInput() {
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.updateAccount(null, accountUpdateDTO);
        });

        assertEquals("Account 'null' not found", exception.getMessage());
    }

    @Test
    public void testDeleteAccount_NullInput() {
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountService.deleteAccount(null);
        });

        assertEquals("Account 'null' not found", exception.getMessage());
        verify(accountRepository, times(0)).deleteByAccountNumber(any());
    }

    @Test
    public void testCreateAccount_MapperIntegration() {
        when(accountMapper.accountDTOToAccount(accountDTO)).thenReturn(account);

        accountService.createAccount(accountDTO);

        verify(accountMapper, times(1)).accountDTOToAccount(accountDTO);
    }

    @Test
    public void testGetAccountInfo_MapperIntegration() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.getAccountInfo(account.getAccountNumber());

        assertEquals(accountDTO, result);
        verify(accountMapper, times(1)).accountToAccountDTO(account);
    }
}

