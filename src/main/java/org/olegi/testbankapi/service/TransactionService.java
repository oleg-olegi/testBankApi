package org.olegi.testbankapi.service;

import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.dto.DepositDTO;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.dto.WithdrawDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    BigDecimal getBalance(String accountNumber);

    AccountDTO deposit(DepositDTO depositDTO);

    AccountDTO withdraw(WithdrawDTO depositDTO);

    List<TransactionDTO> getOperationHistory(Long accountId, LocalDateTime from, LocalDateTime to);
}