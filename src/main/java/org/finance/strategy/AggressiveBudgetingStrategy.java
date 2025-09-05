package org.finance.strategy;
import org.finance.model.*;
import java.math.BigDecimal;
import java.time.YearMonth;
/**
 * Aggressive budgeting strategy.
 * Approach that maximizes income utilization with high percentages.
 */
public class AggressiveBudgetingStrategy implements BudgetingStrategy {
    private static final BigDecimal AGGRESSIVE_PERCENTAGE = BigDecimal.valueOf(0.30);
    @Override
    public Budget calculateSuggestedBudget(Category category, BigDecimal totalIncome) {
        BigDecimal suggestedAmount = totalIncome.multiply(AGGRESSIVE_PERCENTAGE);
        if (suggestedAmount.compareTo(BigDecimal.valueOf(100)) < 0) {
            suggestedAmount = BigDecimal.valueOf(100);
        }
        return new Budget(category, suggestedAmount, YearMonth.now(), "EUR");
    }
    @Override
    public String getStrategyName() {
        return "Aggressive (30%)";
    }
}



