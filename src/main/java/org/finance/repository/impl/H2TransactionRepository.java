package org.finance.repository.impl;
import org.finance.database.DatabaseManager;
import org.finance.model.*;
import org.finance.repository.TransactionRepository;
import org.finance.repository.CategoryRepository;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Implementation of transaction repository with H2 database.
 */
public class H2TransactionRepository implements TransactionRepository {
    private static final Logger logger = Logger.getLogger(H2TransactionRepository.class.getName());
    private final DatabaseManager dbManager;
    private final CategoryRepository categoryRepository;

    public H2TransactionRepository(CategoryRepository categoryRepository) {
        this.dbManager = DatabaseManager.getInstance();
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Transaction save(Transaction transaction) {
        String sql = """
            MERGE INTO transactions (id, amount, description, timestamp, category_name, type, currency) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transaction.getId());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getDescription());
            stmt.setTimestamp(4, Timestamp.valueOf(transaction.getTimestamp()));
            stmt.setString(5, transaction.getCategory().getName());
            stmt.setString(6, transaction instanceof IncomeTransaction ? "INCOME" : "EXPENSE");
            stmt.setString(7, transaction.getCurrency());
            stmt.executeUpdate();
            logger.info("Transazione salvata: " + transaction.getId());
            return transaction;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel salvataggio della transazione", e);
            throw new RuntimeException("Errore nel salvataggio della transazione", e);
        }
    }
    @Override
    public Optional<Transaction> findById(String id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToTransaction(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca della transazione", e);
            throw new RuntimeException("Errore nella ricerca della transazione", e);
        }
    }
    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle transazioni", e);
            throw new RuntimeException("Errore nel recupero delle transazioni", e);
        }
        return transactions;
    }
    @Override
    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM transactions WHERE DATE(timestamp) BETWEEN ? AND ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca per intervallo date", e);
            throw new RuntimeException("Errore nella ricerca per intervallo date", e);
        }
        return transactions;
    }
    @Override
    public List<Transaction> findByCategory(String categoryName) {
        String sql = "SELECT * FROM transactions WHERE category_name = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca per categoria", e);
            throw new RuntimeException("Errore nella ricerca per categoria", e);
        }
        return transactions;
    }
    @Override
    public List<Transaction> findByType(String type) {
        String sql = "SELECT * FROM transactions WHERE type = ? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca per tipo", e);
            throw new RuntimeException("Errore nella ricerca per tipo", e);
        }
        return transactions;
    }
    @Override
    public List<Transaction> findByAmountGreaterThan(double amount) {
        String sql = "SELECT * FROM transactions WHERE amount > ? ORDER BY amount DESC";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca per importo", e);
            throw new RuntimeException("Errore nella ricerca per importo", e);
        }
        return transactions;
    }
    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Transazione eliminata: " + id);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'eliminazione della transazione", e);
            throw new RuntimeException("Errore nell'eliminazione della transazione", e);
        }
    }
    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella verifica esistenza transazione", e);
            throw new RuntimeException("Errore nella verifica esistenza transazione", e);
        }
    }
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel conteggio delle transazioni", e);
            throw new RuntimeException("Errore nel conteggio delle transazioni", e);
        }
    }
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        BigDecimal amount = rs.getBigDecimal("amount");
        String description = rs.getString("description");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
        String categoryName = rs.getString("category_name");
        String type = rs.getString("type");
        String currency = rs.getString("currency");
        Category category = categoryRepository.findById(categoryName)
                .orElse(new Category(categoryName, "Categoria temporanea"));
        if ("INCOME".equals(type)) {
            return new IncomeTransaction(id, amount, description, category, currency, timestamp);
        } else {
            return new ExpenseTransaction(id, amount, description, category, currency, timestamp);
        }
    }
}



