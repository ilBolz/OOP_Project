package org.finance.model;

import java.math.BigDecimal;

/**
 * Implementazione di una transazione di spesa.
 * Utilizza il pattern Factory per la creazione attraverso TransactionFactory.
 */
public class ExpenseTransaction extends Transaction {

    public ExpenseTransaction(BigDecimal amount, String description, Category category, String currency) {
        super(amount, description, category, currency);
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
