package org.finance.iterator;

import org.finance.model.Transaction;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of the Iterator pattern for historical navigation of transactions.
 * Supports navigation in chronological order (normal and reverse).
 */
public class TransactionHistoryIterator implements Iterator<Transaction> {
    private final List<Transaction> transactions;
    private int currentIndex;
    private final boolean reverse;

    public TransactionHistoryIterator(List<Transaction> transactions, boolean reverse) {
        this.transactions = List.copyOf(transactions);
        this.reverse = reverse;
        this.currentIndex = reverse ? transactions.size() - 1 : 0;
    }

    public TransactionHistoryIterator(List<Transaction> transactions) {
        this(transactions, false);
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
}
