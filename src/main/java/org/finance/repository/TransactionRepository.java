package org.finance.repository;
import org.finance.model.Transaction;
import java.time.LocalDate;
import java.util.List;
/**
 * Repository specifico per le transazioni con metodi di ricerca avanzati.
 */
public interface TransactionRepository extends Repository<Transaction, String> {
    /**
     * Trova transazioni per intervallo di date.
     */
    List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate);
    /**
     * Trova transazioni per categoria.
     */
    List<Transaction> findByCategory(String categoryName);
    /**
     * Trova transazioni per tipo (INCOME/EXPENSE).
     */
    List<Transaction> findByType(String type);
    /**
     * Trova transazioni per importo minimo.
     */
    List<Transaction> findByAmountGreaterThan(double amount);
}



