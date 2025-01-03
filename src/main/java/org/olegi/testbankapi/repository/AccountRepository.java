package org.olegi.testbankapi.repository;

import org.olegi.testbankapi.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findById(Long accountId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsById(Long accountId);

    void deleteByAccountNumber(String accountNumber);
}
