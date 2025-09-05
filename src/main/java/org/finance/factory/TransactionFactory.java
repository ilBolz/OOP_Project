package org.finance.factory;

import org.finance.model.*;
import java.math.BigDecimal;

/**
 * Factory per la creazione dinamica di transazioni.
 * Implementa il pattern Factory Method.
 */
public class TransactionFactory {

    /**
     * Crea una transazione del tipo specificato.
     * 
     * @param type Il tipo di transazione da creare
     * @param amount L'importo della transazione
     * @param description La descrizione della transazione
     * @param category La categoria della transazione
     * @param currency La valuta della transazione
     * @return La transazione creata
     * @throws IllegalArgumentException se il tipo non è supportato
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
     * Crea una transazione di entrata.
     */
    public static IncomeTransaction createIncomeTransaction(BigDecimal amount, String description, 
                                                           Category category, String currency) {
        return new IncomeTransaction(amount, description, category, currency);
    }

    /**
     * Crea una transazione di spesa.
     */
    public static ExpenseTransaction createExpenseTransaction(BigDecimal amount, String description, 
                                                             Category category, String currency) {
        return new ExpenseTransaction(amount, description, category, currency);
    }

    /**
     * Crea una transazione di investimento.
     */
    public static InvestmentTransaction createInvestmentTransaction(BigDecimal amount, String description, 
                                                                   Category category, String currency) {
        return new InvestmentTransaction(amount, description, category, currency);
    }
}
