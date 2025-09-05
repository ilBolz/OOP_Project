package org.finance.model;

/**
 * Enum che definisce i tipi di transazione disponibili.
 */
public enum TransactionType {
    INCOME("Income"),
    EXPENSE("Expense"),
    INVESTMENT("Investment");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
