package org.finance.iterator;

import org.finance.model.Transaction;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementazione del pattern Iterator per la navigazione storica delle transazioni.
 * Supporta la navigazione in ordine cronologico (normale e inverso).
 */
public class TransactionHistoryIterator implements Iterator<Transaction> {
    private final List<Transaction> transactions;
    private int currentIndex;
    private final boolean reverse;

    public TransactionHistoryIterator(List<Transaction> transactions, boolean reverse) {
        this.transactions = List.copyOf(transactions); // Copia immutabile per sicurezza
        this.reverse = reverse;
        this.currentIndex = reverse ? transactions.size() - 1 : 0;
    }

    public TransactionHistoryIterator(List<Transaction> transactions) {
        this(transactions, false); // Default: ordine cronologico normale
    }

    @Override
    public boolean hasNext() {
        if (reverse) {
            return currentIndex >= 0;
        } else {
            return currentIndex < transactions.size();
        }
    }

    @Override
    public Transaction next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more transactions available");
        }

        Transaction transaction = transactions.get(currentIndex);
        
        if (reverse) {
            currentIndex--;
        } else {
            currentIndex++;
        }
        
        return transaction;
    }

    /**
     * Resetta l'iteratore alla posizione iniziale.
     */
    public void reset() {
        this.currentIndex = reverse ? transactions.size() - 1 : 0;
    }

    /**
     * Ottiene la posizione corrente dell'iteratore.
     */
    public int getCurrentPosition() {
        if (reverse) {
            return transactions.size() - 1 - currentIndex;
        } else {
            return currentIndex;
        }
    }

    /**
     * Ottiene il numero totale di transazioni.
     */
    public int getTotalCount() {
        return transactions.size();
    }

    /**
     * Verifica se l'iteratore sta navigando in ordine inverso.
     */
    public boolean isReverse() {
        return reverse;
    }
}
