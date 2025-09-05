package org.finance.strategy;

import org.finance.model.Budget;
import org.finance.model.Category;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Interfaccia Strategy per algoritmi di budgeting.
 * Implementa il pattern Strategy per diversi approcci al budgeting.
 */
public interface BudgetingStrategy {
    
    /**
     * Calcola il budget suggerito per una categoria basato sulla strategia implementata.
     * 
     * @param category La categoria per cui calcolare il budget
     * @param totalIncome L'entrata totale del periodo
     * @param historicalTransactions Lista delle transazioni storiche per analisi
     * @param period Il periodo per cui calcolare il budget
     * @param currency La valuta del budget
     * @return Il budget suggerito
     */
    Budget calculateSuggestedBudget(Category category, BigDecimal totalIncome, 
                                   List<org.finance.model.Transaction> historicalTransactions,
                                   YearMonth period, String currency);
    
    /**
     * Ottiene il nome della strategia.
     */
    String getStrategyName();
    
    /**
     * Ottiene una descrizione della strategia.
     */
    String getDescription();
}
