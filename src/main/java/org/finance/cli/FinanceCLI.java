package org.finance.cli;

import org.finance.config.DefaultCategories;
import org.finance.factory.TransactionFactory;
import org.finance.model.*;
import org.finance.observer.*;
import org.finance.strategy.*;
import org.finance.service.FinanceService;
import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Interfaccia a linea di comando per il Personal Finance Manager.
 * Implementa il pattern Command per gestire i comandi dell'utente.
 */
public class FinanceCLI {
    private static final Scanner scanner = new Scanner(System.in);
    private final FinanceService financeService;
    private final List<Category> categories;
    private boolean running = true;

    public FinanceCLI() {
        this.financeService = new FinanceService();
        this.categories = DefaultCategories.createDefaultCategories();
        
        // Inizializza le categorie nel servizio
        for (Category category : categories) {
            financeService.addCategory(category);
        }
        
        // Configura observer per notifiche
        ConsoleBudgetObserver observer = new ConsoleBudgetObserver();
        financeService.addBudgetObserver(observer);
    }

    public void start() {
        printWelcomeMessage();
        
        while (running) {
            printMainMenu();
            String choice = readUserInput("Seleziona un'opzione: ");
            processMainMenuChoice(choice);
        }
        
        System.out.println("\n Grazie per aver usato Personal Finance Manager!");
    }

