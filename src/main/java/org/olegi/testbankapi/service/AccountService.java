package org.olegi.testbankapi.service;

import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.AccountUpdateDTO;

public interface AccountService {
    String createAccount(AccountDTO accountDTO);

    void updateAccount(String accountId, AccountUpdateDTO accountUpdateDTO);

    void deleteAccount(String accountId);

    AccountDTO getAccountInfo(String accountId);
}
