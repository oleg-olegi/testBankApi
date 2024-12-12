package org.olegi.testbankapi.repository;

import org.olegi.testbankapi.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(nativeQuery = true,
            value = """
                    SELECT * FROM transaction t 
                    WHERE t.account_id = :accountId 
                    AND t.time_stamp BETWEEN :from AND :to
                    """)
    List<Transaction> findByAccountIdAndTimestampBetween(
            @Param("accountId") long accountId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}