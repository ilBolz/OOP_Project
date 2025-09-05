package org.finance.observer;

import org.finance.model.Budget;
import org.finance.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@DisplayName("Budget Observer Tests - Pattern Observer")
class BudgetObserverTest {
    
    private BudgetNotificationSubject subject;
    private TestBudgetObserver observer1;
    private TestBudgetObserver observer2;
    private Budget budget;

    @BeforeEach
    void setUp() {
        subject = new BudgetNotificationSubject();
        observer1 = new TestBudgetObserver();
        observer2 = new TestBudgetObserver();
        
        Category ristoranti = new Category("Ristoranti");
        budget = new Budget(ristoranti, BigDecimal.valueOf(300.00), YearMonth.now(), "EUR");
    }

    @Test
    @DisplayName("Dovrebbe aggiungere e rimuovere observer correttamente")
    void shouldAddAndRemoveObserversCorrectly() {
        assertEquals(0, subject.getObserverCount());
        
        subject.addObserver(observer1);
        assertEquals(1, subject.getObserverCount());
        
        subject.addObserver(observer2);
        assertEquals(2, subject.getObserverCount());
        
        // Aggiungere lo stesso observer due volte non dovrebbe aumentare il count
        subject.addObserver(observer1);
        assertEquals(2, subject.getObserverCount());
        
        subject.removeObserver(observer1);
        assertEquals(1, subject.getObserverCount());
        
        subject.clearObservers();
        assertEquals(0, subject.getObserverCount());
    }

    @Test
    @DisplayName("Dovrebbe notificare quando il budget viene superato")
    void shouldNotifyWhenBudgetExceeded() {
        subject.addObserver(observer1);
        subject.addObserver(observer2);
        
        // Aggiungi una spesa che supera il budget
        subject.processExpense(budget, BigDecimal.valueOf(350.00));
        
        // Verifica che entrambi gli observer siano stati notificati
        assertTrue(observer1.budgetExceededCalled);
        assertTrue(observer2.budgetExceededCalled);
        assertEquals(BigDecimal.valueOf(50.00), observer1.lastOverspentAmount);
        assertEquals(BigDecimal.valueOf(50.00), observer2.lastOverspentAmount);
    }

    @Test
    @DisplayName("Dovrebbe notificare quando il budget si avvicina al limite")
    void shouldNotifyWhenBudgetNearLimit() {
        subject.addObserver(observer1);
        
        // Aggiungi una spesa che porta il budget al 95% (270/300)
        subject.processExpense(budget, BigDecimal.valueOf(285.00));
        
        // Verifica che l'observer sia stato notificato per "near limit"
        assertTrue(observer1.budgetNearLimitCalled);
        assertEquals(BigDecimal.valueOf(15.00), observer1.lastRemainingAmount);
    }

    @Test
    @DisplayName("Dovrebbe notificare ogni aggiunta di spesa")
    void shouldNotifyEveryExpenseAddition() {
        subject.addObserver(observer1);
        
        subject.processExpense(budget, BigDecimal.valueOf(50.00));
        assertTrue(observer1.expenseAddedCalled);
        assertEquals(BigDecimal.valueOf(50.00), observer1.lastExpenseAmount);
        
        // Reset per il secondo test
        observer1.reset();
        
        subject.processExpense(budget, BigDecimal.valueOf(75.00));
        assertTrue(observer1.expenseAddedCalled);
        assertEquals(BigDecimal.valueOf(75.00), observer1.lastExpenseAmount);
    }

    @Test
    @DisplayName("Non dovrebbe lanciare eccezioni se un observer fallisce")
    void shouldNotThrowExceptionIfObserverFails() {
        subject.addObserver(new FailingBudgetObserver());
        subject.addObserver(observer1);
        
        // Questo non dovrebbe lanciare eccezioni, anche se il primo observer fallisce
        assertDoesNotThrow(() -> {
            subject.processExpense(budget, BigDecimal.valueOf(100.00));
        });
        
        // Il secondo observer dovrebbe essere stato comunque notificato
        assertTrue(observer1.expenseAddedCalled);
    }

    @Test
    @DisplayName("Dovrebbe rifiutare observer null")
    void shouldRejectNullObserver() {
        assertThrows(NullPointerException.class, () -> {
            subject.addObserver(null);
        });
    }

    // Classe helper per i test
    private static class TestBudgetObserver implements BudgetObserver {
        boolean budgetExceededCalled = false;
        boolean budgetNearLimitCalled = false;
        boolean expenseAddedCalled = false;
        BigDecimal lastOverspentAmount;
        BigDecimal lastRemainingAmount;
        BigDecimal lastExpenseAmount;

        @Override
        public void onBudgetExceeded(Budget budget, BigDecimal overspentAmount) {
            budgetExceededCalled = true;
            lastOverspentAmount = overspentAmount;
        }

        @Override
        public void onBudgetNearLimit(Budget budget, BigDecimal remainingAmount) {
            budgetNearLimitCalled = true;
            lastRemainingAmount = remainingAmount;
        }

        @Override
        public void onExpenseAdded(Budget budget, BigDecimal expenseAmount) {
            expenseAddedCalled = true;
            lastExpenseAmount = expenseAmount;
        }

        void reset() {
            budgetExceededCalled = false;
            budgetNearLimitCalled = false;
            expenseAddedCalled = false;
            lastOverspentAmount = null;
            lastRemainingAmount = null;
            lastExpenseAmount = null;
        }
    }

    // Classe helper che simula un observer che fallisce
    private static class FailingBudgetObserver implements BudgetObserver {
        @Override
        public void onBudgetExceeded(Budget budget, BigDecimal overspentAmount) {
            throw new RuntimeException("Observer failure");
        }

        @Override
        public void onBudgetNearLimit(Budget budget, BigDecimal remainingAmount) {
            throw new RuntimeException("Observer failure");
        }

        @Override
        public void onExpenseAdded(Budget budget, BigDecimal expenseAmount) {
            throw new RuntimeException("Observer failure");
        }
    }
}
