package org.olegi.testbankapi.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.exceptions.UserNotFoundException;
import org.olegi.testbankapi.mapper.AccountMapper;
import org.olegi.testbankapi.model.Account;
import org.olegi.testbankapi.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public void createAccount(AccountDTO accountDTO) {
        Account newAccount = accountMapper.INSTANCE.accountDTOToAccount(accountDTO);
        accountRepository.save(newAccount);
        log.info("Account successfully saved to DB");
    }

    public void updateUser(String accountId, AccountDTO accountDTO) {
        log.info("Trying to update User");
        Account currentAccount = accountRepository.findByAccountId(accountId).orElseThrow(
                () -> new UserNotFoundException(
                        String.format("User with accountId '%s' not found", accountId))
        );
        log.info("Data for updating {}", accountDTO);
        if (accountDTO.getAccountNumber() != null) {
            currentAccount.setAccountNumber(accountDTO.getAccountNumber());
        }
        accountRepository.save(currentAccount);
        log.info("Account successfully updated to DB");
    }
}
