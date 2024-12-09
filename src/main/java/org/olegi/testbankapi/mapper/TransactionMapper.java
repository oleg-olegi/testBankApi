package org.olegi.testbankapi.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.olegi.testbankapi.dto.TransactionDTO;
import org.olegi.testbankapi.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionDTO toDto(Transaction transaction);

    Transaction fromDto(TransactionDTO transactionDTO);
}
