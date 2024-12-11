package org.olegi.testbankapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.model.Transaction;


@Mapper
public interface TransactionMapper {
    @Mapping(source = "transaction.time_stamp", target = "timestamp")
    TransactionDTO transactionToTransactionDTO(Transaction transaction);
}
