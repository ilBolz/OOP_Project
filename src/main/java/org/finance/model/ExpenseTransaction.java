package org.finance.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Implementation of an expense transaction.
 * Uses the Factory pattern for creation through TransactionFactory.
 */
public class ExpenseTransaction extends Transaction {
    public ExpenseTransaction(BigDecimal amount, String description, Category category, String currency) {
        super(amount, description, category, currency);
    }
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



