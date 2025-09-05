package org.finance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Implementazione di una transazione di spesa.
 * Utilizza il pattern Factory per la creazione attraverso TransactionFactory.
 */
public class ExpenseTransaction extends Transaction {

    public ExpenseTransaction(BigDecimal amount, String description, Category category, String currency) {
        super(amount, description, category, currency);
    }
    
    // Costruttore per il caricamento dal database
    public ExpenseTransaction(String id, BigDecimal amount, String description, Category category, String currency, LocalDateTime timestamp) {
        super(id, amount, description, category, currency, timestamp);
    }

    @Override
    protected void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be positive");
        }
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }

    @Override
    public BigDecimal getBalanceImpact() {
        return getAmount().negate(); // Le spese hanno impatto negativo sul bilancio
    }
}
