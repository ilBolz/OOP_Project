package org.finance;

import org.finance.factory.TransactionFactory;
import org.finance.model.*;
import org.finance.observer.*;
import org.finance.strategy.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principale per testare il Personal Finance Manager.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Personal Finance Manager ===\n");
        
        // Test del pattern Composite (Categorie)
        testCategoryComposite();
        
        // Test del pattern Factory (Transazioni)
        testTransactionFactory();
        
        // Test del pattern Strategy (Budgeting)
        testBudgetingStrategies();
        
        // Test del pattern Observer (Notifiche Budget)
        testBudgetObserver();
    }

    private static void testCategoryComposite() {
        System.out.println("Test Pattern Composite - Categorie Gerarchiche");
        System.out.println("-".repeat(50));
        
        // Crea categorie principali
        Category casa = new Category("Casa", "Spese relative alla casa");
        
        // Crea sottocategorie per Casa
        Category affitto = new Category("Affitto", "Pagamento mensile affitto");
        Category bollette = new Category("Bollette", "Utenze varie");
        Category elettricita = new Category("Elettricità", "Bolletta elettrica");
        Category gas = new Category("Gas", "Bolletta gas");
        
        // Costruisci la gerarchia
        casa.addSubcategory(affitto);
        casa.addSubcategory(bollette);
        bollette.addSubcategory(elettricita);
        bollette.addSubcategory(gas);
        
        // Stampa la struttura gerarchica
        System.out.println("Struttura categorie:");
        System.out.println("- " + casa.getFullPath());
        System.out.println("  - " + affitto.getFullPath());
        System.out.println("  - " + bollette.getFullPath());
        System.out.println("    - " + elettricita.getFullPath());
        System.out.println("    - " + gas.getFullPath());
        
        System.out.println("\nSottocategorie di Casa: " + casa.getAllSubcategories().size());
        System.out.println("Pattern Composite testato con successo!\n");
    }

    private static void testTransactionFactory() {
        System.out.println("🏭 Test Pattern Factory - Creazione Transazioni");
        System.out.println("-".repeat(50));
        
        // Crea una categoria di esempio
        Category stipendio = new Category("Stipendio");
        Category spesa = new Category("Spesa Quotidiana");
        Category investimenti = new Category("Investimenti");
        
        // Test Factory Method
        Transaction income = TransactionFactory.createTransaction(
            TransactionType.INCOME, 
            BigDecimal.valueOf(2500.00), 
            "Stipendio mensile", 
            stipendio, 
            "EUR"
        );
        
        Transaction expense = TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(85.50), 
            "Spesa al supermercato", 
            spesa, 
            "EUR"
        );
        
        Transaction investment = TransactionFactory.createInvestmentTransaction(
            BigDecimal.valueOf(500.00), 
            "Acquisto ETF", 
            investimenti, 
            "EUR"
        );
        
        System.out.println("Transazioni create:");
        System.out.println("1. " + income);
        System.out.println("   Impatto bilancio: " + income.getBalanceImpact());
        System.out.println("2. " + expense);
        System.out.println("   Impatto bilancio: " + expense.getBalanceImpact());
        System.out.println("3. " + investment);
        System.out.println("   Impatto bilancio: " + investment.getBalanceImpact());
        
        System.out.println("Pattern Factory testato con successo!\n");
    }

    private static void testBudgetingStrategies() {
        System.out.println("Test Pattern Strategy - Strategie di Budgeting");
        System.out.println("-".repeat(50));
        
        Category alimentari = new Category("Alimentari");
        BigDecimal totalIncome = BigDecimal.valueOf(3000.00);
        List<Transaction> historicalTransactions = new ArrayList<>();
        
        // Aggiungi alcune transazioni storiche per il test
        historicalTransactions.add(TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(200.00), "Spesa 1", alimentari, "EUR"));
        historicalTransactions.add(TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(180.00), "Spesa 2", alimentari, "EUR"));
        historicalTransactions.add(TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(220.00), "Spesa 3", alimentari, "EUR"));
        
        YearMonth currentPeriod = YearMonth.now();
        
        // Test strategia conservativa
        BudgetingStrategy conservative = new ConservativeBudgetingStrategy();
        Budget conservativeBudget = conservative.calculateSuggestedBudget(
            alimentari, totalIncome, historicalTransactions, currentPeriod, "EUR");
        
        // Test strategia aggressiva
        BudgetingStrategy aggressive = new AggressiveBudgetingStrategy();
        Budget aggressiveBudget = aggressive.calculateSuggestedBudget(
            alimentari, totalIncome, historicalTransactions, currentPeriod, "EUR");
        
        System.out.println("Strategie di budgeting per categoria 'Alimentari':");
        System.out.println("Entrata totale: " + totalIncome + " EUR");
        System.out.println();
        System.out.println("Strategia Conservativa: " + conservative.getStrategyName());
        System.out.println("   " + conservative.getDescription());
        System.out.println("   Budget suggerito: " + conservativeBudget.getAmount() + " EUR");
        System.out.println();
        System.out.println("Strategia Aggressiva: " + aggressive.getStrategyName());
        System.out.println("   " + aggressive.getDescription());
        System.out.println("   Budget suggerito: " + aggressiveBudget.getAmount() + " EUR");
        
        System.out.println("Pattern Strategy testato con successo!\n");
    }

    private static void testBudgetObserver() {
        System.out.println("Test Pattern Observer - Notifiche Budget");
        System.out.println("-".repeat(50));
        
        // Crea un budget di test
        Category ristoranti = new Category("Ristoranti");
        Budget budget = new Budget(ristoranti, BigDecimal.valueOf(300.00), YearMonth.now(), "EUR");
        
        // Crea il subject e l'observer
        BudgetNotificationSubject notificationSubject = new BudgetNotificationSubject();
        ConsoleBudgetObserver consoleObserver = new ConsoleBudgetObserver();
        
        // Registra l'observer
        notificationSubject.addObserver(consoleObserver);
        
        // Simula delle spese che attivano le notifiche
        System.out.println("Budget iniziale: " + budget.getAmount() + " EUR per " + budget.getCategory().getName());
        System.out.println("Simulazione spese...\n");
        
        // Spesa normale
        notificationSubject.processExpense(budget, BigDecimal.valueOf(80.00));
        
        // Spesa che porta vicino al limite
        notificationSubject.processExpense(budget, BigDecimal.valueOf(150.00));
        
        // Spesa che supera il budget
        notificationSubject.processExpense(budget, BigDecimal.valueOf(100.00));
        
        System.out.println("Pattern Observer testato con successo!\n");
    }
}
