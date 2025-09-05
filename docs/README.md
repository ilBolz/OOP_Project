# UML Diagrams Documentation

Diagrammi UML semplificati per il Personal Finance Manager project.

## Diagrammi Disponibili

### 1. **Class Diagram** (`class-diagram.puml`)

**Mostra:** Struttura delle classi principali e design pattern

**Pattern Evidenziati:**

- **Factory:** TransactionFactory per creazione transazioni
- **Composite:** Category per gerarchia categorie
- **Iterator:** TransactionHistoryIterator per navigazione
- **Strategy:** Algoritmi di budgeting diversi
- **Observer:** Notifiche budget superato
- **Singleton:** DatabaseManager per connessioni

### 2. **Transaction Sequence** (`transaction-sequence.puml`)

**Mostra:** Flusso di inserimento di una nuova transazione

**Flusso:**

1. User inserisce dati → CLI
2. CLI valida input (Exception Shielding)
3. Factory crea transazione (Factory Pattern)
4. Service salva nel database
5. Observer notifica budget (Observer Pattern)

### 3. **Report Sequence** (`report-sequence.puml`)

**Mostra:** Processo di generazione report finanziario

**Flusso:**

1. Raccolta dati dal database
2. Iterator naviga transazioni (Iterator Pattern)
3. Stream API elabora dati
4. Strategy confronta algoritmi (Strategy Pattern)
5. CLI formatta output

### 4. **Architecture Overview** (`architecture-diagram.puml`)

**Mostra:** Architettura generale del sistema

**Layer:**

- **Presentation:** CLI Interface
- **Business Logic:** FinanceService
- **Data Access:** Repository + H2 Database
- **Patterns:** Factory, Strategy, Observer, etc.

## 🔧 Come Visualizzare

### VS Code (Consigliato)

1. Installa estensione "PlantUML"
2. Apri file .puml
3. Premi `Alt+D` per preview

### Online

