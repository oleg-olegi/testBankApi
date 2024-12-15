package org.olegi.testbankapi.service;

import org.olegi.testbankapi.dto.AccountDTO;

public interface AccountService {
    String createAccount(AccountDTO accountDTO);

    void updateAccount(String accountNumber, AccountDTO accountDTO);

    void deleteAccount(String accountNumber);

    AccountDTO getAccountInfo(String accountNumber);
}
