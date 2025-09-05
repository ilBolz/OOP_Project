package org.finance.observer;

import org.finance.model.Budget;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Subject per il pattern Observer che gestisce le notifiche dei budget.
 * Mantiene una lista di observer e li notifica quando ci sono cambiamenti nei budget.
 */
public class BudgetNotificationSubject {
    private final List<BudgetObserver> observers;

    public BudgetNotificationSubject() {
        this.observers = new ArrayList<>();
    }

    /**
     * Aggiunge un observer alla lista.
     */
    public void addObserver(BudgetObserver observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Rimuove un observer dalla lista.
     */
    public void removeObserver(BudgetObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifica tutti gli observer di un superamento di budget.
     */
    public void notifyBudgetExceeded(Budget budget) {
        BigDecimal overspentAmount = budget.getSpent().subtract(budget.getAmount());
        for (BudgetObserver observer : observers) {
            try {
                observer.onBudgetExceeded(budget, overspentAmount);
            } catch (Exception e) {
                // Log dell'errore ma continua con gli altri observer
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    /**
     * Notifica tutti gli observer che un budget si avvicina al limite.
     */
    public void notifyBudgetNearLimit(Budget budget) {
        for (BudgetObserver observer : observers) {
            try {
                observer.onBudgetNearLimit(budget, budget.getRemainingAmount());
            } catch (Exception e) {
                // Log dell'errore ma continua con gli altri observer
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    /**
     * Notifica tutti gli observer dell'aggiunta di una spesa a un budget.
     */
    public void notifyExpenseAdded(Budget budget, BigDecimal expenseAmount) {
        for (BudgetObserver observer : observers) {
            try {
                observer.onExpenseAdded(budget, expenseAmount);
            } catch (Exception e) {
                // Log dell'errore ma continua con gli altri observer
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    /**
     * Processa una spesa e verifica se devono essere inviate notifiche.
     */
    public void processExpense(Budget budget, BigDecimal expenseAmount) {
        boolean wasNearLimit = budget.isNearLimit();
        boolean wasExceeded = budget.isExceeded();
        
        budget.addExpense(expenseAmount);
        
        // Notifica l'aggiunta della spesa
        notifyExpenseAdded(budget, expenseAmount);
        
        // Verifica se ora il budget è superato
        if (!wasExceeded && budget.isExceeded()) {
            notifyBudgetExceeded(budget);
        }
        // Verifica se ora il budget è vicino al limite
        else if (!wasNearLimit && budget.isNearLimit()) {
            notifyBudgetNearLimit(budget);
        }
    }

    /**
     * Ottiene il numero di observer registrati.
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Rimuove tutti gli observer.
     */
    public void clearObservers() {
        observers.clear();
    }
}
