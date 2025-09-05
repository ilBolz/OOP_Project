package org.finance.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Singleton for managing H2 database connections.
 * Implements the Singleton pattern to ensure only one active connection.
 */
public class DatabaseManager {
    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:h2:./data/finance_db;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private DatabaseManager() {
        initializeDatabase();
    }

    /**
     * Gets the singleton instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Gets the database connection.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Initializes the database by creating necessary tables.
     */
    private void initializeDatabase() {
        try (Connection initConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            createTables(initConnection);
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Unable to initialize database", e);
        }
    }

    /**
     * Creates necessary tables in the database.
     */
    private void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    name VARCHAR(100) PRIMARY KEY,
                    description VARCHAR(500),
                    parent_name VARCHAR(100),
                    FOREIGN KEY (parent_name) REFERENCES categories(name)
                )
            """);
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
            logger.info("Tables created/verified successfully");
        }
    }
    
    public void closeConnection() {
        logger.info("Closing database connections");
    }
}



