package org.finance.observer;

import org.finance.model.Budget;

/**
 * Interfaccia Observer per ricevere notifiche relative ai budget.
 * Implementa il pattern Observer per notificare i superamenti di budget.
 */
public interface BudgetObserver {
    
    /**
     * Notifica quando un budget viene superato.
     */
    void onBudgetExceeded(Budget budget, java.math.BigDecimal overspentAmount);
    
    /**
     * Notifica quando un budget si avvicina al limite (>= 90%).
     */
    void onBudgetNearLimit(Budget budget, java.math.BigDecimal remainingAmount);
    
    /**
     * Notifica quando viene effettuata una spesa che impatta un budget.
     */
    void onExpenseAdded(Budget budget, java.math.BigDecimal expenseAmount);
}
