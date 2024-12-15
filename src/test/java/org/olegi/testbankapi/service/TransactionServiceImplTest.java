package org.olegi.testbankapi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.olegi.testbankapi.dto.*;
import org.olegi.testbankapi.enums.TransactionTypes;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.mapper.TransactionMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.model.Transaction;
import org.olegi.testbankapi.repository.AccountRepository;
import org.olegi.testbankapi.repository.TransactionRepository;
import org.olegi.testbankapi.service.impl.TransactionServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
class TransactionServiceImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("mydb")
                    .withUsername("myuser")
                    .withPassword("mypass");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account account;
    private DepositDTO depositDTO;
    private WithdrawDTO withdrawDTO;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        account = new Account(1L, "1234567890", new BigDecimal("1000.00"), Collections.emptyList());
        depositDTO = new DepositDTO("1234567890", new BigDecimal("500.00"));
        withdrawDTO = new WithdrawDTO("1234567890", new BigDecimal("300.00"));

        transaction = new Transaction();
        transaction.setTransactionType(TransactionTypes.DEPOSIT);
        transaction.setAmount(depositDTO.getAmount());
        transaction.setTimeStamp(LocalDateTime.now());
        transaction.setAccount(account);
    }

    @AfterEach
    void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    void testGetBalance_Success() {
        when(accountRepository.findByAccountNumber("1234567890")).thenReturn(Optional.of(account));

        BigDecimal balance = transactionService.getBalance(account.getAccountNumber());

        assertEquals(account.getBalance(), balance);
        verify(accountRepository, times(1)).findByAccountNumber(account.getAccountNumber());
    }

    @Test
    void testGetBalance_AccountNotFound() {
        when(accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.getBalance(account.getAccountNumber());
        });

        assertEquals("Account not found: 1234567890", exception.getMessage());
        verify(accountRepository, times(1)).findByAccountNumber(account.getAccountNumber());
    }

    @Test
    void testDeposit_Success() {
        when(accountRepository.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDTO(account))
                .thenReturn(new AccountDTO(account.getAccountNumber(), depositDTO.getAmount()));

        AccountDTO result = transactionService.deposit(depositDTO);

        assertEquals(new BigDecimal("500.00"), result.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testDeposit_AccountNotFound() {
        when(accountRepository.findByAccountNumber(depositDTO.getAccountNumber())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.deposit(depositDTO);
        });

        assertEquals("Account not found: 1234567890", exception.getMessage());
        verify(transactionRepository, times(0)).save(any());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void testWithdraw_Success() {
        when(accountRepository.findByAccountNumber(withdrawDTO.getAccountNumber())).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDTO(account))
                .thenReturn(new AccountDTO(
                        account.getAccountNumber(),
                        account.getBalance().subtract(withdrawDTO.getAmount())));

        AccountDTO result = transactionService.withdraw(withdrawDTO);

        assertEquals(new BigDecimal("700.00"), result.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        withdrawDTO.setAmount(new BigDecimal("2000.00"));

        when(accountRepository.findByAccountNumber(withdrawDTO.getAccountNumber())).thenReturn(Optional.of(account));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdraw(withdrawDTO);
        });

        assertEquals("Insufficient funds for withdrawal", exception.getMessage());
        verify(transactionRepository, times(0)).save(any());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void testGetOperationHistory_Success() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();

        when(accountRepository.existsById(account.getId())).thenReturn(true);
        when(transactionRepository.findByAccountIdAndTimestampBetween(account.getId(), from, to))
                .thenReturn(List.of(transaction));
        when(transactionMapper.transactionToTransactionDTO(transaction))
                .thenReturn(new TransactionDTO(
                        transaction.getAmount(),
                        transaction.getTimeStamp(),
                        transaction.getTransactionType()));

        List<TransactionDTO> result = transactionService.getOperationHistory(account.getId(), from, to);

        assertEquals(1, result.size());
        assertEquals(transaction.getAmount(), result.get(0).getAmount());
        verify(transactionRepository, times(1))
                .findByAccountIdAndTimestampBetween(account.getId(), from, to);
    }

    @Test
    void testGetOperationHistory_AccountNotFound() {
        LocalDateTime from = LocalDateTime.now().minusDays(7);
        LocalDateTime to = LocalDateTime.now();

        when(accountRepository.findById(account.getId())).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.getOperationHistory(account.getId(), from, to);
        });

        assertEquals("Account not found: 1", exception.getMessage());
        verify(transactionRepository, times(0))
                .findByAccountIdAndTimestampBetween(account.getId(), from, to);
    }
}