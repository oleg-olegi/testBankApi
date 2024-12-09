package org.olegi.testbankapi.service;

import lombok.AllArgsConstructor;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.exceptions.DepositMustBePositiveException;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;

    public BigDecimal getBalance(String accountNumber) {
        return accountRepository.findById(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    public void deposit(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DepositMustBePositiveException("Amount must have a positive value.");
        }
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        BigDecimal updatedBalance = account.getBalance().add(amount);
        account.setBalance(updatedBalance);
        accountRepository.save(account);
    }

    public void withdraw(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DepositMustBePositiveException("Amount must have a positive value.");
        }
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        BigDecimal updatedBalance = account.getBalance().subtract(amount);
        if (updatedBalance.compareTo(amount) >= 0) {
            account.setBalance(updatedBalance);
            accountRepository.save(account);
        } else throw new RuntimeException("Balance is not enough to withdraw.");
    }

    public List<TransactionDTO> getStatement(String accountNumber, LocalDateTime from, LocalDateTime to) {
        return null;
    }
}
