package org.olegi.testbankapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.olegi.testbankapi.dto.*;
import org.olegi.testbankapi.model.Account;

@Mapper
public interface AccountMapper {

    AccountDTO accountToAccountDTO(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Account accountDTOToAccount(AccountDTO accountDTODTO);
}
