package org.finance.observer;
import org.finance.model.Budget;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Subject for the Observer pattern that manages budget notifications.
 * Maintains a list of observers and notifies them when there are budget changes.
 */
public class BudgetNotificationSubject {
    private final List<BudgetObserver> observers;
    public BudgetNotificationSubject() {
        this.observers = new ArrayList<>();
    }
    /**
     * Adds an observer to the list.
     */
    public void addObserver(BudgetObserver observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    /**
     * Removes an observer from the list.
     */
    public void removeObserver(BudgetObserver observer) {
        observers.remove(observer);
    }
    /**
     * Notifies all observers of a budget exceeded.
     */
    public void notifyBudgetExceeded(Budget budget) {
        BigDecimal overspentAmount = budget.getSpent().subtract(budget.getAmount());
        for (BudgetObserver observer : observers) {
            try {
                observer.onBudgetExceeded(budget, overspentAmount);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    /**
     * Notifies all observers that a budget is near the limit.
     */
    public void notifyBudgetNearLimit(Budget budget) {
        for (BudgetObserver observer : observers) {
            try {
                observer.onBudgetNearLimit(budget, budget.getRemainingAmount());
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    /**
     * Notifies all observers of an expense added to a budget.
     */
    public void notifyExpenseAdded(Budget budget, BigDecimal expenseAmount) {
        for (BudgetObserver observer : observers) {
            try {
                observer.onExpenseAdded(budget, expenseAmount);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    /**
     * Processes an expense and checks if notifications should be sent.
     */
    public void processExpense(Budget budget, BigDecimal expenseAmount) {
        boolean wasNearLimit = budget.isNearLimit();
        boolean wasExceeded = budget.isExceeded();
        budget.addExpense(expenseAmount);
        notifyExpenseAdded(budget, expenseAmount);
        if (!wasExceeded && budget.isExceeded()) {
            notifyBudgetExceeded(budget);
        }
        else if (!wasNearLimit && budget.isNearLimit()) {
            notifyBudgetNearLimit(budget);
        }
    }
    /**
     * Gets the number of registered observers.
     */
    public int getObserverCount() {
        return observers.size();
    }
    /**
     * Removes all observers.
     */
    public void clearObservers() {
        observers.clear();
    }
}



