package org.olegi.testbankapi.mapper;

import org.mapstruct.Mapper;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.model.Transaction;


@Mapper
public interface TransactionMapper {

    TransactionDTO toDto(Transaction transaction);

    Transaction fromDto(TransactionDTO transactionDTO);

    TransactionDTO transactionToTransactionDTO(Transaction transaction);
}
