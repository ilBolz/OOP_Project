package org.finance.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.finance.config.FinanceModule;
import org.finance.database.DatabaseManager;
import org.finance.model.*;
import org.finance.observer.ConsoleBudgetObserver;
import org.finance.service.PersistentFinanceService;
import org.finance.strategy.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaccia CLI principale per il Personal Finance Manager con persistenza database.
 * Implementa un'interfaccia utente completa e intuitiva per gestire le finanze personali.
 */
public class PersistentFinanceCLI {
    private final Scanner scanner;
    private final PersistentFinanceService financeService;
    private final DateTimeFormatter dateFormatter;
    
    public PersistentFinanceCLI() {
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Inizializza Guice per l'injection delle dipendenze
        Injector injector = Guice.createInjector(new FinanceModule());
        this.financeService = injector.getInstance(PersistentFinanceService.class);
        
        // Aggiungi osservatore per le notifiche sui budget
        this.financeService.addBudgetObserver(new ConsoleBudgetObserver());
        
        System.out.println("=".repeat(60));
        System.out.println("   PERSONAL FINANCE MANAGER - VERSIONE PERSISTENTE");
        System.out.println("=".repeat(60));
        System.out.println("Database H2 inizializzato con successo!");
        System.out.println();
    }

    public void start() {
        boolean running = true;
        
        while (running) {
            try {
                showMainMenu();
                int choice = getIntInput("Seleziona un'opzione: ");
                
                switch (choice) {
                    case 1 -> manageTransactions();
                    case 2 -> manageCategories();
                    case 3 -> manageBudgets();
                    case 4 -> viewReports();
                    case 5 -> configureSettings();
                    case 0 -> {
                        System.out.println("Chiusura dell'applicazione...");
                        cleanup();
                        running = false;
                    }
                    default -> System.out.println("Opzione non valida. Riprova.");
                }
            } catch (Exception e) {
                System.err.println("Si è verificato un errore: " + e.getMessage());
                System.out.println("Premi INVIO per continuare...");
                scanner.nextLine();
            }
        }
    }
    
    private void cleanup() {
        DatabaseManager.getInstance().closeConnection();
        scanner.close();
        System.out.println("Arrivederci!");
    }

