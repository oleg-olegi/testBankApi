package org.olegi.testbankapi.service;

import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.*;
import org.olegi.testbankapi.enums.TransactionTypes;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.mapper.TransactionMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.model.Transaction;
import org.olegi.testbankapi.repository.AccountRepository;
import org.olegi.testbankapi.repository.TransactionRepository;
import org.olegi.testbankapi.repository.TransactionRepositoryCustomImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private final TransactionRepositoryCustomImpl transactionRepositoryCustom;

    public BigDecimal getBalance(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    @Transactional
    public AccountDTO deposit(DepositDTO depositDTO) {
        Account account = accountRepository.findByAccountNumber(depositDTO.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (depositDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionTypes.DEPOSIT);
        transaction.setAmount(depositDTO.getAmount());
        transaction.setTime_stamp(LocalDateTime.now());
        transaction.setAccount(account);

        account.setBalance(account.getBalance().add(depositDTO.getAmount()));
        account.getTransactions().add(transaction);

        log.info("Saving deposit transaction: {}", transaction);
        transactionRepository.save(transaction);
        log.info("Updating account: {}", account.getAccountNumber());
        accountRepository.save(account);

        return accountMapper.accountToAccountDTO(account);
    }

    @Transactional
    public AccountDTO withdraw(WithdrawDTO withdrawDTO) {
        Account account = accountRepository.findByAccountNumber(withdrawDTO.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (withdrawDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero");
        }
        if (account.getBalance().compareTo(withdrawDTO.getAmount()) < 0) {
            log.error("Недостаточно средств на счете: {}. Текущий баланс: {}, сумма списания: {}", withdrawDTO.getAccountNumber(), account.getBalance(), withdrawDTO.getAmount());
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionTypes.WITHDRAW);
        transaction.setAmount(withdrawDTO.getAmount());
        transaction.setTime_stamp(LocalDateTime.now());
        transaction.setAccount(account);

        account.setBalance(account.getBalance().subtract(withdrawDTO.getAmount()));
        account.getTransactions().add(transaction);

        log.info("Saving withdraw transaction: {}", transaction);
        transactionRepository.save(transaction);
        log.info("Updating account: {}", account.getAccountNumber());
        accountRepository.save(account);

        return accountMapper.accountToAccountDTO(account);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getOperationHistory(Long accountId,
                                                    LocalDateTime from,
                                                    LocalDateTime to) {
        OperationHistoryDTO operationHistoryDTO = processOperationHistoryDTO(accountId, from, to);

        Account account = accountRepository.findById(operationHistoryDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        log.info("Account ID: {}", account.getId());
        log.info("From Date: {}", operationHistoryDTO.getFromDate());
        log.info("To Date: {}", operationHistoryDTO.getToDate());

        List<Transaction> transactions = transactionRepositoryCustom.findByAccountIdAndTimestampBetweenCriteria(
                accountId, from, to
        );

        log.info("Get operation history: {}", transactions.size());
        return transactions
                .stream()
                .map(transactionMapper::transactionToTransactionDTO)
                .collect(Collectors.toList());
    }

    private OperationHistoryDTO processOperationHistoryDTO(Long accountNumber, LocalDateTime from, LocalDateTime to) {
        OperationHistoryDTO operationHistoryDTO = new OperationHistoryDTO();
        operationHistoryDTO.setAccountId(accountNumber);
        operationHistoryDTO.setFromDate(from);
        operationHistoryDTO.setToDate(to);
        return operationHistoryDTO;
    }
}
