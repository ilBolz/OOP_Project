package org.finance.model;
import java.math.BigDecimal;
/**
 * Implementation of an investment transaction.
 * Uses the Factory pattern for creation through TransactionFactory.
 */
public class InvestmentTransaction extends Transaction {
    public InvestmentTransaction(BigDecimal amount, String description, Category category, String currency) {
        super(amount, description, category, currency);
    }
    @Override
    protected void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Investment amount must be positive");
        }
    }
    @Override
    public TransactionType getType() {
        return TransactionType.INVESTMENT;
    }
    @Override
    public BigDecimal getBalanceImpact() {
        return getAmount().negate(); // Gli investimenti hanno impatto negativo sul bilancio corrente
    }
}



