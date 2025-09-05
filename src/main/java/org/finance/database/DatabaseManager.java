package org.finance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Singleton per la gestione della connessione al database H2.
 * Implementa il pattern Singleton per garantire una sola connessione attiva.
 */
public class DatabaseManager {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;
    
    // Configurazione database H2 (file locale)
    private static final String DB_URL = "jdbc:h2:./data/finance_db;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private DatabaseManager() {
        initializeDatabase();
    }
    
    /**
     * Ottiene l'istanza singleton del DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Ottiene la connessione al database.
     */
    public Connection getConnection() throws SQLException {
        // Crea sempre una nuova connessione invece di riutilizzare quella singleton
        // per evitare problemi di connessioni chiuse
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Inizializza il database creando le tabelle necessarie.
     */
    private void initializeDatabase() {
        try (Connection initConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createTables(initConnection);
            logger.info("Database inizializzato con successo");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'inizializzazione del database", e);
            throw new RuntimeException("Impossibile inizializzare il database", e);
        }
    }
    
    /**
     * Crea le tabelle necessarie nel database.
     */
    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Tabella Categories
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    name VARCHAR(100) PRIMARY KEY,
                    description VARCHAR(500),
                    parent_name VARCHAR(100),
                    FOREIGN KEY (parent_name) REFERENCES categories(name)
                )
            """);
            
            // Tabella Transactions
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id VARCHAR(100) PRIMARY KEY,
                    amount DECIMAL(15,2) NOT NULL,
                    description VARCHAR(500),
                    timestamp TIMESTAMP NOT NULL,
                    category_name VARCHAR(100),
                    type VARCHAR(20) NOT NULL,
                    currency VARCHAR(10) DEFAULT 'EUR',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_name) REFERENCES categories(name)
                )
            """);
            
            // Tabella Budgets
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS budgets (
                    id VARCHAR(100) PRIMARY KEY,
                    category_name VARCHAR(100) NOT NULL,
                    limit_amount DECIMAL(15,2) NOT NULL,
                    period VARCHAR(10) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (category_name) REFERENCES categories(name)
                )
            """);
            
            logger.info("Tabelle create/verificate con successo");
        }
    }
    
    /**
     * Chiude tutte le connessioni aperte (non necessario con il nuovo approccio).
     * Mantenuto per compatibilità.
     */
    public void closeConnection() {
        // Con il nuovo approccio, ogni connessione viene chiusa automaticamente
        // tramite try-with-resources nei repository
        logger.info("Chiusura connessioni database (gestite automaticamente)");
    }
}
