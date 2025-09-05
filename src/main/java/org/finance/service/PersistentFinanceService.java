package org.finance.service;

import com.google.inject.Inject;
import org.finance.model.*;
import org.finance.observer.*;
import org.finance.repository.*;
import org.finance.strategy.*;
import org.finance.iterator.TransactionHistoryIterator;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service principale per la gestione delle operazioni finanziarie con persistenza.
 * Centralizza la logica di business e coordina i vari componenti del sistema.
 */
public class PersistentFinanceService {
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetNotificationSubject budgetNotificationSubject;
    private BudgetingStrategy budgetingStrategy;
    private String defaultCurrency;

    @Inject
    public PersistentFinanceService(TransactionRepository transactionRepository,
                                    BudgetRepository budgetRepository,
                                    CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.budgetNotificationSubject = new BudgetNotificationSubject();
        this.budgetingStrategy = new ConservativeBudgetingStrategy(); // Default conservativo
        this.defaultCurrency = "EUR";
        
        // Inizializza le categorie predefinite se non esistono
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            // Crea categorie predefinite
            Category alimentari = new Category("Alimentari", "Spese per cibo e bevande");
            Category trasporti = new Category("Trasporti", "Spese per trasporti e carburante");
            Category casa = new Category("Casa", "Spese per la casa");
            Category svago = new Category("Svago", "Spese per divertimento e tempo libero");
            Category lavoro = new Category("Lavoro", "Entrate da lavoro");
            
            categoryRepository.save(alimentari);
            categoryRepository.save(trasporti);
            categoryRepository.save(casa);
            categoryRepository.save(svago);
            categoryRepository.save(lavoro);
        }
    }

    // ===== GESTIONE TRANSAZIONI =====
    
    public void addTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        
        // Salva nel repository
        transactionRepository.save(transaction);
        
        // Se è una spesa, aggiorna i budget corrispondenti
        if (transaction instanceof ExpenseTransaction) {
            updateBudgetsForExpense((ExpenseTransaction) transaction);
        }
    }

    private void updateBudgetsForExpense(ExpenseTransaction expense) {
        // Trova tutti i budget per la categoria della spesa nel periodo corrente
        List<Budget> relevantBudgets = budgetRepository.findByCategory(expense.getCategory().getName())
                .stream()
                .filter(budget -> budget.getPeriod().equals(YearMonth.from(expense.getTimestamp())))
                .toList();

        for (Budget budget : relevantBudgets) {
            budget.addExpense(expense.getAmount());
            budgetRepository.save(budget); // Aggiorna il budget nel database
            
            // Notifica se il budget è superato o vicino al limite
            if (budget.isExceeded()) {
                budgetNotificationSubject.notifyBudgetExceeded(budget);
            } else if (budget.isNearLimit()) {
                budgetNotificationSubject.notifyBudgetNearLimit(budget);
            }
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByCategory(String categoryName) {
        return transactionRepository.findByCategory(categoryName);
    }

    public List<Transaction> getTransactionsByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    public void removeTransaction(String transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        if (transaction.isPresent()) {
            transactionRepository.deleteById(transactionId);
            
            // Se era una spesa, aggiorna i budget
            if (transaction.get() instanceof ExpenseTransaction) {
                // Rimuovi la spesa dai budget (logica inversa)
                rollbackBudgetsForExpense((ExpenseTransaction) transaction.get());
            }
        }
    }

    private void rollbackBudgetsForExpense(ExpenseTransaction expense) {
        List<Budget> relevantBudgets = budgetRepository.findByCategory(expense.getCategory().getName())
                .stream()
                .filter(budget -> budget.getPeriod().equals(YearMonth.from(expense.getTimestamp())))
                .toList();

        for (Budget budget : relevantBudgets) {
            budget.removeExpense(expense.getAmount());
            budgetRepository.save(budget);
        }
    }

    // ===== GESTIONE CATEGORIE =====
    
    public void addCategory(Category category) {
        Objects.requireNonNull(category, "Category cannot be null");
        categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findById(name);
    }

    public void removeCategory(String categoryName) {
        // Verifica che non ci siano transazioni associate
        List<Transaction> relatedTransactions = transactionRepository.findByCategory(categoryName);
        if (!relatedTransactions.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing transactions");
        }
        
        // Verifica che non ci siano budget associati
        List<Budget> relatedBudgets = budgetRepository.findByCategory(categoryName);
        if (!relatedBudgets.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing budgets");
        }
        
        categoryRepository.deleteById(categoryName);
    }

    // ===== GESTIONE BUDGET =====
    
    public void addBudget(Budget budget) {
        Objects.requireNonNull(budget, "Budget cannot be null");
        budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public List<Budget> getBudgetsByCategory(String categoryName) {
        return budgetRepository.findByCategory(categoryName);
    }

    public List<Budget> getBudgetsByPeriod(YearMonth period) {
        return budgetRepository.findByPeriod(period);
    }

    public List<Budget> getActiveBudgets() {
        return budgetRepository.findActiveBudgets();
    }

    public void removeBudget(String budgetId) {
        budgetRepository.deleteById(budgetId);
    }

    // ===== STATISTICHE E REPORT =====
    
    public BigDecimal getTotalIncome() {
        return getAllTransactions().stream()
                .filter(t -> t instanceof IncomeTransaction)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpenses() {
        return getAllTransactions().stream()
                .filter(t -> t instanceof ExpenseTransaction)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public Map<String, BigDecimal> getExpensesByCategory() {
        return getAllTransactions().stream()
                .filter(t -> t instanceof ExpenseTransaction)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    // ===== CONFIGURAZIONE =====
    
    public void setBudgetingStrategy(BudgetingStrategy strategy) {
        this.budgetingStrategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
    }

    public BudgetingStrategy getBudgetingStrategy() {
        return budgetingStrategy;
    }

    public void setDefaultCurrency(String currency) {
        this.defaultCurrency = Objects.requireNonNull(currency, "Currency cannot be null");
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    // ===== OSSERVATORI =====
    
    public void addBudgetObserver(BudgetObserver observer) {
        budgetNotificationSubject.addObserver(observer);
    }

    public void removeBudgetObserver(BudgetObserver observer) {
        budgetNotificationSubject.removeObserver(observer);
    }

    // ===== ITERATORE =====
    
    public TransactionHistoryIterator getTransactionIterator() {
        return new TransactionHistoryIterator(getAllTransactions());
    }

    // ===== SUGGERIMENTI BUDGET =====
    
    public Budget suggestBudgetForCategory(Category category, YearMonth period) {
        return budgetingStrategy.calculateSuggestedBudget(
                category, 
                getTotalIncome(), 
                getAllTransactions(), 
                period, 
                defaultCurrency
        );
    }
}
