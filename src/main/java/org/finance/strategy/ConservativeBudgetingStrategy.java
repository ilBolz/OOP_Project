package org.finance.strategy;

import org.finance.model.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

/**
 * Strategia di budgeting conservativa.
 * Approccio prudente che favorisce il risparmio e limita le spese.
 */
public class ConservativeBudgetingStrategy implements BudgetingStrategy {
    
    private static final BigDecimal SAFETY_MARGIN = BigDecimal.valueOf(0.85); // 15% di margine di sicurezza
    private static final BigDecimal MAX_CATEGORY_PERCENTAGE = BigDecimal.valueOf(0.25); // Max 25% per categoria

    @Override
    public Budget calculateSuggestedBudget(Category category, BigDecimal totalIncome,
                                         List<Transaction> historicalTransactions,
                                         YearMonth period, String currency) {
        
        // Calcola la media delle spese storiche per questa categoria
        BigDecimal averageExpense = calculateAverageExpenseForCategory(category, historicalTransactions);
        
        // Applica il margine di sicurezza conservativo
        BigDecimal conservativeAmount = averageExpense.multiply(SAFETY_MARGIN);
        
        // Limita al massimo percentuale dell'entrata totale
        BigDecimal maxAllowed = totalIncome.multiply(MAX_CATEGORY_PERCENTAGE);
        BigDecimal suggestedAmount = conservativeAmount.min(maxAllowed);
        
        // Assicurati che non sia negativo o zero
        if (suggestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            suggestedAmount = totalIncome.multiply(BigDecimal.valueOf(0.05)); // 5% minimo
        }
        
        // Assicurati che sia almeno 10 EUR anche se non c'è storico
        if (suggestedAmount.compareTo(BigDecimal.valueOf(10)) < 0) {
            suggestedAmount = BigDecimal.valueOf(50); // Budget minimo conservativo
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
        return "Conservative";
    }

    @Override
    public String getDescription() {
        return "Strategia conservativa che favorisce il risparmio con margini di sicurezza del 15% " +
               "e limita ogni categoria al massimo 25% dell'entrata totale.";
    }
}
