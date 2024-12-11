package org.olegi.testbankapi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.AccountUpdateDTO;
import org.olegi.testbankapi.exceptions.AccountAlreadyExistsException;
import org.olegi.testbankapi.exceptions.AccountNotFoundException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public String createAccount(AccountDTO accountDTO) {
        if (accountRepository.existsByAccountNumber(accountDTO.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Account with this number already exists");
        }
        Account newAccount = accountMapper.accountDTOToAccount(accountDTO);
        accountRepository.save(newAccount);
        log.info("Account successfully saved to DB");
        return newAccount.getAccountNumber();
    }

    public void updateAccount(String accountId, AccountUpdateDTO accountUpdateDTO) {
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

    public AccountDTO getAccountInfo(String accountId) {
        log.info("Trying to get account info with id {}", accountId);
        Account currentAccount = accountRepository.findByAccountNumber(accountId).orElseThrow(
                () -> new AccountNotFoundException(
                        String.format("Account '%s' not found", accountId))
        );
        return accountMapper.accountToAccountDTO(currentAccount);
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        log.info("Trying to delete account with id {}", accountNumber);
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            accountRepository.deleteByAccountNumber(accountNumber);
        } else throw new AccountNotFoundException(
                String.format("Account '%s' not found", accountNumber));
    }
}