    private void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("            MENU PRINCIPALE");
        System.out.println("=".repeat(50));
        System.out.println("1. Gestione Transazioni");
        System.out.println("2. Gestione Categorie");
        System.out.println("3. Gestione Budget");
        System.out.println("4. Report e Statistiche");
        System.out.println("5. Impostazioni");
        System.out.println("0. Esci");
        System.out.println("=".repeat(50));
    }

    // ===== GESTIONE TRANSAZIONI =====
    
    private void manageTransactions() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- GESTIONE TRANSAZIONI ---");
            System.out.println("1. Aggiungi Entrata");
            System.out.println("2. Aggiungi Spesa");
            System.out.println("3. Visualizza Tutte le Transazioni");
            System.out.println("4. Cerca Transazioni");
            System.out.println("5. Elimina Transazione");
            System.out.println("0. Torna al Menu Principale");
            
            int choice = getIntInput("Seleziona: ");
            
            switch (choice) {
                case 1 -> addIncomeTransaction();
                case 2 -> addExpenseTransaction();
                case 3 -> viewAllTransactions();
                case 4 -> searchTransactions();
                case 5 -> deleteTransaction();
                case 0 -> back = true;
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    private void addIncomeTransaction() {
        System.out.println("\n--- NUOVA ENTRATA ---");
        
        try {
            BigDecimal amount = getBigDecimalInput("Importo: €");
            String description = getStringInput("Descrizione: ");
            Category category = selectCategory("Seleziona categoria per l'entrata: ");
            
            if (category == null) return;
            
            Transaction transaction = new IncomeTransaction(amount, description, category, financeService.getDefaultCurrency());
            financeService.addTransaction(transaction);
            
            System.out.println("✓ Entrata aggiunta con successo!");
            System.out.println("  ID: " + transaction.getId());
            System.out.println("  Importo: €" + amount);
            System.out.println("  Categoria: " + category.getName());
            
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta dell'entrata: " + e.getMessage());
        }
    }

    private void addExpenseTransaction() {
        System.out.println("\n--- NUOVA SPESA ---");
        
        try {
            BigDecimal amount = getBigDecimalInput("Importo: €");
            String description = getStringInput("Descrizione: ");
            Category category = selectCategory("Seleziona categoria per la spesa: ");
            
            if (category == null) return;
            
            Transaction transaction = new ExpenseTransaction(amount, description, category, financeService.getDefaultCurrency());
            financeService.addTransaction(transaction);
            
            System.out.println("✓ Spesa aggiunta con successo!");
            System.out.println("  ID: " + transaction.getId());
            System.out.println("  Importo: €" + amount);
            System.out.println("  Categoria: " + category.getName());
            
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della spesa: " + e.getMessage());
        }
    }

    private void viewAllTransactions() {
        System.out.println("\n--- TUTTE LE TRANSAZIONI ---");
        
        List<Transaction> transactions = financeService.getAllTransactions();
        
        if (transactions.isEmpty()) {
            System.out.println("Nessuna transazione trovata.");
            return;
        }
        
        System.out.printf("%-8s %-10s %-15s %-20s %-15s %-15s%n", 
                "TIPO", "IMPORTO", "CATEGORIA", "DESCRIZIONE", "DATA", "ID");
        System.out.println("-".repeat(100));
        
        for (Transaction t : transactions) {
            String type = t instanceof IncomeTransaction ? "ENTRATA" : "SPESA";
            System.out.printf("%-8s €%-9.2f %-15s %-20s %-15s %-15s%n",
                    type,
                    t.getAmount(),
                    t.getCategory().getName(),
                    truncateString(t.getDescription(), 18),
                    t.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")),
                    t.getId().substring(0, 8) + "...");
        }
        
        System.out.println("\nTotale transazioni: " + transactions.size());
    }

    private void searchTransactions() {
        System.out.println("\n--- CERCA TRANSAZIONI ---");
        System.out.println("1. Per Categoria");
        System.out.println("2. Per Intervallo Date");
        System.out.println("3. Solo Entrate");
        System.out.println("4. Solo Spese");
        
        int choice = getIntInput("Tipo di ricerca: ");
        List<Transaction> results = List.of();
        
        switch (choice) {
            case 1 -> {
                Category category = selectCategory("Seleziona categoria: ");
                if (category != null) {
                    results = financeService.getTransactionsByCategory(category.getName());
                }
            }
            case 2 -> {
                LocalDate startDate = getDateInput("Data inizio (dd/MM/yyyy): ");
                LocalDate endDate = getDateInput("Data fine (dd/MM/yyyy): ");
                if (startDate != null && endDate != null) {
                    results = financeService.getTransactionsByDateRange(startDate, endDate);
                }
            }
            case 3 -> results = financeService.getAllTransactions().stream()
                    .filter(t -> t instanceof IncomeTransaction).toList();
            case 4 -> results = financeService.getAllTransactions().stream()
                    .filter(t -> t instanceof ExpenseTransaction).toList();
            default -> {
                System.out.println("Opzione non valida.");
                return;
            }
        }
        
        System.out.println("\nRisultati trovati: " + results.size());
        displayTransactions(results);
    }

    private void deleteTransaction() {
        System.out.println("\n--- ELIMINA TRANSAZIONE ---");
        
        viewAllTransactions();
        
        if (financeService.getAllTransactions().isEmpty()) {
            return;
        }
        
        String transactionId = getStringInput("Inserisci l'ID completo della transazione da eliminare: ");
        
        try {
            financeService.removeTransaction(transactionId);
            System.out.println("✓ Transazione eliminata con successo!");
        } catch (Exception e) {
            System.err.println("Errore nell'eliminazione: " + e.getMessage());
        }
    }

    // ===== GESTIONE CATEGORIE =====
    
    private void manageCategories() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- GESTIONE CATEGORIE ---");
            System.out.println("1. Visualizza Categorie");
            System.out.println("2. Aggiungi Categoria");
            System.out.println("3. Elimina Categoria");
            System.out.println("0. Torna al Menu Principale");
            
            int choice = getIntInput("Seleziona: ");
            
            switch (choice) {
                case 1 -> viewCategories();
                case 2 -> addCategory();
                case 3 -> deleteCategory();
                case 0 -> back = true;
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    private void viewCategories() {
        System.out.println("\n--- CATEGORIE DISPONIBILI ---");
        
        List<Category> categories = financeService.getAllCategories();
        
        if (categories.isEmpty()) {
            System.out.println("Nessuna categoria trovata.");
            return;
        }
        
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            System.out.printf("%d. %s - %s%n", i + 1, cat.getName(), cat.getDescription());
        }
    }

    private void addCategory() {
        System.out.println("\n--- NUOVA CATEGORIA ---");
        
        try {
            String name = getStringInput("Nome categoria: ");
            String description = getStringInput("Descrizione: ");
            
            Category category = new Category(name, description);
            financeService.addCategory(category);
            
            System.out.println("✓ Categoria '" + name + "' aggiunta con successo!");
            
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta della categoria: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        System.out.println("\n--- ELIMINA CATEGORIA ---");
        
        viewCategories();
        
        if (financeService.getAllCategories().isEmpty()) {
            return;
        }
        
        String categoryName = getStringInput("Nome della categoria da eliminare: ");
        
        try {
            financeService.removeCategory(categoryName);
            System.out.println("✓ Categoria eliminata con successo!");
        } catch (Exception e) {
            System.err.println("Errore nell'eliminazione: " + e.getMessage());
        }
    }

    // ===== GESTIONE BUDGET =====
    
    private void manageBudgets() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- GESTIONE BUDGET ---");
            System.out.println("1. Visualizza Budget");
            System.out.println("2. Aggiungi Budget");
            System.out.println("3. Elimina Budget");
            System.out.println("4. Suggerisci Budget");
            System.out.println("0. Torna al Menu Principale");
            
            int choice = getIntInput("Seleziona: ");
            
            switch (choice) {
                case 1 -> viewBudgets();
                case 2 -> addBudget();
                case 3 -> deleteBudget();
                case 4 -> suggestBudget();
                case 0 -> back = true;
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    private void viewBudgets() {
        System.out.println("\n--- BUDGET ATTIVI ---");
        
        List<Budget> budgets = financeService.getActiveBudgets();
        
        if (budgets.isEmpty()) {
            System.out.println("Nessun budget attivo trovato.");
            return;
        }
        
        System.out.printf("%-15s %-10s %-10s %-10s %-15s%n", 
                "CATEGORIA", "LIMITE", "SPESO", "RIMASTO", "PERIODO");
        System.out.println("-".repeat(70));
        
        for (Budget budget : budgets) {
            System.out.printf("%-15s €%-9.2f €%-9.2f €%-9.2f %-15s%n",
                    budget.getCategory().getName(),
                    budget.getAmount(),
                    budget.getSpent(),
                    budget.getRemainingAmount(),
                    budget.getPeriod().toString());
        }
    }

    private void addBudget() {
        System.out.println("\n--- NUOVO BUDGET ---");
        
        try {
            Category category = selectCategory("Seleziona categoria per il budget: ");
            if (category == null) return;
            
            BigDecimal amount = getBigDecimalInput("Limite budget: €");
            YearMonth period = getPeriodInput("Periodo (MM/yyyy): ");
            
            Budget budget = new Budget(category, amount, period, financeService.getDefaultCurrency());
            financeService.addBudget(budget);
            
            System.out.println("✓ Budget aggiunto con successo!");
            System.out.println("  Categoria: " + category.getName());
            System.out.println("  Limite: €" + amount);
            System.out.println("  Periodo: " + period);
            
        } catch (Exception e) {
            System.err.println("Errore nell'aggiunta del budget: " + e.getMessage());
        }
    }

    private void deleteBudget() {
        System.out.println("\n--- ELIMINA BUDGET ---");
        
        viewBudgets();
        
        if (financeService.getActiveBudgets().isEmpty()) {
            return;
        }
        
        String budgetId = getStringInput("Inserisci l'ID del budget da eliminare: ");
        
        try {
            financeService.removeBudget(budgetId);
            System.out.println("✓ Budget eliminato con successo!");
        } catch (Exception e) {
            System.err.println("Errore nell'eliminazione: " + e.getMessage());
        }
    }

    private void suggestBudget() {
        System.out.println("\n--- SUGGERIMENTO BUDGET ---");
        
        try {
            Category category = selectCategory("Seleziona categoria: ");
            if (category == null) return;
            
            YearMonth period = getPeriodInput("Periodo (MM/yyyy): ");
            
            Budget suggested = financeService.suggestBudgetForCategory(category, period);
            
            System.out.println("✓ Budget suggerito:");
            System.out.println("  Categoria: " + suggested.getCategory().getName());
            System.out.println("  Importo suggerito: €" + suggested.getAmount());
            System.out.println("  Periodo: " + suggested.getPeriod());
            System.out.println("  Strategia: " + financeService.getBudgetingStrategy().getStrategyName());
            
            boolean create = getBooleanInput("Vuoi creare questo budget? (s/n): ");
            if (create) {
                financeService.addBudget(suggested);
                System.out.println("✓ Budget creato con successo!");
            }
            
        } catch (Exception e) {
            System.err.println("Errore nel suggerimento: " + e.getMessage());
        }
    }

    // ===== REPORT E STATISTICHE =====
    
    private void viewReports() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- REPORT E STATISTICHE ---");
            System.out.println("1. Bilancio Generale");
            System.out.println("2. Spese per Categoria");
            System.out.println("3. Analisi Budget");
            System.out.println("0. Torna al Menu Principale");
            
            int choice = getIntInput("Seleziona: ");
            
            switch (choice) {
                case 1 -> showGeneralBalance();
                case 2 -> showExpensesByCategory();
                case 3 -> showBudgetAnalysis();
                case 0 -> back = true;
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    private void showGeneralBalance() {
        System.out.println("\n--- BILANCIO GENERALE ---");
        
        BigDecimal totalIncome = financeService.getTotalIncome();
        BigDecimal totalExpenses = financeService.getTotalExpenses();
        BigDecimal balance = financeService.getBalance();
        
        System.out.println("Entrate totali:    €" + totalIncome);
        System.out.println("Spese totali:      €" + totalExpenses);
        System.out.println("-".repeat(30));
        System.out.println("Bilancio:          €" + balance);
        
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("✓ Bilancio positivo!");
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("⚠ Bilancio negativo!");
        } else {
            System.out.println("= Bilancio in pareggio");
        }
    }

    private void showExpensesByCategory() {
        System.out.println("\n--- SPESE PER CATEGORIA ---");
        
        var expensesByCategory = financeService.getExpensesByCategory();
        
        if (expensesByCategory.isEmpty()) {
            System.out.println("Nessuna spesa registrata.");
            return;
        }
        
        System.out.printf("%-20s %-15s%n", "CATEGORIA", "TOTALE SPESO");
        System.out.println("-".repeat(40));
        
        expensesByCategory.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> 
                    System.out.printf("%-20s €%-14.2f%n", entry.getKey(), entry.getValue())
                );
    }

    private void showBudgetAnalysis() {
        System.out.println("\n--- ANALISI BUDGET ---");
        
        List<Budget> budgets = financeService.getActiveBudgets();
        
        if (budgets.isEmpty()) {
            System.out.println("Nessun budget attivo.");
            return;
        }
        
        System.out.printf("%-15s %-10s %-8s %-10s%n", "CATEGORIA", "LIMITE", "UTILIZZO", "STATO");
        System.out.println("-".repeat(50));
        
        for (Budget budget : budgets) {
            String status;
            if (budget.isExceeded()) {
                status = "SUPERATO";
            } else if (budget.isNearLimit()) {
                status = "VICINO AL LIMITE";
            } else {
                status = "OK";
            }
            
            System.out.printf("%-15s €%-9.2f %-7.1f%% %-10s%n",
                    budget.getCategory().getName(),
                    budget.getAmount(),
                    budget.getUsagePercentage(),
                    status);
        }
    }

    // ===== IMPOSTAZIONI =====
    
    private void configureSettings() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- IMPOSTAZIONI ---");
            System.out.println("1. Cambia Strategia di Budgeting");
            System.out.println("2. Impostazioni Valuta");
            System.out.println("3. Informazioni Database");
            System.out.println("0. Torna al Menu Principale");
            
            int choice = getIntInput("Seleziona: ");
            
            switch (choice) {
                case 1 -> changeBudgetingStrategy();
                case 2 -> changeCurrency();
                case 3 -> showDatabaseInfo();
                case 0 -> back = true;
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    private void changeBudgetingStrategy() {
        System.out.println("\n--- STRATEGIA DI BUDGETING ---");
        System.out.println("Strategia attuale: " + financeService.getBudgetingStrategy().getStrategyName());
        System.out.println();
        System.out.println("1. Conservativa (50% necessità, 30% desideri, 20% risparmi)");
        System.out.println("2. Aggressiva (40% necessità, 40% desideri, 20% risparmi)");
        
        int choice = getIntInput("Seleziona nuova strategia: ");
        
        switch (choice) {
            case 1 -> {
                financeService.setBudgetingStrategy(new ConservativeBudgetingStrategy());
                System.out.println("✓ Strategia conservativa impostata!");
            }
            case 2 -> {
                financeService.setBudgetingStrategy(new AggressiveBudgetingStrategy());
                System.out.println("✓ Strategia aggressiva impostata!");
            }
            default -> System.out.println("Opzione non valida.");
        }
    }

    private void changeCurrency() {
        System.out.println("\n--- IMPOSTAZIONI VALUTA ---");
        System.out.println("Valuta attuale: " + financeService.getDefaultCurrency());
        
        String newCurrency = getStringInput("Nuova valuta (es. EUR, USD, GBP): ");
        
        if (newCurrency != null && !newCurrency.trim().isEmpty()) {
            financeService.setDefaultCurrency(newCurrency.toUpperCase());
            System.out.println("✓ Valuta aggiornata a: " + newCurrency.toUpperCase());
        }
    }

    private void showDatabaseInfo() {
        System.out.println("\n--- INFORMAZIONI DATABASE ---");
        System.out.println("Database: H2 (file locale)");
        System.out.println("Percorso: ./data/finance_db");
        System.out.println();
        System.out.println("Statistiche:");
        System.out.println("  Categorie: " + financeService.getAllCategories().size());
        System.out.println("  Transazioni: " + financeService.getAllTransactions().size());
        System.out.println("  Budget attivi: " + financeService.getActiveBudgets().size());
    }

    // ===== METODI HELPER =====
    
    private Category selectCategory(String prompt) {
        System.out.println("\n" + prompt);
        
        List<Category> categories = financeService.getAllCategories();
        
        if (categories.isEmpty()) {
            System.out.println("Nessuna categoria disponibile. Creane una prima.");
            return null;
        }
        
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, categories.get(i).getName());
        }
        
        int choice = getIntInput("Seleziona categoria (numero): ");
        
        if (choice >= 1 && choice <= categories.size()) {
            return categories.get(choice - 1);
        } else {
            System.out.println("Selezione non valida.");
            return null;
        }
    }

    private void displayTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("Nessuna transazione trovata.");
            return;
        }
        
        System.out.printf("%-8s %-10s %-15s %-20s %-15s%n", 
                "TIPO", "IMPORTO", "CATEGORIA", "DESCRIZIONE", "DATA");
        System.out.println("-".repeat(80));
        
        for (Transaction t : transactions) {
            String type = t instanceof IncomeTransaction ? "ENTRATA" : "SPESA";
            System.out.printf("%-8s €%-9.2f %-15s %-20s %-15s%n",
                    type,
                    t.getAmount(),
                    t.getCategory().getName(),
                    truncateString(t.getDescription(), 18),
                    t.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
            }
        }
    }

    private BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un importo valido (es. 10.50).");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("s") || input.equals("si") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Rispondi con 's' o 'n'.");
            }
        }
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine().trim(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido. Usa dd/MM/yyyy (es. 15/03/2024).");
            }
        }
    }

    private YearMonth getPeriodInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                return YearMonth.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato periodo non valido. Usa MM/yyyy (es. 03/2024).");
            }
        }
    }

    // ===== MAIN METHOD =====
    
    public static void main(String[] args) {
        try {
            PersistentFinanceCLI cli = new PersistentFinanceCLI();
            cli.start();
        } catch (Exception e) {
            System.err.println("Errore critico nell'avvio dell'applicazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
