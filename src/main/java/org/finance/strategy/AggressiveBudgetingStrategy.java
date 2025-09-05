package org.finance.strategy;

import org.finance.model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

/**
 * Strategia di budgeting aggressiva.
 * Approccio che massimizza l'utilizzo delle entrate per spese e investimenti.
 */
public class AggressiveBudgetingStrategy implements BudgetingStrategy {
    
    private static final BigDecimal GROWTH_FACTOR = BigDecimal.valueOf(1.15); // 15% di crescita sulle spese storiche
    private static final BigDecimal MAX_CATEGORY_PERCENTAGE = BigDecimal.valueOf(0.40); // Max 40% per categoria

    @Override
    public Budget calculateSuggestedBudget(Category category, BigDecimal totalIncome,
                                         List<Transaction> historicalTransactions,
                                         YearMonth period, String currency) {
        
        // Calcola la media delle spese storiche per questa categoria
        BigDecimal averageExpense = calculateAverageExpenseForCategory(category, historicalTransactions);
        
        // Applica il fattore di crescita aggressivo
        BigDecimal aggressiveAmount = averageExpense.multiply(GROWTH_FACTOR);
        
        // Limita al massimo percentuale dell'entrata totale (più permissivo del conservativo)
        BigDecimal maxAllowed = totalIncome.multiply(MAX_CATEGORY_PERCENTAGE);
        BigDecimal suggestedAmount = aggressiveAmount.min(maxAllowed);
        
        // Se non ci sono dati storici, usa una percentuale più alta dell'entrata
        if (suggestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            suggestedAmount = totalIncome.multiply(BigDecimal.valueOf(0.10)); // 10% minimo
        }
        
        // Assicurati che sia almeno 20 EUR anche se non c'è storico
        if (suggestedAmount.compareTo(BigDecimal.valueOf(20)) < 0) {
            suggestedAmount = BigDecimal.valueOf(100); // Budget minimo aggressivo
        }
        
        return new Budget(category, suggestedAmount, period, currency);
    }

    private BigDecimal calculateAverageExpenseForCategory(Category category, List<Transaction> transactions) {
        List<Transaction> categoryTransactions = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .filter(t -> isSameCategoryOrSubcategory(t.getCategory(), category))
            .toList();
        
        if (categoryTransactions.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalExpense = categoryTransactions.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalExpense.divide(BigDecimal.valueOf(categoryTransactions.size()), 2, RoundingMode.HALF_UP);
    }

    private boolean isSameCategoryOrSubcategory(Category transactionCategory, Category targetCategory) {
        if (transactionCategory.equals(targetCategory)) {
            return true;
        }
        
        // Verifica se la categoria della transazione è una sottocategoria della categoria target
        return targetCategory.getAllSubcategories().contains(transactionCategory);
    }

    @Override
    public String getStrategyName() {
        return "Aggressive";
    }

    @Override
    public String getDescription() {
        return "Strategia aggressiva che massimizza l'utilizzo delle entrate con crescita del 15% " +
               "sulle spese storiche e permette fino al 40% dell'entrata per categoria.";
    }
}
