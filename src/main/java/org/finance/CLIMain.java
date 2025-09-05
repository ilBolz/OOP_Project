package org.finance;

import org.finance.cli.FinanceCLI;

/**
 * Launcher per l'interfaccia CLI del Personal Finance Manager.
 */
public class CLIMain {
    public static void main(String[] args) {
        try {
            FinanceCLI cli = new FinanceCLI();
            cli.start();
        } catch (Exception e) {
            System.err.println("Errore durante l'avvio dell'applicazione: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
