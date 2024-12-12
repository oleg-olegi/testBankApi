package org.olegi.testbankapi.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.AccountUpdateDTO;
import org.olegi.testbankapi.exceptions.AccountAlreadyExistsException;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.repository.AccountRepository;
import org.olegi.testbankapi.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createAccount(AccountDTO accountDTO) {
        if (accountDTO == null) {
            throw new IllegalArgumentException("Account can not be null");
        }
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Account with this number already exists");
        }
        Account newAccount = accountMapper.accountDTOToAccount(accountDTO);
        accountRepository.save(newAccount);
        log.info("Account successfully saved to DB");
        return newAccount.getAccountNumber();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateAccount(String accountNumber, AccountUpdateDTO accountUpdateDTO) {
        if (accountUpdateDTO == null) {
            throw new IllegalArgumentException("Account DTO can not be null");
        }
        log.info("Trying to update account with number {}", accountNumber);
        Account currentAccount = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountNotFoundException(
                        String.format("Account '%s' not found", accountNumber))
        );
        log.info("Data for updating {}", accountUpdateDTO);
        if (accountUpdateDTO.getAccountNumber() != null) {
            currentAccount.setAccountNumber(accountUpdateDTO.getAccountNumber());
        }
        accountRepository.save(currentAccount);
        log.info("Account successfully updated to DB");
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AccountDTO getAccountInfo(String accountId) {
        log.info("Trying to get account info with id {}", accountId);
        Account currentAccount = accountRepository.findByAccountNumber(accountId).orElseThrow(
                () -> new AccountNotFoundException(
                        String.format("Account '%s' not found", accountId))
        );
        return accountMapper.accountToAccountDTO(currentAccount);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteAccount(String accountNumber) {
        log.info("Trying to delete account with id {}", accountNumber);
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            accountRepository.deleteByAccountNumber(accountNumber);
        } else throw new AccountNotFoundException(
                String.format("Account '%s' not found", accountNumber));
    }
}
