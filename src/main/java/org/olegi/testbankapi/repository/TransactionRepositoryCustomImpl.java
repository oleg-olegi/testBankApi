package org.olegi.testbankapi.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.olegi.testbankapi.model.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TransactionRepositoryCustomImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Transaction> findByAccountIdAndTimestampBetweenCriteria(long accountId, LocalDateTime from, LocalDateTime to) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> criteriaQuery = criteriaBuilder.createQuery(Transaction.class);

        Root<Transaction> transaction = criteriaQuery.from(Transaction.class);

        Predicate accountIdPredicate = criteriaBuilder.equal(transaction.get("account").get("id"), accountId);
        Predicate timestampPredicate = criteriaBuilder.between(transaction.get("time_stamp"), from, to);

        criteriaQuery.where(criteriaBuilder.and(accountIdPredicate, timestampPredicate));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}