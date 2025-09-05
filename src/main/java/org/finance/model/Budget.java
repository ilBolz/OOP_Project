package org.finance.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a financial budget for a specific category in a time period.
 */
public class Budget {
    private final String id;
    private final Category category;
    private final BigDecimal amount;
    private final YearMonth period;
    private final String currency;
    private BigDecimal spent;
    private final LocalDate createdAt;

    public Budget(Category category, BigDecimal amount, YearMonth period, String currency) {
        this.id = UUID.randomUUID().toString();
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.period = Objects.requireNonNull(period, "Period cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.spent = BigDecimal.ZERO;
        this.createdAt = LocalDate.now();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amount must be positive");
        }
    }
    public Budget(String id, Category category, BigDecimal amount, YearMonth period) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.period = Objects.requireNonNull(period, "Period cannot be null");
        this.currency = "EUR"; // Default currency dal database
        this.spent = BigDecimal.ZERO;
        this.createdAt = LocalDate.now();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget amount must be positive");
        }
    }
    /**
     * Aggiunge una spesa al budget corrente.
     */
    public void addExpense(BigDecimal expenseAmount) {
        if (expenseAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Expense amount cannot be negative");
        }
        this.spent = this.spent.add(expenseAmount);
    }
    /**
     * Rimuove una spesa dal budget corrente (per undo operations).
     */
    public void removeExpense(BigDecimal expenseAmount) {
        if (expenseAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Expense amount cannot be negative");
        }
        this.spent = this.spent.subtract(expenseAmount);
        if (this.spent.compareTo(BigDecimal.ZERO) < 0) {
            this.spent = BigDecimal.ZERO;
        }
    }
    /**
     * Calcola l'importo rimanente nel budget.
     */
    public BigDecimal getRemainingAmount() {
        return amount.subtract(spent);
    }
    /**
     * Calcola la percentuale di budget utilizzata.
     */
    public BigDecimal getUsagePercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spent.divide(amount, 4, java.math.RoundingMode.HALF_UP)
                   .multiply(BigDecimal.valueOf(100));
    }
    /**
     * Verifica se il budget � stato superato.
     */
    public boolean isExceeded() {
        return spent.compareTo(amount) > 0;
    }
    /**
     * Verifica se il budget � vicino al limite (>= 90%).
     */
    public boolean isNearLimit() {
        return getUsagePercentage().compareTo(BigDecimal.valueOf(90)) >= 0;
    }
    /**
     * Resetta le spese del budget (per un nuovo periodo).
     */
    public void resetSpent() {
        this.spent = BigDecimal.ZERO;
    }
    public String getId() { return id; }
    public Category getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public YearMonth getPeriod() { return period; }
    public String getCurrency() { return currency; }
    public BigDecimal getSpent() { return spent; }
    public LocalDate getCreatedAt() { return createdAt; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(id, budget.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return String.format("Budget{category='%s', amount=%s, spent=%s, period=%s, remaining=%s}", 
                category.getName(), amount, spent, period, getRemainingAmount());
    }
}



