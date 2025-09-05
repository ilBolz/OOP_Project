package org.finance.observer;
import org.finance.model.Budget;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Concrete implementation of BudgetObserver that shows notifications on the console.
 */
public class ConsoleBudgetObserver implements BudgetObserver {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    @Override
    public void onBudgetExceeded(Budget budget, BigDecimal overspentAmount) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("\n" + "=".repeat(60));
        System.out.println("BUDGET SUPERATO!");
        System.out.println("=".repeat(60));
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Categoria: " + budget.getCategory().getFullPath());
        System.out.println("Budget previsto: " + budget.getAmount() + " " + budget.getCurrency());
        System.out.println("Importo speso: " + budget.getSpent() + " " + budget.getCurrency());
        System.out.println("Superamento: " + overspentAmount + " " + budget.getCurrency());
        System.out.println("Periodo: " + budget.getPeriod());
        System.out.println("=".repeat(60) + "\n");
    }
    @Override
    public void onBudgetNearLimit(Budget budget, BigDecimal remainingAmount) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("\n" + "ATTENZIONE: Budget vicino al limite!");
        System.out.println("-".repeat(50));
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Categoria: " + budget.getCategory().getFullPath());
        System.out.println("Budget previsto: " + budget.getAmount() + " " + budget.getCurrency());
        System.out.println("Importo speso: " + budget.getSpent() + " " + budget.getCurrency());
        System.out.println("Rimanente: " + remainingAmount + " " + budget.getCurrency());
        System.out.println("Utilizzo: " + budget.getUsagePercentage() + "%");
        System.out.println("Periodo: " + budget.getPeriod());
        System.out.println("-".repeat(50) + "\n");
    }
    @Override
    public void onExpenseAdded(Budget budget, BigDecimal expenseAmount) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.println("Spesa registrata:");
        System.out.println("   Categoria: " + budget.getCategory().getName());
        System.out.println("   Importo: " + expenseAmount + " " + budget.getCurrency());
        System.out.println("   Budget utilizzato: " + budget.getUsagePercentage() + "%");
        System.out.println("   Rimanente: " + budget.getRemainingAmount() + " " + budget.getCurrency());
        System.out.println("   Timestamp: " + timestamp);
        System.out.println();
    }
}



