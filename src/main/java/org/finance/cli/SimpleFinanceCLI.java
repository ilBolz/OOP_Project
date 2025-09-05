package org.finance.cli;

import org.finance.database.DatabaseManager;
import org.finance.model.*;
import org.finance.observer.ConsoleBudgetObserver;
import org.finance.repository.impl.*;
import org.finance.service.PersistentFinanceService;
import org.finance.strategy.*;
import org.finance.factory.TransactionFactory;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Simple CLI for Personal Finance Manager without dependency injection.
 * Demonstrates all design patterns and core functionality.
 */
public class SimpleFinanceCLI {
    private final Scanner scanner;
    private final PersistentFinanceService financeService;
    private final DateTimeFormatter dateFormatter;

    public SimpleFinanceCLI() {
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Manual dependency creation (without Guice)
        DatabaseManager.getInstance(); // Initialize database
        H2CategoryRepository categoryRepo = new H2CategoryRepository();
        H2BudgetRepository budgetRepo = new H2BudgetRepository(categoryRepo);
        H2TransactionRepository transactionRepo = new H2TransactionRepository(categoryRepo);
        
        this.financeService = new PersistentFinanceService(transactionRepo, budgetRepo, categoryRepo);
        this.financeService.addBudgetObserver(new ConsoleBudgetObserver());
        
        // Set default strategy
        this.financeService.setBudgetingStrategy(new ConservativeBudgetingStrategy());
        
        printWelcome();
        initializeDefaultCategories();
    }

    private void printWelcome() {
        System.out.println("=".repeat(60));
        System.out.println("     PERSONAL FINANCE MANAGER - DEMO VERSION");
        System.out.println("=".repeat(60));
        System.out.println("✓ Database H2 initialized successfully!");
        System.out.println("✓ All design patterns loaded and operational");
        System.out.println("✓ Conservative budgeting strategy active (15%)");
        System.out.println();
    }

