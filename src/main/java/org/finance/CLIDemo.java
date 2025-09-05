package org.finance;

import org.finance.config.DefaultCategories;
import org.finance.factory.TransactionFactory;
import org.finance.model.*;
import org.finance.service.FinanceService;
import org.finance.strategy.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dimostrazione completa delle funzionalità CLI del Personal Finance Manager.
 */
public class CLIDemo {
    
    public static void main(String[] args) {
        System.out.println("🏦 PERSONAL FINANCE MANAGER - CLI DEMO");
        System.out.println("=".repeat(60));
        
        // Inizializza il servizio
        FinanceService service = new FinanceService();
        
        // Carica categorie predefinite
        List<Category> categories = DefaultCategories.createDefaultCategories();
        for (Category category : categories) {
            service.addCategory(category);
        }
        
        System.out.println("Sistema inizializzato con " + categories.size() + " categorie predefinite");
        
        // Demo aggiunta transazioni
        demoTransactions(service, categories);
        
        // Demo budget
        demoBudgets(service, categories);
        
        // Demo report
        demoReports(service);
        
        // Demo strategie
        demoStrategies(service, categories);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Demo completata! Ecco come utilizzare la CLI:");
        System.out.println("1. Compila: mvn compile");
        System.out.println("2. Avvia CLI: mvn exec:java -Dexec.mainClass=\"org.finance.CLIMain\"");
        System.out.println("3. Segui il menu interattivo per gestire le tue finanze!");
        System.out.println("=".repeat(60));
    }
    
    private static void demoTransactions(FinanceService service, List<Category> categories) {
        System.out.println("\nDEMO: Aggiunta Transazioni");
        System.out.println("-".repeat(40));
        
        // Trova alcune categorie di esempio
        Category stipendio = findCategoryByName(categories, "Stipendio");
        Category ristoranti = findCategoryByName(categories, "Ristoranti");
        Category carburante = findCategoryByName(categories, "Carburante");
        Category etf = findCategoryByName(categories, "ETF");
        
        // Aggiungi transazioni di esempio
        Transaction income1 = TransactionFactory.createIncomeTransaction(
            BigDecimal.valueOf(2800.00), "Stipendio marzo", stipendio, "EUR");
        service.addTransaction(income1);
        
        Transaction expense1 = TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(45.80), "Cena con amici", ristoranti, "EUR");
        service.addTransaction(expense1);
        
        Transaction expense2 = TransactionFactory.createExpenseTransaction(
            BigDecimal.valueOf(65.50), "Rifornimento auto", carburante, "EUR");
        service.addTransaction(expense2);
        
        Transaction investment1 = TransactionFactory.createInvestmentTransaction(
            BigDecimal.valueOf(500.00), "Investimento mensile ETF World", etf, "EUR");
        service.addTransaction(investment1);
        
        System.out.println("✅ Aggiunte " + service.getTransactionCount() + " transazioni:");
        System.out.println("   💚 " + income1.getDescription() + ": +" + income1.getAmount() + " EUR");
        System.out.println("   💸 " + expense1.getDescription() + ": -" + expense1.getAmount() + " EUR");
        System.out.println("   💸 " + expense2.getDescription() + ": -" + expense2.getAmount() + " EUR");
        System.out.println("   📈 " + investment1.getDescription() + ": -" + investment1.getAmount() + " EUR");
        
