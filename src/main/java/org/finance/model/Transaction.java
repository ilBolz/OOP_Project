package org.finance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe base astratta per tutte le transazioni finanziarie.
 * Implementa il pattern Template Method per la struttura comune delle transazioni.
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
    
    // Costruttore per il caricamento dal database
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
     * Template method per la validazione dell'importo.
     * Le sottoclassi possono implementare validazioni specifiche.
     */
    protected abstract void validateAmount(BigDecimal amount);

    /**
     * Template method per ottenere il tipo di transazione.
     */
    public abstract TransactionType getType();

    /**
     * Template method per calcolare l'impatto sul bilancio.
     * Positivo per entrate, negativo per spese.
     */
    public abstract BigDecimal getBalanceImpact();

    // Getters
    public String getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public Category getCategory() { return category; }
    public String getCurrency() { return currency; }
    
    // Metodo helper per ottenere la data (senza orario)
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