1. Copia contenuto file .puml
2. Vai su [plantuml.com](http://www.plantuml.com/plantuml/uml/)
3. Incolla e visualizza

## Pattern Evidenziati

I diagrammi mostrano chiaramente:

**Obbligatori (16pts):**

- Factory Pattern - TransactionFactory
- Composite Pattern - Category hierarchy
- Iterator Pattern - TransactionHistoryIterator
- Exception Shielding - Input validation

**Bonus (+19pts):**

- Strategy Pattern - Budgeting algorithms
- Observer Pattern - Budget notifications
- Singleton Pattern - DatabaseManager

**Tecnologie:**

- Collections & Generics
- Stream API & Lambdas
- Java I/O (Database)
- JUnit Testing
- Dependency Injection

I diagrammi sono **pronti per l'interview** e dimostrano tutti i requisiti dell'assignment!

- **Factory Pattern:** TransactionFactory creates different transaction types
- **Composite Pattern:** Category manages parent-child hierarchical relationships
- **Iterator Pattern:** TransactionHistoryIterator provides chronological navigation
- **Strategy Pattern:** Different budgeting algorithms (Conservative/Aggressive)
- **Observer Pattern:** Budget notifications through BudgetObserver interface
- **Singleton Pattern:** DatabaseManager ensures single connection point

### 2. **Transaction Insertion Sequence** (`transaction-sequence.puml`)

**Purpose:** Illustrates the complete flow of adding a new transaction to the system.

**Key Flow:**

1. **User Input:** CLI captures transaction details from user
2. **Factory Creation:** TransactionFactory creates appropriate transaction type
3. **Validation:** Exception shielding with input validation
4. **Persistence:** Repository saves to H2 database using try-with-resources
5. **Budget Monitoring:** Observer pattern triggers budget notifications
6. **User Feedback:** Success/error messages displayed

**Technologies Demonstrated:**

- Exception Shielding (try-catch, validation)
- Factory Pattern (dynamic creation)
- Repository Pattern (data persistence)
- Observer Pattern (budget alerts)
- Database I/O (H2 with JDBC)

### 3. **Report Generation Sequence** (`report-sequence.puml`)

**Purpose:** Shows the process of generating financial reports with data analysis.

**Key Flow:**

1. **Data Collection:** Repository layer fetches all transactions and budgets
2. **Iterator Navigation:** TransactionHistoryIterator provides chronological access
3. **Stream Processing:** Java 8 Streams filter and aggregate data
4. **Strategy Comparison:** Different budgeting strategies are calculated and compared
5. **Category Analysis:** Composite pattern enables hierarchical category reporting
6. **Formatted Output:** CLI presents structured financial report

**Technologies Demonstrated:**

- Iterator Pattern (navigation)
- Stream API & Lambdas (data processing)
- Strategy Pattern (algorithm comparison)
- Composite Pattern (category hierarchy)
- Repository Pattern (data access)
- Collections & Generics (type-safe data handling)

### 4. **Architecture Overview** (`architecture-diagram.puml`)

**Purpose:** Provides high-level view of system architecture and technology stack.

**Architecture Layers:**

- **Presentation:** CLI interface for user interaction
- **Service:** Business logic coordination
- **Repository:** Data access abstraction
- **Database:** H2 file-based persistence
- **Patterns:** Cross-cutting design pattern implementations

**Key Technologies:**

- Java SE 17 with modern features
- Google Guice for dependency injection
- H2 Database for local persistence
- JUnit 5 + Mockito for testing
- Stream API for data processing
- Collections & Generics for type safety

## 🔧 Viewing the Diagrams

### Prerequisites

To view these PlantUML diagrams, you need:

1. **VS Code with PlantUML Extension:**

   ```bash
   # Install PlantUML extension in VS Code
   # Then open any .puml file and press Alt+D to preview
   ```

2. **Online PlantUML Viewer:**

   - Copy diagram content to [plantuml.com](http://www.plantuml.com/plantuml/uml/)

3. **Command Line (if PlantUML JAR installed):**
   ```bash
   java -jar plantuml.jar *.puml
   ```

### Diagram Files

- `class-diagram.puml` - Complete class structure (main deliverable)
- `transaction-sequence.puml` - Transaction insertion flow
- `report-sequence.puml` - Report generation process
- `architecture-diagram.puml` - System architecture overview

## Pattern Implementation Evidence

Each diagram demonstrates the required design patterns:

| Pattern                 | Implementation                                                    | Diagram Reference            |
| ----------------------- | ----------------------------------------------------------------- | ---------------------------- |
| **Factory**             | TransactionFactory creates Income/Expense/Investment transactions | Class + Transaction Sequence |
| **Composite**           | Category parent-child hierarchy with recursive operations         | Class + Report Sequence      |
| **Iterator**            | TransactionHistoryIterator navigates transaction history          | Class + Report Sequence      |
| **Exception Shielding** | Try-with-resources, input validation, error propagation           | Transaction Sequence         |
| **Strategy**            | ConservativeBudgetingStrategy vs AggressiveBudgetingStrategy      | Class + Report Sequence      |
| **Observer**            | BudgetObserver receives budget exceeded notifications             | Class + Transaction Sequence |
| **Singleton**           | DatabaseManager single instance for connection management         | Class + Architecture         |

## Assignment Compliance

These diagrams fulfill the **UML diagrams (class + architectural)** requirement from the Final Project Assignment deliverables section:

**Class Diagram:** Complete system structure with all required patterns  
**Sequence Diagrams:** Transaction insertion and report generation flows  
**Architectural Diagram:** High-level system overview with technology stack  
**Pattern Documentation:** Clear identification of all design patterns  
**Technology Integration:** Demonstration of core Java technologies

The diagrams provide comprehensive documentation for interview questions about design decisions, pattern implementations, and architectural choices.
