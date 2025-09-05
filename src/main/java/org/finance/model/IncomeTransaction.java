package org.finance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Implementazione di una transazione di entrata.
 * Utilizza il pattern Factory per la creazione attraverso TransactionFactory.
 */
public class IncomeTransaction extends Transaction {

    public IncomeTransaction(BigDecimal amount, String description, Category category, String currency) {
        super(amount, description, category, currency);
    }
    
    // Costruttore per il caricamento dal database
    public IncomeTransaction(String id, BigDecimal amount, String description, Category category, String currency, LocalDateTime timestamp) {
        super(id, amount, description, category, currency, timestamp);
    }

    @Override
    protected void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income amount must be positive");
        }
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }

    @Override
    public BigDecimal getBalanceImpact() {
        return getAmount(); // Le entrate hanno impatto positivo sul bilancio
    }
}
