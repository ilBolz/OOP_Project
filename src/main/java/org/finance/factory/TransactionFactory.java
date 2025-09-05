package org.finance.factory;

import org.finance.model.*;
import java.math.BigDecimal;

/**
 * Factory for the dynamic creation of transactions.
 */
public class TransactionFactory {

    /**
     * Creates a transaction of the specified type.
     *
     * @param type The type of transaction to create
     * @param amount The amount of the transaction
     * @param description The description of the transaction
     * @param category The category of the transaction
     * @param currency The currency of the transaction
     * @return The created transaction
     * @throws IllegalArgumentException if the type is not supported
     */

    public static Transaction createTransaction(TransactionType type, BigDecimal amount, 
                                               String description, Category category, String currency) {
        return switch (type) {
            case INCOME -> new IncomeTransaction(amount, description, category, currency);
            case EXPENSE -> new ExpenseTransaction(amount, description, category, currency);
            case INVESTMENT -> new InvestmentTransaction(amount, description, category, currency);
            default -> throw new IllegalArgumentException("Unsupported transaction type: " + type);
        };
    }

    /**
     * Create an income transaction.
     */
    public static IncomeTransaction createIncomeTransaction(BigDecimal amount, String description, 
                                                           Category category, String currency) {
        return new IncomeTransaction(amount, description, category, currency);
    }

    /**
     * Create an expense transaction.
     */
    public static ExpenseTransaction createExpenseTransaction(BigDecimal amount, String description, 
                                                             Category category, String currency) {
        return new ExpenseTransaction(amount, description, category, currency);
    }

    /**
     * Create an investment transaction.
     */
    public static InvestmentTransaction createInvestmentTransaction(BigDecimal amount, String description, 
                                                                   Category category, String currency) {
        return new InvestmentTransaction(amount, description, category, currency);
    }
}
