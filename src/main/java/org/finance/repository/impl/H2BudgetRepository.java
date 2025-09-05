package org.finance.repository.impl;

import org.finance.database.DatabaseManager;
import org.finance.model.Budget;
import org.finance.model.Category;
import org.finance.repository.BudgetRepository;
import org.finance.repository.CategoryRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implementazione del repository per i budget con database H2.
 */
public class H2BudgetRepository implements BudgetRepository {
    private static final Logger logger = Logger.getLogger(H2BudgetRepository.class.getName());
    private final DatabaseManager dbManager;
    private final CategoryRepository categoryRepository;
    
    public H2BudgetRepository(CategoryRepository categoryRepository) {
        this.dbManager = DatabaseManager.getInstance();
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public Budget save(Budget budget) {
        String sql = """
            MERGE INTO budgets (id, category_name, limit_amount, period) 
            VALUES (?, ?, ?, ?)
        """;
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, budget.getId());
            stmt.setString(2, budget.getCategory().getName());
            stmt.setBigDecimal(3, budget.getAmount());
            stmt.setString(4, budget.getPeriod().toString());
            
            stmt.executeUpdate();
            logger.info("Budget salvato: " + budget.getId());
            return budget;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel salvataggio del budget", e);
            throw new RuntimeException("Errore nel salvataggio del budget", e);
        }
    }
    
    @Override
    public Optional<Budget> findById(String id) {
        String sql = "SELECT * FROM budgets WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToBudget(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca del budget", e);
            throw new RuntimeException("Errore nella ricerca del budget", e);
        }
    }
    
    @Override
    public List<Budget> findAll() {
        String sql = "SELECT * FROM budgets ORDER BY period DESC";
        List<Budget> budgets = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                budgets.add(mapResultSetToBudget(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dei budget", e);
            throw new RuntimeException("Errore nel recupero dei budget", e);
        }
        
        return budgets;
    }
    
    @Override
    public List<Budget> findByCategory(String categoryName) {
        String sql = "SELECT * FROM budgets WHERE category_name = ? ORDER BY period DESC";
        List<Budget> budgets = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(mapResultSetToBudget(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca budget per categoria", e);
            throw new RuntimeException("Errore nella ricerca budget per categoria", e);
        }
        
        return budgets;
    }
    
    @Override
    public List<Budget> findByPeriod(YearMonth period) {
        String sql = "SELECT * FROM budgets WHERE period = ?";
        List<Budget> budgets = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, period.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(mapResultSetToBudget(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca budget per periodo", e);
            throw new RuntimeException("Errore nella ricerca budget per periodo", e);
        }
        
        return budgets;
    }
    
    @Override
    public List<Budget> findActiveBudgets() {
        YearMonth currentMonth = YearMonth.now();
        String sql = "SELECT * FROM budgets WHERE period >= ? ORDER BY period ASC";
        List<Budget> budgets = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, currentMonth.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                budgets.add(mapResultSetToBudget(rs));
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca budget attivi", e);
            throw new RuntimeException("Errore nella ricerca budget attivi", e);
        }
        
        return budgets;
    }
    
    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM budgets WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Budget eliminato: " + id);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'eliminazione del budget", e);
            throw new RuntimeException("Errore nell'eliminazione del budget", e);
        }
    }
    
    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM budgets WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella verifica esistenza budget", e);
            throw new RuntimeException("Errore nella verifica esistenza budget", e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM budgets";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel conteggio dei budget", e);
            throw new RuntimeException("Errore nel conteggio dei budget", e);
        }
    }
    
    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String categoryName = rs.getString("category_name");
        BigDecimal limitAmount = rs.getBigDecimal("limit_amount");
        YearMonth period = YearMonth.parse(rs.getString("period"));
        
        // Recupera la categoria (o crea una temporanea se non trovata)
        Category category = categoryRepository.findById(categoryName)
                .orElse(new Category(categoryName, "Categoria temporanea"));
        
        return new Budget(id, category, limitAmount, period);
    }
}
