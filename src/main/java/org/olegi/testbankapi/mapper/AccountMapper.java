package org.olegi.testbankapi.mapper;

import org.mapstruct.Mapper;
import org.olegi.testbankapi.dto.*;
import org.olegi.testbankapi.model.Account;

@Mapper
public interface AccountMapper {

    AccountDTO accountToAccountDTO(Account account);

    Account accountDTOToAccount(AccountDTO accountDTODTO);
}
