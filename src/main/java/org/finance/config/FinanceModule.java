package org.finance.config;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.finance.repository.*;
import org.finance.repository.impl.*;
import org.finance.service.PersistentFinanceService;
/**
 * Modulo di configurazione per Google Guice.
 * Configura l'injection delle dipendenze per i repository.
 */
public class FinanceModule extends AbstractModule {
    @Override
    protected void configure() {
    }
    @Provides
    @Singleton
    CategoryRepository provideCategoryRepository() {
        return new H2CategoryRepository();
    }
    @Provides
    @Singleton
    TransactionRepository provideTransactionRepository(CategoryRepository categoryRepository) {
        return new H2TransactionRepository(categoryRepository);
    }
    @Provides
    @Singleton
    BudgetRepository provideBudgetRepository(CategoryRepository categoryRepository) {
        return new H2BudgetRepository(categoryRepository);
    }
    @Provides
    @Singleton
    PersistentFinanceService provideFinanceService(TransactionRepository transactionRepository,
                                                   BudgetRepository budgetRepository,
                                                   CategoryRepository categoryRepository) {
        return new PersistentFinanceService(transactionRepository, budgetRepository, categoryRepository);
    }
}



