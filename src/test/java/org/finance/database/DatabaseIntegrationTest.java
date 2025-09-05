package org.finance.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per il database.
 */
public class DatabaseIntegrationTest {
    
    private DatabaseManager dbManager;
    
    @BeforeEach
    void setUp() {
        dbManager = DatabaseManager.getInstance();
    }
    
    @AfterEach
    void tearDown() {
        dbManager.closeConnection();
    }
    
    @Test
    void testDatabaseConnection() {
        try {
            var connection = dbManager.getConnection();
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            System.out.println("✓ Connessione database stabilita con successo!");
        } catch (Exception e) {
            fail("Errore nella connessione al database: " + e.getMessage());
        }
    }
}
