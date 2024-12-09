package org.olegi.testbankapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.DepositDTO;

import org.olegi.testbankapi.dto.TransferRequestDTO;
import org.olegi.testbankapi.dto.WithdrawDTO;
import org.olegi.testbankapi.model.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    DepositDTO accountToDepositDTO(Account account);

    WithdrawDTO accountToWithdrawDTO(Account account);

    TransferRequestDTO accountToTransferRequestDTO(Account account);

    AccountDTO accountToAccountDTO(Account account);

    Account accountDTOToAccount(AccountDTO accountDTODTO);
}
