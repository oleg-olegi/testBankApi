package org.olegi.testbankapi.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.DepositDTO;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.dto.WithdrawDTO;
import org.olegi.testbankapi.enums.TransactionTypes;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.exceptions.DepositMustBePositiveException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.mapper.TransactionMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.model.Transaction;
import org.olegi.testbankapi.repository.AccountRepository;
import org.olegi.testbankapi.repository.TransactionRepository;
import org.olegi.testbankapi.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public BigDecimal getBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AccountDTO deposit(DepositDTO depositDTO) {
        Account account = getAccount(depositDTO.getAccountNumber());
        validateAmount(depositDTO.getAmount());

        Transaction transaction = createTransaction(TransactionTypes.DEPOSIT, depositDTO.getAmount(), account);
        account.setBalance(account.getBalance().add(depositDTO.getAmount()));

        return processTransaction(account, transaction);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AccountDTO withdraw(WithdrawDTO withdrawDTO) {
        Account account = getAccount(withdrawDTO.getAccountNumber());
        validateAmount(withdrawDTO.getAmount());

        if (account.getBalance().compareTo(withdrawDTO.getAmount()) < 0) {
            log.error("Недостаточно средств на счете: {}. Текущий баланс: {}, сумма списания: {}", withdrawDTO.getAccountNumber(), account.getBalance(), withdrawDTO.getAmount());
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }

        Transaction transaction = createTransaction(TransactionTypes.WITHDRAW, withdrawDTO.getAmount(), account);
        account.setBalance(account.getBalance().subtract(withdrawDTO.getAmount()));

        return processTransaction(account, transaction);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<TransactionDTO> getOperationHistory(Long accountId, LocalDateTime from, LocalDateTime to) {
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account not found: " + accountId);
        }

        log.info("Account ID: {}, From Date: {}, To Date: {}", accountId, from, to);
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("From Date is less than To");
        }
        List<Transaction> transactions = transactionRepository.findByAccountIdAndTimestampBetween(accountId, from, to);

        log.info("Found {} transactions", transactions.size());
        return transactions.stream()
                .map(transactionMapper::transactionToTransactionDTO)
                .collect(Collectors.toList());
    }

    private Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DepositMustBePositiveException("Amount must be greater than zero");
        }
    }

    private Transaction createTransaction(TransactionTypes transactionType, BigDecimal amount, Account account) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setTimeStamp(LocalDateTime.now());
        transaction.setAccount(account);
        return transaction;
    }

    private AccountDTO processTransaction(Account account, Transaction transaction) {
        log.info("Saving transaction: {}", transaction);
        transactionRepository.save(transaction);

        log.info("Updating account: {}", account.getAccountNumber());
        accountRepository.save(account);

        return accountMapper.accountToAccountDTO(account);
    }
}


