package org.finance.observer;
import org.finance.model.Budget;
/**
 * Observer interface for receiving budget-related notifications.
 * Implements the Observer pattern to notify about budget overruns.
 */
public interface BudgetObserver {
    /**
     * Notification when a budget is exceeded.
     */
    void onBudgetExceeded(Budget budget, java.math.BigDecimal overspentAmount);
    
    /**
     * Notification when a budget is near the limit (>= 90%).
     */
    void onBudgetNearLimit(Budget budget, java.math.BigDecimal remainingAmount);
    
    /**
     * Notification when an expense is made that impacts a budget.
     */
    void onExpenseAdded(Budget budget, java.math.BigDecimal expenseAmount);
}



