package org.finance.service;

import org.finance.model.*;
import org.finance.observer.*;
import org.finance.strategy.*;
import org.finance.iterator.TransactionHistoryIterator;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service principale per la gestione delle operazioni finanziarie.
 * Centralizza la logica di business e coordina i vari componenti del sistema.
 */
public class FinanceService {
    private final List<Transaction> transactions;
    private final List<Budget> budgets;
    private final List<Category> categories;
    private final BudgetNotificationSubject budgetNotificationSubject;
    private BudgetingStrategy budgetingStrategy;
    private String defaultCurrency;

    public FinanceService() {
        this.transactions = new ArrayList<>();
        this.budgets = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.budgetNotificationSubject = new BudgetNotificationSubject();
        this.budgetingStrategy = new ConservativeBudgetingStrategy(); // Default conservativo
        this.defaultCurrency = "EUR";
    }

    // ===== GESTIONE TRANSAZIONI =====
    
    public void addTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        transactions.add(transaction);
        
        // Se è una spesa, aggiorna i budget corrispondenti
        if (transaction.getType() == TransactionType.EXPENSE) {
            updateBudgetsForExpense(transaction);
        }
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByCategory(Category category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equals(category) || 
                           category.getAllSubcategories().contains(t.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByPeriod(YearMonth period) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getTimestamp()).equals(period))
                .collect(Collectors.toList());
    }

    public List<Transaction> searchTransactions(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return transactions.stream()
                .filter(t -> t.getDescription().toLowerCase().contains(lowerSearchTerm) ||
                           t.getCategory().getName().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    public TransactionHistoryIterator getTransactionIterator(boolean reverse) {
        return new TransactionHistoryIterator(transactions, reverse);
    }

    // ===== GESTIONE BUDGET =====
    
    public void addBudget(Budget budget) {
        Objects.requireNonNull(budget, "Budget cannot be null");
        
        // Rimuovi budget esistenti per la stessa categoria e periodo
        budgets.removeIf(b -> b.getCategory().equals(budget.getCategory()) && 
                             b.getPeriod().equals(budget.getPeriod()));
        
        budgets.add(budget);
        
        // Aggiorna il budget con le spese esistenti
        updateBudgetWithExistingExpenses(budget);
    }

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }

    public List<Budget> getBudgetsByPeriod(YearMonth period) {
        return budgets.stream()
                .filter(b -> b.getPeriod().equals(period))
                .collect(Collectors.toList());
    }

    public Optional<Budget> getBudgetForCategory(Category category, YearMonth period) {
        return budgets.stream()
                .filter(b -> b.getCategory().equals(category) && b.getPeriod().equals(period))
                .findFirst();
    }

    public Budget createSuggestedBudget(Category category, YearMonth period) {
        BigDecimal totalIncome = calculateTotalIncomeForPeriod(period);
        List<Transaction> historicalTransactions = getHistoricalTransactionsForCategory(category);
        
        return budgetingStrategy.calculateSuggestedBudget(
                category, totalIncome, historicalTransactions, period, defaultCurrency);
    }

    private void updateBudgetsForExpense(Transaction expense) {
        YearMonth expenseMonth = YearMonth.from(expense.getTimestamp());
        
        // Trova budget corrispondenti (categoria o categoria padre)
        List<Budget> relevantBudgets = budgets.stream()
                .filter(b -> b.getPeriod().equals(expenseMonth))
                .filter(b -> b.getCategory().equals(expense.getCategory()) ||
                           isSubcategoryOf(expense.getCategory(), b.getCategory()))
                .collect(Collectors.toList());

        for (Budget budget : relevantBudgets) {
            budgetNotificationSubject.processExpense(budget, expense.getAmount());
        }
    }

    private void updateBudgetWithExistingExpenses(Budget budget) {
        List<Transaction> existingExpenses = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> YearMonth.from(t.getTimestamp()).equals(budget.getPeriod()))
                .filter(t -> t.getCategory().equals(budget.getCategory()) ||
                           isSubcategoryOf(t.getCategory(), budget.getCategory()))
                .collect(Collectors.toList());

        for (Transaction expense : existingExpenses) {
            budget.addExpense(expense.getAmount());
        }
    }

    private boolean isSubcategoryOf(Category child, Category parent) {
        return parent.getAllSubcategories().contains(child);
    }

    // ===== GESTIONE CATEGORIE =====
    
    public void addCategory(Category category) {
        Objects.requireNonNull(category, "Category cannot be null");
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public Optional<Category> findCategoryByName(String name) {
        return categories.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // ===== REPORT E STATISTICHE =====
    
    public Map<String, BigDecimal> getMonthlyBalance(YearMonth month) {
        List<Transaction> monthlyTransactions = getTransactionsByPeriod(month);
        
        BigDecimal income = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal investments = monthlyTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INVESTMENT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = income.subtract(expenses).subtract(investments);

        Map<String, BigDecimal> result = new HashMap<>();
        result.put("income", income);
        result.put("expenses", expenses);
        result.put("investments", investments);
        result.put("balance", balance);
        
        return result;
    }

    public Map<String, BigDecimal> getExpensesByCategory() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
    }

    public Map<YearMonth, BigDecimal> getMonthlyTrend(int months) {
        YearMonth startMonth = YearMonth.now().minusMonths(months - 1);
        Map<YearMonth, BigDecimal> trend = new LinkedHashMap<>();

        for (int i = 0; i < months; i++) {
            YearMonth month = startMonth.plusMonths(i);
            Map<String, BigDecimal> balance = getMonthlyBalance(month);
            trend.put(month, balance.get("balance"));
        }

        return trend;
    }

    public BigDecimal getTotalBalance() {
        return transactions.stream()
                .map(Transaction::getBalanceImpact)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===== GESTIONE OBSERVER =====
    
    public void addBudgetObserver(BudgetObserver observer) {
        budgetNotificationSubject.addObserver(observer);
    }

    public void removeBudgetObserver(BudgetObserver observer) {
        budgetNotificationSubject.removeObserver(observer);
    }

    // ===== GESTIONE STRATEGIA E IMPOSTAZIONI =====
    
    public void setBudgetingStrategy(BudgetingStrategy strategy) {
        this.budgetingStrategy = Objects.requireNonNull(strategy, "Strategy cannot be null");
    }

    public BudgetingStrategy getBudgetingStrategy() {
        return budgetingStrategy;
    }

    public void setDefaultCurrency(String currency) {
        this.defaultCurrency = Objects.requireNonNull(currency, "Currency cannot be null").toUpperCase();
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    // ===== METODI PRIVATI DI UTILITÀ =====
    
    private BigDecimal calculateTotalIncomeForPeriod(YearMonth period) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> YearMonth.from(t.getTimestamp()).equals(period))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Transaction> getHistoricalTransactionsForCategory(Category category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equals(category) ||
                           category.getAllSubcategories().contains(t.getCategory()))
                .collect(Collectors.toList());
    }

    // ===== METODI DI UTILITÀ PER STATISTICHE =====
    
    public int getTransactionCount() {
        return transactions.size();
    }

    public int getBudgetCount() {
        return budgets.size();
    }

    public int getCategoryCount() {
        return categories.size();
    }

    public List<Budget> getExceededBudgets() {
        return budgets.stream()
                .filter(Budget::isExceeded)
                .collect(Collectors.toList());
    }

    public List<Budget> getBudgetsNearLimit() {
        return budgets.stream()
                .filter(b -> b.isNearLimit() && !b.isExceeded())
                .collect(Collectors.toList());
    }

    /**
     * Resetta tutti i dati del servizio (utile per test o nuova sessione).
     */
    public void clearAllData() {
        transactions.clear();
        budgets.clear();
        categories.clear();
        budgetNotificationSubject.clearObservers();
    }
}
