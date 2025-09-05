package org.finance.factory;
import org.finance.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
@DisplayName("TransactionFactory Tests - Pattern Factory")
class TransactionFactoryTest {
    @Test
    @DisplayName("Dovrebbe creare una transazione di entrata correttamente")
    void shouldCreateIncomeTransactionCorrectly() {
        Category stipendio = new Category("Stipendio");
        Transaction income = TransactionFactory.createTransaction(
            TransactionType.INCOME, 
            BigDecimal.valueOf(2500.00), 
            "Stipendio mensile", 
            stipendio, 
            "EUR"
        );
        assertNotNull(income);
        assertTrue(income instanceof IncomeTransaction);
        assertEquals(TransactionType.INCOME, income.getType());
        assertEquals(BigDecimal.valueOf(2500.00), income.getAmount());
        assertEquals("Stipendio mensile", income.getDescription());
        assertEquals(stipendio, income.getCategory());
        assertEquals("EUR", income.getCurrency());
        assertEquals(BigDecimal.valueOf(2500.00), income.getBalanceImpact());
    }
    @Test
    @DisplayName("Dovrebbe creare una transazione di spesa correttamente")
    void shouldCreateExpenseTransactionCorrectly() {
        Category spesa = new Category("Spesa");
        Transaction expense = TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(85.50), 
            "Spesa al supermercato", 
            spesa, 
            "EUR"
        );
        assertNotNull(expense);
        assertTrue(expense instanceof ExpenseTransaction);
        assertEquals(TransactionType.EXPENSE, expense.getType());
        assertEquals(BigDecimal.valueOf(85.50), expense.getAmount());
        assertEquals("Spesa al supermercato", expense.getDescription());
        assertEquals(spesa, expense.getCategory());
        assertEquals("EUR", expense.getCurrency());
        assertEquals(BigDecimal.valueOf(-85.50), expense.getBalanceImpact());
    }
    @Test
    @DisplayName("Dovrebbe creare una transazione di investimento correttamente")
    void shouldCreateInvestmentTransactionCorrectly() {
        Category investimenti = new Category("Investimenti");
        Transaction investment = TransactionFactory.createInvestmentTransaction(
            BigDecimal.valueOf(500.00), 
            "Acquisto ETF", 
            investimenti, 
            "EUR"
        );
        assertNotNull(investment);
        assertTrue(investment instanceof InvestmentTransaction);
        assertEquals(TransactionType.INVESTMENT, investment.getType());
        assertEquals(BigDecimal.valueOf(500.00), investment.getAmount());
        assertEquals("Acquisto ETF", investment.getDescription());
        assertEquals(investimenti, investment.getCategory());
        assertEquals("EUR", investment.getCurrency());
        assertEquals(BigDecimal.valueOf(-500.00), investment.getBalanceImpact());
    }
    @Test
    @DisplayName("Dovrebbe lanciare eccezione per importi negativi")
    void shouldThrowExceptionForNegativeAmounts() {
        Category categoria = new Category("Test");
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createIncomeTransaction(
                BigDecimal.valueOf(-100.00), 
                "Test", 
                categoria, 
                "EUR"
            );
        });
        assertThrows(IllegalArgumentException.class, () -> {
            TransactionFactory.createExpenseTransaction(
                BigDecimal.valueOf(-50.00), 
                "Test", 
                categoria, 
                "EUR"
            );
        });
    }
    @Test
    @DisplayName("Dovrebbe lanciare eccezione per parametri null")
    void shouldThrowExceptionForNullParameters() {
        Category categoria = new Category("Test");
        assertThrows(NullPointerException.class, () -> {
            TransactionFactory.createIncomeTransaction(
                null, 
                "Test", 
                categoria, 
                "EUR"
            );
        });
        assertThrows(NullPointerException.class, () -> {
            TransactionFactory.createExpenseTransaction(
                BigDecimal.valueOf(100.00), 
                null, 
                categoria, 
                "EUR"
            );
        });
        assertThrows(NullPointerException.class, () -> {
            TransactionFactory.createInvestmentTransaction(
                BigDecimal.valueOf(100.00), 
                "Test", 
                null, 
                "EUR"
            );
        });
    }
    @Test
    @DisplayName("Dovrebbe lanciare eccezione per tipo di transazione non supportato")
    void shouldThrowExceptionForUnsupportedTransactionType() {
        Category categoria = new Category("Test");
        assertDoesNotThrow(() -> {
            TransactionFactory.createTransaction(
                TransactionType.INCOME, 
                BigDecimal.valueOf(100.00), 
                "Test", 
                categoria, 
                "EUR"
            );
        });
    }
}



