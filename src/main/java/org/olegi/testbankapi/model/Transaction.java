package org.olegi.testbankapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.olegi.testbankapi.enums.TransactionTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NonNull
    @PositiveOrZero(message = "Balance must be positive or zero")
    private BigDecimal amount;

    @NonNull
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time_stamp;

    @NonNull
    @Enumerated(EnumType.STRING)
    private TransactionTypes transactionType;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", time_stamp=" + time_stamp + '}';
    }
}