        BigDecimal balance = service.getTotalBalance();
        System.out.println("💰 Bilancio totale: " + balance + " EUR");
    }
    
    private static void demoBudgets(FinanceService service, List<Category> categories) {
        System.out.println("\n📊 DEMO: Gestione Budget");
        System.out.println("-".repeat(40));
        
        Category ristoranti = findCategoryByName(categories, "Ristoranti");
        Category trasporti = findCategoryByName(categories, "Trasporti");
        
        // Crea budget per il mese corrente
        YearMonth currentMonth = YearMonth.now();
        
        Budget budgetRistoranti = new Budget(ristoranti, BigDecimal.valueOf(200.00), currentMonth, "EUR");
        service.addBudget(budgetRistoranti);
        
        Budget budgetTrasporti = new Budget(trasporti, BigDecimal.valueOf(300.00), currentMonth, "EUR");
        service.addBudget(budgetTrasporti);
        
        System.out.println("✅ Creati " + service.getBudgetCount() + " budget per " + currentMonth + ":");
        System.out.println("   🍽️  Ristoranti: " + budgetRistoranti.getAmount() + " EUR");
        System.out.println("   🚗 Trasporti: " + budgetTrasporti.getAmount() + " EUR");
        
        // Mostra stato budget
        List<Budget> budgets = service.getAllBudgets();
        for (Budget budget : budgets) {
            String status = budget.isExceeded() ? "🚨 SUPERATO" : 
                           budget.isNearLimit() ? "⚠️ VICINO AL LIMITE" : "✅ OK";
            System.out.println("   📊 " + budget.getCategory().getName() + 
                             " - Utilizzato: " + budget.getUsagePercentage() + "% - " + status);
        }
    }
    
    private static void demoReports(FinanceService service) {
        System.out.println("\n📈 DEMO: Report e Statistiche");
        System.out.println("-".repeat(40));
        
        YearMonth currentMonth = YearMonth.now();
        
        // Report bilancio mensile
        var balance = service.getMonthlyBalance(currentMonth);
        System.out.println("📅 Bilancio " + currentMonth.format(DateTimeFormatter.ofPattern("MM/yyyy")) + ":");
        System.out.println("   💚 Entrate: " + balance.get("income") + " EUR");
        System.out.println("   💸 Spese: " + balance.get("expenses") + " EUR");
        System.out.println("   📈 Investimenti: " + balance.get("investments") + " EUR");
        System.out.println("   💰 Saldo: " + balance.get("balance") + " EUR");
        
        // Spese per categoria
        var expensesByCategory = service.getExpensesByCategory();
        if (!expensesByCategory.isEmpty()) {
            System.out.println("\n📊 Spese per categoria:");
            expensesByCategory.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> 
                    System.out.println("   📁 " + entry.getKey() + ": " + entry.getValue() + " EUR"));
        }
        
        // Trend mensile (ultimi 3 mesi)
        var trend = service.getMonthlyTrend(3);
        if (!trend.isEmpty()) {
            System.out.println("\n📈 Trend ultimi 3 mesi:");
            trend.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEach(entry -> 
                    System.out.println("   📅 " + entry.getKey() + ": " + entry.getValue() + " EUR"));
        }
    }
    
    private static void demoStrategies(FinanceService service, List<Category> categories) {
        System.out.println("\n🔧 DEMO: Strategie di Budgeting");
        System.out.println("-".repeat(40));
        
        Category alimentari = findCategoryByName(categories, "Alimentari");
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        
        // Aggiungi un'entrata per il prossimo mese per far funzionare le strategie
        Category stipendio = findCategoryByName(categories, "Stipendio");
        service.addTransaction(TransactionFactory.createIncomeTransaction(
            BigDecimal.valueOf(3000.00), "Stipendio previsto", stipendio, "EUR"));
        
        // Aggiungi alcune transazioni storiche per alimentari per testare le strategie
        Category spesaQuotidiana = findCategoryByName(categories, "Spesa Quotidiana");
        if (spesaQuotidiana != null) {
            service.addTransaction(TransactionFactory.createExpenseTransaction(
                BigDecimal.valueOf(150.00), "Spesa settimanale", spesaQuotidiana, "EUR"));
            service.addTransaction(TransactionFactory.createExpenseTransaction(
                BigDecimal.valueOf(120.00), "Spesa settimanale", spesaQuotidiana, "EUR"));
        }
        
        // Test strategia conservativa
        BudgetingStrategy conservative = new ConservativeBudgetingStrategy();
        service.setBudgetingStrategy(conservative);
        Budget conservativeBudget = service.createSuggestedBudget(alimentari, nextMonth);
        
        // Cambia strategia ad aggressiva
        BudgetingStrategy aggressive = new AggressiveBudgetingStrategy();
        service.setBudgetingStrategy(aggressive);
        Budget aggressiveBudget = service.createSuggestedBudget(alimentari, nextMonth);
        
        System.out.println("💡 Budget suggeriti per " + alimentari.getName() + " (" + nextMonth + "):");
        System.out.println("   📉 Strategia Conservativa: " + conservativeBudget.getAmount() + " EUR");
        System.out.println("   📈 Strategia Aggressiva: " + aggressiveBudget.getAmount() + " EUR");
        
        // Ripristina strategia conservativa
        service.setBudgetingStrategy(conservative);
        System.out.println("🔧 Strategia attiva: " + service.getBudgetingStrategy().getStrategyName());
    }
    
    private static Category findCategoryByName(List<Category> categories, String name) {
        for (Category category : categories) {
            Category found = findCategoryInTree(category, name);
            if (found != null) {
                return found;
            }
        }
        return categories.get(0); // Fallback
    }
    
    private static Category findCategoryInTree(Category category, String name) {
        if (category.getName().equalsIgnoreCase(name)) {
            return category;
        }
        for (Category sub : category.getSubcategories()) {
            Category found = findCategoryInTree(sub, name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
