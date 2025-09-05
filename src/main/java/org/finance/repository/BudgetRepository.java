package org.finance.repository;

import org.finance.model.Budget;
import java.time.YearMonth;
import java.util.List;

/**
 * Repository specifico per i budget.
 */
public interface BudgetRepository extends Repository<Budget, String> {
    
    /**
     * Trova budget per categoria.
     */
    List<Budget> findByCategory(String categoryName);
    
    /**
     * Trova budget per periodo.
     */
    List<Budget> findByPeriod(YearMonth period);
    
    /**
     * Trova budget attivi (non scaduti).
     */
    List<Budget> findActiveBudgets();
}
