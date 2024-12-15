package org.olegi.testbankapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.enums.TransactionTypes;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.model.Transaction;
import org.olegi.testbankapi.repository.AccountRepository;
import org.olegi.testbankapi.repository.TransactionRepository;
import org.olegi.testbankapi.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transferMoney(TransferRequestDTO transferRequestDTO) {
        log.info("Initiating transfer: {} from account {} to account {}",
                transferRequestDTO.getAmount(),
                transferRequestDTO.getFromAccountNumber(),
                transferRequestDTO.getToAccountNumber());

        if (transferRequestDTO.getAmount().compareTo(new BigDecimal(0)) <= 0) {
            log.error("Invalid transfer amount: {}", transferRequestDTO.getAmount());
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account accountFrom = accountRepository.findByAccountNumber(transferRequestDTO.getFromAccountNumber())
                .orElseThrow(() -> {
                    log.error("Account not found: {}", transferRequestDTO.getFromAccountNumber());
                    return new AccountNotFoundException("Account not found: " + transferRequestDTO.getFromAccountNumber());
                });

        Account accountTo = accountRepository.findByAccountNumber(transferRequestDTO.getToAccountNumber())
                .orElseThrow(() -> {
                    log.error("Account not found: {}", transferRequestDTO.getToAccountNumber());
                    return new AccountNotFoundException("Account not found: " + transferRequestDTO.getToAccountNumber());
                });

        log.info("Before transfer: Account {} balance: {}, Account {} balance: {}",
                accountFrom.getAccountNumber(), accountFrom.getBalance(),
                accountTo.getAccountNumber(), accountTo.getBalance());

        BigDecimal amount = transferRequestDTO.getAmount();

        if (accountFrom.getBalance().compareTo(amount) < 0) {
            log.error("Недостаточно средств на счете: {}. Текущий баланс: {}, сумма списания: {}",
                    accountFrom.getAccountNumber(), accountFrom.getBalance(), amount);
            throw new IllegalArgumentException("Insufficient funds for transfer amount");
        }

        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));

        Transaction transactionFrom = createTransactionEntity(amount, accountFrom);

        transactionRepository.save(transactionFrom);
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);

        log.info("After transfer: Account {} balance: {}, Account {} balance: {}",
                accountFrom.getAccountNumber(), accountFrom.getBalance(),
                accountTo.getAccountNumber(), accountTo.getBalance());

        log.info("Transfer completed successfully");
    }

    private static Transaction createTransactionEntity(BigDecimal amount, Account accountFrom) {
        Transaction transactionFrom = new Transaction();
        transactionFrom.setAmount(amount);
        transactionFrom.setTransactionType(TransactionTypes.TRANSFER);
        transactionFrom.setAccount(accountFrom);
        transactionFrom.setTimeStamp(LocalDateTime.now());
        return transactionFrom;
    }
}