    private void printWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PERSONAL FINANCE MANAGER");
        System.out.println("=".repeat(60));
        System.out.println("Benvenuto nel tuo gestore di finanze personali!");
        System.out.println("Gestisci transazioni, budget e categorie facilmente.");
        System.out.println("=".repeat(60));
    }

    private void printMainMenu() {
        System.out.println("\n MENU PRINCIPALE");
        System.out.println("-".repeat(30));
        System.out.println("1. Gestione Transazioni");
        System.out.println("2. Gestione Budget");
        System.out.println("3. Gestione Categorie");
        System.out.println("4. Report e Statistiche");
        System.out.println("5. Impostazioni");
        System.out.println("0. Esci");
        System.out.println("-".repeat(30));
    }

    private void processMainMenuChoice(String choice) {
        try {
            switch (choice.trim()) {
                case "1" -> showTransactionMenu();
                case "2" -> showBudgetMenu();
                case "3" -> showCategoryMenu();
                case "4" -> showReportsMenu();
                case "5" -> showSettingsMenu();
                case "0" -> {
                    running = false;
                    return;
                }
                default -> System.out.println("Opzione non valida. Riprova.");
            }
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            System.out.println("Riprova con un'opzione valida.");
        }
    }

    private void showTransactionMenu() {
        boolean backToMain = false;
        
        while (!backToMain) {
            System.out.println("\n GESTIONE TRANSAZIONI");
            System.out.println("-".repeat(30));
            System.out.println("1. Aggiungi Entrata");
            System.out.println("2. Aggiungi Spesa");
            System.out.println("3. Aggiungi Investimento");
            System.out.println("4. Visualizza Transazioni");
            System.out.println("5. Cerca Transazioni");
            System.out.println("0. Torna al Menu Principale");
            System.out.println("-".repeat(30));

            String choice = readUserInput("Seleziona un'opzione: ");
            switch (choice.trim()) {
                case "1" -> addIncomeTransaction();
                case "2" -> addExpenseTransaction();
                case "3" -> addInvestmentTransaction();
                case "4" -> listTransactions();
                case "5" -> searchTransactions();
                case "0" -> backToMain = true;
                default -> {
                    System.out.println("Opzione non valida. Riprova!");
                    System.out.println("Inserisci un numero tra 0 e 5");
                }
            }
        }
    }

    private void addIncomeTransaction() {
        System.out.println("\n NUOVA ENTRATA");
        System.out.println("-".repeat(20));

        try {
            BigDecimal amount = readAmount("Inserisci l'importo: ");
            String description = readUserInput("Descrizione: ");
            Category category = selectCategory("entrata");
            String currency = readCurrency();

            Transaction transaction = TransactionFactory.createIncomeTransaction(
                amount, sanitizeInput(description), category, currency);
            
            financeService.addTransaction(transaction);
            
            System.out.println("Entrata aggiunta con successo!");
            System.out.println("Importo: " + amount + " " + currency);
            System.out.println("Descrizione: " + description);
            System.out.println("Categoria: " + category.getFullPath());
            
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta dell'entrata: " + e.getMessage());
        }
    }

    private void addExpenseTransaction() {
        System.out.println("\n NUOVA SPESA");
        System.out.println("-".repeat(20));

        try {
            BigDecimal amount = readAmount("Inserisci l'importo: ");
            String description = readUserInput("Descrizione: ");
            Category category = selectCategory("spesa");
            String currency = readCurrency();

            Transaction transaction = TransactionFactory.createExpenseTransaction(
                amount, sanitizeInput(description), category, currency);
            
            financeService.addTransaction(transaction);
            
            System.out.println("Spesa aggiunta con successo!");
            System.out.println("Importo: " + amount + " " + currency);
            System.out.println("Descrizione: " + description);
            System.out.println("Categoria: " + category.getFullPath());
            
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta della spesa: " + e.getMessage());
        }
    }

    private void addInvestmentTransaction() {
        System.out.println("\n NUOVO INVESTIMENTO");
        System.out.println("-".repeat(25));

        try {
            BigDecimal amount = readAmount("Inserisci l'importo: ");
            String description = readUserInput("Descrizione: ");
            Category category = selectCategory("investimento");
            String currency = readCurrency();

            Transaction transaction = TransactionFactory.createInvestmentTransaction(
                amount, sanitizeInput(description), category, currency);
            
            financeService.addTransaction(transaction);
            
            System.out.println("Investimento aggiunto con successo!");
            System.out.println("Importo: " + amount + " " + currency);
            System.out.println("Descrizione: " + description);
            System.out.println("Categoria: " + category.getFullPath());
            
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta dell'investimento: " + e.getMessage());
        }
    }

    private void listTransactions() {
        System.out.println("\n LISTA TRANSAZIONI");
        System.out.println("-".repeat(30));

        List<Transaction> transactions = financeService.getAllTransactions();
        
        if (transactions.isEmpty()) {
            System.out.println("Nessuna transazione trovata.");
            return;
        }

        // Ordina per data (più recenti prima)
        transactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (int i = 0; i < Math.min(transactions.size(), 20); i++) {
            Transaction t = transactions.get(i);
            String icon = getTransactionIcon(t.getType());
            String impact = t.getBalanceImpact().compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            
            System.out.printf("%s %s | %s%s %s | %s | %s%n",
                icon,
                t.getTimestamp().format(formatter),
                impact,
                t.getBalanceImpact(),
                t.getCurrency(),
                t.getCategory().getName(),
                t.getDescription()
            );
        }

        if (transactions.size() > 20) {
            System.out.println("... e altre " + (transactions.size() - 20) + " transazioni");
        }
        
        System.out.println("\nTotale transazioni: " + transactions.size());
    }

    private void searchTransactions() {
        System.out.println("\n CERCA TRANSAZIONI");
        System.out.println("-".repeat(25));
        
        String searchTerm = readUserInput("Inserisci termine di ricerca (descrizione): ");
        List<Transaction> results = financeService.searchTransactions(searchTerm);
        
        if (results.isEmpty()) {
            System.out.println(" Nessuna transazione trovata per: " + searchTerm);
            return;
        }
        
        System.out.println(" Trovate " + results.size() + " transazioni:");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Transaction t : results) {
            String icon = getTransactionIcon(t.getType());
            System.out.printf("%s %s | %s %s | %s | %s%n",
                icon,
                t.getTimestamp().format(formatter),
                t.getBalanceImpact(),
                t.getCurrency(),
                t.getCategory().getName(),
                t.getDescription()
            );
        }
    }

    private void showBudgetMenu() {
        boolean backToMain = false;
        
        while (!backToMain) {
            System.out.println("\n GESTIONE BUDGET");
            System.out.println("-".repeat(25));
            System.out.println("1. Crea Budget");
            System.out.println("2. Visualizza Budget");
            System.out.println("3. Statistiche Budget");
            System.out.println("0. Torna al Menu Principale");
            System.out.println("-".repeat(25));

            String choice = readUserInput("Seleziona un'opzione: ");
            switch (choice.trim()) {
                case "1" -> createBudget();
                case "2" -> listBudgets();
                case "3" -> showBudgetStats();
                case "0" -> backToMain = true;
                default -> {
                    System.out.println("Opzione non valida. Riprova!");
                    System.out.println("Inserisci un numero tra 0 e 3");
                }
            }
        }
    }

    private void createBudget() {
        System.out.println("\n NUOVO BUDGET");
        System.out.println("-".repeat(20));

        try {
            Category category = selectCategory("budget");
            BigDecimal amount = readAmount("Importo budget: ");
            YearMonth period = readPeriod();
            String currency = readCurrency();

            Budget budget = new Budget(category, amount, period, currency);
            financeService.addBudget(budget);

            System.out.println("Budget creato con successo!");
            System.out.println("Categoria: " + category.getFullPath());
            System.out.println("Importo: " + amount + " " + currency);
            System.out.println("Periodo: " + period);

        } catch (Exception e) {
            System.out.println("Errore durante la creazione del budget: " + e.getMessage());
        }
    }

    private void listBudgets() {
        System.out.println("\n LISTA BUDGET");
        System.out.println("-".repeat(20));

        List<Budget> budgets = financeService.getAllBudgets();
        
        if (budgets.isEmpty()) {
            System.out.println("Nessun budget trovato.");
            return;
        }

        for (Budget budget : budgets) {
            String status = budget.isExceeded() ? " SUPERATO" :
                           budget.isNearLimit() ? "️ VICINO AL LIMITE" : "OK";
            
            System.out.println("\n " + budget.getCategory().getFullPath());
            System.out.println(" Budget: " + budget.getAmount() + " " + budget.getCurrency());
            System.out.println(" Speso: " + budget.getSpent() + " " + budget.getCurrency());
            System.out.println(" Rimanente: " + budget.getRemainingAmount() + " " + budget.getCurrency());
            System.out.println(" Utilizzo: " + budget.getUsagePercentage() + "%");
            System.out.println(" Periodo: " + budget.getPeriod());
            System.out.println(" Status: " + status);
        }
    }

    private void showBudgetStats() {
        System.out.println("\n📈 STATISTICHE BUDGET");
        System.out.println("-".repeat(30));
        
        List<Budget> budgets = financeService.getAllBudgets();
        
        if (budgets.isEmpty()) {
            System.out.println("📭 Nessun budget per generare statistiche.");
            return;
        }

        long exceeded = budgets.stream().mapToLong(b -> b.isExceeded() ? 1 : 0).sum();
        long nearLimit = budgets.stream().mapToLong(b -> b.isNearLimit() && !b.isExceeded() ? 1 : 0).sum();
        long ok = budgets.size() - exceeded - nearLimit;

        System.out.println("Riepilogo Budget:");
        System.out.println("   ✅ OK: " + ok);
        System.out.println("   ⚠️ Vicini al limite: " + nearLimit);
        System.out.println("   🚨 Superati: " + exceeded);
        System.out.println("   📝 Totale: " + budgets.size());
    }

    private void showCategoryMenu() {
        boolean backToMain = false;
        
        while (!backToMain) {
            System.out.println("\n📁 GESTIONE CATEGORIE");
            System.out.println("-".repeat(25));
            System.out.println("1. 📋 Visualizza Categorie");
            System.out.println("2. ➕ Aggiungi Categoria");
            System.out.println("0. ⬅️  Torna al Menu Principale");
            System.out.println("-".repeat(25));

            String choice = readUserInput("Seleziona un'opzione: ");
            switch (choice.trim()) {
                case "1" -> listCategories();
                case "2" -> addCategory();
                case "0" -> backToMain = true;
                default -> {
                    System.out.println("Opzione non valida. Riprova!");
                    System.out.println("Inserisci un numero tra 0 e 2");
                }
            }
        }
    }

    private void listCategories() {
        System.out.println("\n CATEGORIE DISPONIBILI");
        System.out.println("-".repeat(30));

        for (Category category : categories) {
            printCategoryTree(category, 0);
        }
    }

    private void printCategoryTree(Category category, int level) {
        String indent = "  ".repeat(level);
        String icon = level == 0 ? "+" : "-";
        
        System.out.println(indent + icon + " " + category.getName());
        
        for (Category sub : category.getSubcategories()) {
            printCategoryTree(sub, level + 1);
        }
    }

    private void addCategory() {
        System.out.println("\n NUOVA CATEGORIA");
        System.out.println("-".repeat(20));

        try {
            String name = readUserInput("Nome categoria: ");
            String description = readUserInput("Descrizione (opzionale): ");
            
            Category newCategory = new Category(sanitizeInput(name), sanitizeInput(description));
            
            System.out.println("Vuoi aggiungere questa categoria come sottocategoria? (s/n): ");
            String addAsSub = readUserInput("").toLowerCase();
            
            if (addAsSub.startsWith("s")) {
                Category parent = selectCategory("padre");
                parent.addSubcategory(newCategory);
                System.out.println("Sottocategoria aggiunta a: " + parent.getFullPath());
            } else {
                categories.add(newCategory);
                financeService.addCategory(newCategory);
                System.out.println("Categoria principale aggiunta!");
            }
            
        } catch (Exception e) {
            System.out.println("Errore durante l'aggiunta della categoria: " + e.getMessage());
        }
    }

    private void showReportsMenu() {
        boolean backToMain = false;
        
        while (!backToMain) {
            System.out.println("\n📈 REPORT E STATISTICHE");
            System.out.println("-".repeat(30));
            System.out.println("1. 💰 Bilancio Mensile");
            System.out.println("2. Spese per Categoria");
            System.out.println("3. 📈 Trend Mensile");
            System.out.println("0. ⬅️  Torna al Menu Principale");
            System.out.println("-".repeat(30));

            String choice = readUserInput("Seleziona un'opzione: ");
            switch (choice.trim()) {
                case "1" -> showMonthlyBalance();
                case "2" -> showExpensesByCategory();
                case "3" -> showMonthlyTrend();
                case "0" -> backToMain = true;
                default -> {
                    System.out.println("❌ Opzione non valida. Riprova!");
                    System.out.println("💡 Inserisci un numero tra 0 e 3");
                }
            }
        }
    }

    private void showMonthlyBalance() {
        YearMonth month = readPeriod();
        System.out.println("\n💰 BILANCIO " + month);
        System.out.println("-".repeat(30));
        
        Map<String, BigDecimal> balance = financeService.getMonthlyBalance(month);
        
        System.out.println("💚 Entrate: " + balance.getOrDefault("income", BigDecimal.ZERO) + " EUR");
        System.out.println("💸 Spese: " + balance.getOrDefault("expenses", BigDecimal.ZERO) + " EUR");
        System.out.println("📈 Investimenti: " + balance.getOrDefault("investments", BigDecimal.ZERO) + " EUR");
        System.out.println("-".repeat(30));
        System.out.println("💰 Bilancio: " + balance.getOrDefault("balance", BigDecimal.ZERO) + " EUR");
    }

    private void showExpensesByCategory() {
        System.out.println("\nSPESE PER CATEGORIA");
        System.out.println("-".repeat(30));
        
        Map<String, BigDecimal> expenses = financeService.getExpensesByCategory();
        
        if (expenses.isEmpty()) {
            System.out.println("📭 Nessuna spesa registrata.");
            return;
        }
        
        expenses.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.println("📁 " + entry.getKey() + ": " + entry.getValue() + " EUR"));
    }

    private void showMonthlyTrend() {
        System.out.println("\n📈 TREND ULTIMI 6 MESI");
        System.out.println("-".repeat(30));
        
        Map<YearMonth, BigDecimal> trend = financeService.getMonthlyTrend(6);
        
        if (trend.isEmpty()) {
            System.out.println("📭 Dati insufficienti per il trend.");
            return;
        }
        
        trend.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                System.out.println("📅 " + entry.getKey() + ": " + entry.getValue() + " EUR"));
    }

    private void showSettingsMenu() {
        boolean backToMain = false;
        
        while (!backToMain) {
            System.out.println("\n⚙️ IMPOSTAZIONI");
            System.out.println("-".repeat(20));
            System.out.println("1. 💱 Imposta Valuta Predefinita");
            System.out.println("2. Strategia Budget");
            System.out.println("0. ⬅️  Torna al Menu Principale");
            System.out.println("-".repeat(20));

            String choice = readUserInput("Seleziona un'opzione: ");
            switch (choice.trim()) {
                case "1" -> setDefaultCurrency();
                case "2" -> setBudgetStrategy();
                case "0" -> backToMain = true;
                default -> {
                    System.out.println("❌ Opzione non valida. Riprova!");
                    System.out.println("💡 Inserisci un numero tra 0 e 2");
                }
            }
        }
    }

    private void setDefaultCurrency() {
        System.out.println("\n💱 IMPOSTA VALUTA PREDEFINITA");
        System.out.println("-".repeat(30));
        System.out.println("Valuta attuale: " + financeService.getDefaultCurrency());
        
        String newCurrency = readCurrency();
        financeService.setDefaultCurrency(newCurrency);
        
        System.out.println("✅ Valuta predefinita impostata: " + newCurrency);
    }

    private void setBudgetStrategy() {
        System.out.println("\nSTRATEGIA BUDGET");
        System.out.println("-".repeat(25));
        System.out.println("1. 📉 Conservativa");
        System.out.println("2. 📈 Aggressiva");
        System.out.println("-".repeat(25));

        String choice = readUserInput("Seleziona strategia: ");
        BudgetingStrategy strategy = switch (choice.trim()) {
            case "1" -> new ConservativeBudgetingStrategy();
            case "2" -> new AggressiveBudgetingStrategy();
            default -> {
                System.out.println("❌ Opzione non valida.");
                yield null;
            }
        };

        if (strategy != null) {
            financeService.setBudgetingStrategy(strategy);
            System.out.println("✅ Strategia impostata: " + strategy.getStrategyName());
            System.out.println("📝 " + strategy.getDescription());
        }
    }

    // Metodi di utilità per l'input
    private String readUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private BigDecimal readAmount(String prompt) {
        while (true) {
            try {
                String input = readUserInput(prompt);
                BigDecimal amount = new BigDecimal(input.replace(",", "."));
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("❌ L'importo deve essere positivo.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                System.out.println("❌ Inserisci un numero valido.");
            }
        }
    }

    private String readCurrency() {
        String currency = readUserInput("Valuta (default: " + financeService.getDefaultCurrency() + "): ");
        return currency.trim().isEmpty() ? financeService.getDefaultCurrency() : currency.toUpperCase();
    }

    private YearMonth readPeriod() {
        while (true) {
            try {
                String input = readUserInput("Periodo (MM/yyyy, default: corrente): ");
                if (input.trim().isEmpty()) {
                    return YearMonth.now();
                }
                return YearMonth.parse(input, DateTimeFormatter.ofPattern("MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("❌ Formato non valido. Usa MM/yyyy (es: 03/2025).");
            }
        }
    }

    private Category selectCategory(String type) {
        System.out.println("\n📁 Seleziona categoria per " + type + ":");
        
        List<Category> allCategories = getAllCategoriesFlat();
        
        for (int i = 0; i < allCategories.size(); i++) {
            Category cat = allCategories.get(i);
            System.out.println((i + 1) + ". " + cat.getFullPath());
        }
        
        while (true) {
            try {
                String input = readUserInput("Seleziona numero categoria: ");
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < allCategories.size()) {
                    return allCategories.get(index);
                } else {
                    System.out.println("❌ Numero non valido.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Inserisci un numero valido.");
            }
        }
    }

    private List<Category> getAllCategoriesFlat() {
        List<Category> result = new ArrayList<>();
        for (Category category : categories) {
            addCategoryAndSubs(category, result);
        }
        return result;
    }

    private void addCategoryAndSubs(Category category, List<Category> result) {
        result.add(category);
        for (Category sub : category.getSubcategories()) {
            addCategoryAndSubs(sub, result);
        }
    }

    private String sanitizeInput(String input) {
        if (input == null) return "";
        return StringEscapeUtils.escapeHtml4(input.trim());
    }

    private String getTransactionIcon(TransactionType type) {
        return switch (type) {
            case INCOME -> "💚";
            case EXPENSE -> "💸";
            case INVESTMENT -> "📈";
        };
    }
}
