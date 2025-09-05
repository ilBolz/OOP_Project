package org.finance.strategy;
import org.finance.model.*;
import java.math.BigDecimal;
import java.time.YearMonth;
/**
 * Conservative budgeting strategy.
 * Prudent approach that favors savings with low percentages.
 */
public class ConservativeBudgetingStrategy implements BudgetingStrategy {
    private static final BigDecimal CONSERVATIVE_PERCENTAGE = BigDecimal.valueOf(0.15);
    @Override
    public Budget calculateSuggestedBudget(Category category, BigDecimal totalIncome) {
        BigDecimal suggestedAmount = totalIncome.multiply(CONSERVATIVE_PERCENTAGE);
        if (suggestedAmount.compareTo(BigDecimal.valueOf(50)) < 0) {
            suggestedAmount = BigDecimal.valueOf(50);
        }
        return new Budget(category, suggestedAmount, YearMonth.now(), "EUR");
    }
    @Override
    public String getStrategyName() {
        return "Conservative (15%)";
    }
}



