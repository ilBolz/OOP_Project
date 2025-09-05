package org.finance.strategy;
import org.finance.model.Budget;
import org.finance.model.Category;
import java.math.BigDecimal;
/**
 * Strategy interface for budgeting algorithms.
 * Implements the Strategy pattern for different budgeting approaches.
 */
public interface BudgetingStrategy {
    /**
     * Calculates the suggested budget for a category based on the implemented strategy.
     * 
     * @param category The category for which to calculate the budget
     * @param totalIncome The total income for the period
     * @return The suggested budget
     */
    Budget calculateSuggestedBudget(Category category, BigDecimal totalIncome);
    /**
     * Gets the strategy name.
     */
    String getStrategyName();
}



