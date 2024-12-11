package org.olegi.testbankapi.mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public AccountMapper accountMapper() {
        return Mappers.getMapper(AccountMapper.class);
    }

    @Bean
    public TransactionMapper transactionMapper() {
        return Mappers.getMapper(TransactionMapper.class);
    }
}