    private void initializeDefaultCategories() {
        try {
            // Create some default categories if they don't exist
            if (financeService.getAllCategories().isEmpty()) {
                System.out.println("Initializing default categories...");
                
                Category casa = new Category("Casa", "Home-related expenses");
                Category alimentari = new Category("Alimentari", "Food and groceries");
                Category trasporti = new Category("Trasporti", "Transportation costs");
                Category stipendio = new Category("Stipendio", "Salary income");
                Category investimenti = new Category("Investimenti", "Investment transactions");
                
                financeService.addCategory(casa);
                financeService.addCategory(alimentari);
                financeService.addCategory(trasporti);
                financeService.addCategory(stipendio);
                financeService.addCategory(investimenti);
                
                System.out.println("✓ Default categories created");
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not initialize default categories: " + e.getMessage());
        }
    }

    public void start() {
        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                int choice = getIntInput("Choose option: ");
                
                switch (choice) {
                    case 1 -> manageTransactions();
                    case 2 -> manageBudgets();
                    case 3 -> viewReports();
                    case 4 -> manageCategories();
                    case 5 -> testPatterns();
                    case 6 -> settings();
                    case 0 -> {
                        System.out.println("Thank you for using Personal Finance Manager!");
                        running = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         MAIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. 💰 Manage Transactions");
        System.out.println("2. 📊 Manage Budgets");
        System.out.println("3. 📈 View Reports");
        System.out.println("4. 📁 Manage Categories");
        System.out.println("5. 🔧 Test Design Patterns");
        System.out.println("6. ⚙️  Settings");
        System.out.println("0. 🚪 Exit");
        System.out.println("-".repeat(40));
    }

    private void manageTransactions() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- TRANSACTION MANAGEMENT ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. Add Investment");
            System.out.println("4. View All Transactions");
            System.out.println("5. View Transaction History (Iterator Pattern)");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Select: ");
            switch (choice) {
                case 1 -> addIncome();
                case 2 -> addExpense();
                case 3 -> addInvestment();
                case 4 -> viewAllTransactions();
                case 5 -> viewTransactionHistory();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addIncome() {
        System.out.println("\n--- ADD INCOME ---");
        try {
            BigDecimal amount = getBigDecimalInput("Amount: €");
            String description = getStringInput("Description: ");
            Category category = selectCategory("Select category: ");
            if (category == null) return;

            Transaction income = TransactionFactory.createIncomeTransaction(
                amount, description, category, financeService.getDefaultCurrency());
            financeService.addTransaction(income);
            
            System.out.println("✓ Income added successfully!");
            System.out.println("  Amount: €" + amount);
            System.out.println("  Category: " + category.getName());
            System.out.println("  Current balance: €" + financeService.getBalance());
        } catch (Exception e) {
            System.err.println("Error adding income: " + e.getMessage());
        }
    }

    private void addExpense() {
        System.out.println("\n--- ADD EXPENSE ---");
        try {
            BigDecimal amount = getBigDecimalInput("Amount: €");
            String description = getStringInput("Description: ");
            Category category = selectCategory("Select category: ");
            if (category == null) return;

            Transaction expense = TransactionFactory.createExpenseTransaction(
                amount, description, category, financeService.getDefaultCurrency());
            financeService.addTransaction(expense);
            
            System.out.println("✓ Expense added successfully!");
            System.out.println("  Amount: €" + amount);
            System.out.println("  Category: " + category.getName());
            System.out.println("  Current balance: €" + financeService.getBalance());
        } catch (Exception e) {
            System.err.println("Error adding expense: " + e.getMessage());
        }
    }

    private void addInvestment() {
        System.out.println("\n--- ADD INVESTMENT ---");
        try {
            BigDecimal amount = getBigDecimalInput("Amount: €");
            String description = getStringInput("Description: ");
            Category category = selectCategory("Select category: ");
            if (category == null) return;

            Transaction investment = TransactionFactory.createInvestmentTransaction(
                amount, description, category, financeService.getDefaultCurrency());
            financeService.addTransaction(investment);
            
            System.out.println("✓ Investment added successfully!");
            System.out.println("  Amount: €" + amount);
            System.out.println("  Category: " + category.getName());
        } catch (Exception e) {
            System.err.println("Error adding investment: " + e.getMessage());
        }
    }

    private void viewAllTransactions() {
        System.out.println("\n--- ALL TRANSACTIONS ---");
        List<Transaction> transactions = financeService.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        displayTransactions(transactions);
    }

    private void viewTransactionHistory() {
        System.out.println("\n--- TRANSACTION HISTORY (Iterator Pattern) ---");
        var iterator = financeService.getTransactionIterator();
        
        if (!iterator.hasNext()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("Browsing transactions chronologically:");
        System.out.println("-".repeat(50));
        
        int count = 0;
        while (iterator.hasNext() && count < 10) { // Show max 10 transactions
            Transaction t = iterator.next();
            System.out.printf("%-15s €%-8.2f %-20s %-15s%n",
                t.getClass().getSimpleName(),
                t.getAmount(),
                t.getDescription(),
                t.getCategory().getName());
            count++;
        }
        
        if (iterator.hasNext()) {
            System.out.println("... and " + (financeService.getAllTransactions().size() - count) + " more transactions");
        }
    }

    private void manageBudgets() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- BUDGET MANAGEMENT ---");
            System.out.println("1. View Active Budgets");
            System.out.println("2. Create New Budget");
            System.out.println("3. Budget Suggestions (Strategy Pattern)");
            System.out.println("0. Back to Main Menu");
            
            int choice = getIntInput("Select: ");
            switch (choice) {
                case 1 -> viewBudgets();
                case 2 -> createBudget();
                case 3 -> suggestBudgets();
                case 0 -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewBudgets() {
        System.out.println("\n--- ACTIVE BUDGETS ---");
        List<Budget> budgets = financeService.getActiveBudgets();
        if (budgets.isEmpty()) {
            System.out.println("No active budgets found.");
            return;
        }
        
        System.out.printf("%-15s %-10s %-10s %-10s %-10s%n", 
                "CATEGORY", "LIMIT", "SPENT", "REMAINING", "USAGE");
        System.out.println("-".repeat(60));
        
        for (Budget budget : budgets) {
            String status = budget.isExceeded() ? "EXCEEDED" : 
                           budget.isNearLimit() ? "WARNING" : "OK";
            System.out.printf("%-15s €%-9.2f €%-9.2f €%-9.2f %-7.1f%% %-8s%n",
                    budget.getCategory().getName(),
                    budget.getAmount(),
                    budget.getSpent(),
                    budget.getRemainingAmount(),
                    budget.getUsagePercentage(),
                    status);
        }
    }

    private void createBudget() {
        System.out.println("\n--- CREATE BUDGET ---");
        try {
            Category category = selectCategory("Select category: ");
            if (category == null) return;
            
            BigDecimal amount = getBigDecimalInput("Budget limit: €");
            YearMonth period = getPeriodInput("Period (MM/yyyy): ");
            
            Budget budget = new Budget(category, amount, period, financeService.getDefaultCurrency());
            financeService.addBudget(budget);
            
            System.out.println("✓ Budget created successfully!");
            System.out.println("  Category: " + category.getName());
            System.out.println("  Limit: €" + amount);
            System.out.println("  Period: " + period);
        } catch (Exception e) {
            System.err.println("Error creating budget: " + e.getMessage());
        }
    }

    private void suggestBudgets() {
        System.out.println("\n--- BUDGET SUGGESTIONS (Strategy Pattern) ---");
        try {
            Category category = selectCategory("Select category: ");
            if (category == null) return;
            
            // Show both strategies
            BudgetingStrategy conservative = new ConservativeBudgetingStrategy();
            BudgetingStrategy aggressive = new AggressiveBudgetingStrategy();
            
            Budget conservativeBudget = conservative.calculateSuggestedBudget(category, financeService.getTotalIncome());
            Budget aggressiveBudget = aggressive.calculateSuggestedBudget(category, financeService.getTotalIncome());
            
            System.out.println("\nBudget suggestions for " + category.getName() + ":");
            System.out.println("1. " + conservative.getStrategyName() + ": €" + conservativeBudget.getAmount());
            System.out.println("2. " + aggressive.getStrategyName() + ": €" + aggressiveBudget.getAmount());
            
            int choice = getIntInput("Choose strategy (1 or 2): ");
            Budget selected = (choice == 1) ? conservativeBudget : aggressiveBudget;
            
            boolean create = getBooleanInput("Create this budget? (y/n): ");
            if (create) {
                financeService.addBudget(selected);
                System.out.println("✓ Budget created using " + 
                    (choice == 1 ? conservative.getStrategyName() : aggressive.getStrategyName()));
            }
        } catch (Exception e) {
            System.err.println("Error in budget suggestion: " + e.getMessage());
        }
    }

    private void viewReports() {
        System.out.println("\n--- FINANCIAL REPORTS ---");
        System.out.println("Total Income: €" + financeService.getTotalIncome());
        System.out.println("Total Expenses: €" + financeService.getTotalExpenses());
        System.out.println("Current Balance: €" + financeService.getBalance());
        System.out.println("\nExpenses by Category:");
        System.out.println("-".repeat(30));
        
        financeService.getExpensesByCategory().forEach((category, amount) -> 
            System.out.printf("%-20s: €%.2f%n", category, amount));
    }

    private void manageCategories() {
        System.out.println("\n--- CATEGORY MANAGEMENT (Composite Pattern) ---");
        List<Category> categories = financeService.getAllCategories();
        
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }
        
        System.out.println("Available categories:");
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            System.out.printf("%d. %s - %s%n", i + 1, cat.getName(), cat.getDescription());
        }
    }

    private void testPatterns() {
        System.out.println("\n--- DESIGN PATTERNS TEST ---");
        System.out.println("This application demonstrates the following patterns:");
        System.out.println("✓ Factory Pattern - TransactionFactory creates different transaction types");
        System.out.println("✓ Composite Pattern - Category hierarchy management");
        System.out.println("✓ Strategy Pattern - Conservative vs Aggressive budgeting");
        System.out.println("✓ Observer Pattern - Budget notifications when limits exceeded");
        System.out.println("✓ Iterator Pattern - Transaction history navigation");
        System.out.println("✓ Exception Shielding - Input validation and error handling");
        System.out.println("\nAll patterns are working and can be tested through the menu options!");
    }

    private void settings() {
        System.out.println("\n--- SETTINGS ---");
        System.out.println("Current strategy: " + financeService.getBudgetingStrategy().getStrategyName());
        System.out.println("Default currency: " + financeService.getDefaultCurrency());
        
        boolean change = getBooleanInput("Change budgeting strategy? (y/n): ");
        if (change) {
            System.out.println("1. Conservative (15%)");
            System.out.println("2. Aggressive (30%)");
            int choice = getIntInput("Select: ");
            
            if (choice == 1) {
                financeService.setBudgetingStrategy(new ConservativeBudgetingStrategy());
                System.out.println("✓ Conservative strategy activated!");
            } else if (choice == 2) {
                financeService.setBudgetingStrategy(new AggressiveBudgetingStrategy());
                System.out.println("✓ Aggressive strategy activated!");
            }
        }
    }

    // Helper methods
    private Category selectCategory(String prompt) {
        System.out.println(prompt);
        List<Category> categories = financeService.getAllCategories();
        
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return null;
        }
        
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, categories.get(i).getName());
        }
        
        int choice = getIntInput("Select category: ");
        if (choice < 1 || choice > categories.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        
        return categories.get(choice - 1);
    }

    private void displayTransactions(List<Transaction> transactions) {
        System.out.printf("%-15s %-10s %-25s %-15s %-12s%n", 
                "TYPE", "AMOUNT", "DESCRIPTION", "CATEGORY", "DATE");
        System.out.println("-".repeat(80));
        
        for (Transaction t : transactions) {
            System.out.printf("%-15s €%-9.2f %-25s %-15s %-12s%n",
                    t.getClass().getSimpleName().replace("Transaction", ""),
                    t.getAmount(),
                    truncateString(t.getDescription(), 23),
                    t.getCategory().getName(),
                    t.getDate().format(dateFormatter));
        }
    }

    private String truncateString(String str, int maxLength) {
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount. Please try again.");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private boolean getBooleanInput(String prompt) {
        while (true) {
            String input = getStringInput(prompt).toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("s") || input.equals("si")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Please enter y/n.");
        }
    }

    private YearMonth getPeriodInput(String prompt) {
        while (true) {
            try {
                String input = getStringInput(prompt);
                return YearMonth.parse(input, DateTimeFormatter.ofPattern("MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format. Use MM/yyyy (e.g., 12/2024).");
            }
        }
    }

    public static void main(String[] args) {
        try {
            SimpleFinanceCLI cli = new SimpleFinanceCLI();
            cli.start();
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
