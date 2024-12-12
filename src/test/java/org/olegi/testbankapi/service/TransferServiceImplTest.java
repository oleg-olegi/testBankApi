package org.olegi.testbankapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.model.Transaction;
import org.olegi.testbankapi.repository.AccountRepository;
import org.olegi.testbankapi.repository.TransactionRepository;
import org.olegi.testbankapi.service.impl.TransferServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
public class TransferServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransferServiceImpl transferServiceImpl;

    private Account accountFrom;
    private Account accountTo;

    @BeforeEach
    public void setUp() {
        accountFrom = new Account();
        accountFrom.setAccountNumber("1234567890");
        accountFrom.setBalance(BigDecimal.valueOf(1000));
        accountTo = new Account();
        accountTo.setAccountNumber("0987654321");
        accountTo.setBalance(BigDecimal.valueOf(500));
    }

    @Test
    public void transferMoney_ShouldTransferAmountSuccessfully() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("1234567890", "0987654321", BigDecimal.valueOf(200));
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(accountTo));

        transferServiceImpl.transferMoney(transferRequestDTO);

        assertThat(accountFrom.getBalance()).isEqualTo(BigDecimal.valueOf(800));
        assertThat(accountTo.getBalance()).isEqualTo(BigDecimal.valueOf(700));
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenAmountIsZeroOrNegative() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("1234567890", "0987654321", BigDecimal.valueOf(0));

        assertThatThrownBy(() -> transferServiceImpl.transferMoney(transferRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than zero");
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenFromAccountNotFound() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("nonExistingAccount", "0987654321", BigDecimal.valueOf(200));
        when(accountRepository.findByAccountNumber("nonExistingAccount")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferServiceImpl.transferMoney(transferRequestDTO))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found: nonExistingAccount");
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenToAccountNotFound() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("1234567890", "nonExistingAccount", BigDecimal.valueOf(200));
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findByAccountNumber("nonExistingAccount")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferServiceImpl.transferMoney(transferRequestDTO))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("Account not found: nonExistingAccount");
    }

    @Test
    public void transferMoney_ShouldThrowException_WhenInsufficientFunds() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO("1234567890", "0987654321", BigDecimal.valueOf(2000));
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(accountFrom));
        when(accountRepository.findByAccountNumber("0987654321")).thenReturn(Optional.of(accountTo));

        assertThatThrownBy(() -> transferServiceImpl.transferMoney(transferRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient funds for transfer amount");
    }
}
