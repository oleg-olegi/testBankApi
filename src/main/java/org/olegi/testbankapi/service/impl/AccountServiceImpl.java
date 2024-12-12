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
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
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

    public void updateAccount(String accountId, AccountUpdateDTO accountUpdateDTO) {
        if (accountUpdateDTO == null) {
            throw new IllegalArgumentException("Account can not be null");
        }
        log.info("Trying to update account with id {}", accountId);
        Account currentAccount = accountRepository.findByAccountNumber(accountId).orElseThrow(
                () -> new AccountNotFoundException(
                        String.format("Account '%s' not found", accountId))
        );
        log.info("Data for updating {}", accountUpdateDTO);
        if (accountUpdateDTO.getAccountNumber() != null) {
            currentAccount.setAccountNumber(accountUpdateDTO.getAccountNumber());
        }
        if (accountUpdateDTO.getBalance() != null) {
            currentAccount.setBalance(accountUpdateDTO.getBalance());
        }
        accountRepository.save(currentAccount);
        log.info("Account successfully updated to DB");
    }

    @Override
    public AccountDTO getAccountInfo(String accountId) {
        log.info("Trying to get account info with id {}", accountId);
        Account currentAccount = accountRepository.findByAccountNumber(accountId).orElseThrow(
                () -> new AccountNotFoundException(
                        String.format("Account '%s' not found", accountId))
        );
        return accountMapper.accountToAccountDTO(currentAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber) {
        log.info("Trying to delete account with id {}", accountNumber);
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            accountRepository.deleteByAccountNumber(accountNumber);
        } else throw new AccountNotFoundException(
                String.format("Account '%s' not found", accountNumber));
    }
}
