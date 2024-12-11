package org.olegi.testbankapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column
    @PositiveOrZero(message = "Balance must be positive or zero")
    private BigDecimal balance;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id + ", " +
                "accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance + '}';
    }
}