package org.finance.repository.impl;
import org.finance.database.DatabaseManager;
import org.finance.model.Category;
import org.finance.repository.CategoryRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Implementazione del repository per le categorie con database H2.
 */
public class H2CategoryRepository implements CategoryRepository {
    private static final Logger logger = Logger.getLogger(H2CategoryRepository.class.getName());
    private final DatabaseManager dbManager;
    public H2CategoryRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }
    @Override
    public Category save(Category category) {
        String sql = """
            MERGE INTO categories (name, description, parent_name) 
            VALUES (?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getDescription());
            stmt.setString(3, category.getParent() != null ? category.getParent().getName() : null);
            stmt.executeUpdate();
            logger.info("Categoria salvata: " + category.getName());
            return category;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel salvataggio della categoria", e);
            throw new RuntimeException("Errore nel salvataggio della categoria", e);
        }
    }
    @Override
    public Optional<Category> findById(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCategory(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca della categoria", e);
            throw new RuntimeException("Errore nella ricerca della categoria", e);
        }
    }
    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories ORDER BY name";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle categorie", e);
            throw new RuntimeException("Errore nel recupero delle categorie", e);
        }
        return categories;
    }
    @Override
    public List<Category> findRootCategories() {
        String sql = "SELECT * FROM categories WHERE parent_name IS NULL ORDER BY name";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle categorie root", e);
            throw new RuntimeException("Errore nel recupero delle categorie root", e);
        }
        return categories;
    }
    @Override
    public List<Category> findByParentName(String parentName) {
        String sql = "SELECT * FROM categories WHERE parent_name = ? ORDER BY name";
        List<Category> categories = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parentName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle sottocategorie", e);
            throw new RuntimeException("Errore nel recupero delle sottocategorie", e);
        }
        return categories;
    }
    @Override
    public void deleteById(String name) {
        String sql = "DELETE FROM categories WHERE name = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Categoria eliminata: " + name);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'eliminazione della categoria", e);
            throw new RuntimeException("Errore nell'eliminazione della categoria", e);
        }
    }
    @Override
    public boolean existsById(String name) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella verifica esistenza categoria", e);
            throw new RuntimeException("Errore nella verifica esistenza categoria", e);
        }
    }
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM categories";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel conteggio delle categorie", e);
            throw new RuntimeException("Errore nel conteggio delle categorie", e);
        }
    }
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String description = rs.getString("description");
        String parentName = rs.getString("parent_name");
        Category category = new Category(name, description);
        if (parentName != null) {
            Category parent = new Category(parentName, "");
            parent.addSubcategory(category);
        }
        return category;
    }
}



