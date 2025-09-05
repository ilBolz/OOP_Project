package org.finance.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for all financial transactions.
 * Implements the Template Method pattern for common transaction structure.
 */
public abstract class Transaction {
    private final String id;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime timestamp;
    private final Category category;
    private final String currency;

    protected Transaction(BigDecimal amount, String description, Category category, String currency) {
        this.id = UUID.randomUUID().toString();
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.timestamp = LocalDateTime.now();
        validateAmount(amount);
    }

    protected Transaction(String id, BigDecimal amount, String description, Category category, String currency, LocalDateTime timestamp) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        validateAmount(amount);
    }

    /**
     * Template method for amount validation.
     * Subclasses can implement specific validations.
     */
    protected abstract void validateAmount(BigDecimal amount);

    /**
     * Template method to get transaction type.
     */
    public abstract TransactionType getType();

    /**
     * Template method to calculate balance impact.
     * Positive for income, negative for expenses.
     */
    public abstract BigDecimal getBalanceImpact();

    public String getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Category getCategory() { return category; }
    public String getCurrency() { return currency; }

    public java.time.LocalDate getDate() { 
        return timestamp.toLocalDate(); 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', amount=%s, description='%s', category='%s', timestamp=%s}", 
                getClass().getSimpleName(), id, amount, description, category.getName(), timestamp);
    }
}



