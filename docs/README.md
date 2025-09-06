# UML Diagrams Documentation

UML diagrams for the Personal Finance Manager project.

## Available Diagrams

### 1. **Class Diagram** (`class-diagram.puml`)

**Shows:** Main class structure and design patterns

**Highlighted Patterns:**

- **Factory:** TransactionFactory for transaction creation
- **Composite:** Category for category hierarchy
- **Iterator:** TransactionHistoryIterator for navigation
- **Strategy:** Different budgeting algorithms
- **Observer:** Budget exceeded notifications
- **Singleton:** DatabaseManager for connections

### 2. **Transaction Sequence** (`transaction-sequence.puml`)

**Shows:** New transaction insertion flow

**Flow:**

1. User enters data → CLI
2. CLI validates input (Exception Shielding)
3. Factory creates transaction (Factory Pattern)
4. Service saves to database
5. Observer notifies budget (Observer Pattern)

### 3. **Report Sequence** (`report-sequence.puml`)

**Shows:** Financial report generation process

**Flow:**

1. Data collection from database
2. Iterator navigates transactions (Iterator Pattern)
3. Stream API processes data
4. Strategy compares algorithms (Strategy Pattern)
5. CLI formats output

### 4. **Architecture Overview** (`architecture-diagram.puml`)

**Shows:** System's general architecture

**Layer:**

- **Presentation:** CLI Interface
- **Business Logic:** FinanceService
- **Data Access:** Repository + H2 Database
- **Patterns:** Factory, Strategy, Observer, etc.

## How to View Diagrams

**VS Code (Recommended):**

1. Install "PlantUML" extension
2. Open .puml file
3. Press `Alt+D` for preview

**Online:**

- Copy content to [plantuml.com](http://www.plantuml.com/plantuml/uml/)

**Command Line:**

```bash
java -jar plantuml.jar *.puml
```

## Implemented Patterns

**Core Patterns:**

- Factory Pattern - TransactionFactory
- Composite Pattern - Category hierarchy
- Iterator Pattern - TransactionHistoryIterator
- Exception Shielding - Input validation

**Additional Patterns:**

- Strategy Pattern - Budgeting algorithms
- Observer Pattern - Budget notifications
- Singleton Pattern - DatabaseManager

**Technologies:**

- Collections & Generics
- Stream API & Lambdas
- H2 Database
- JUnit Testing

## Design Pattern Implementation

| Pattern                 | Implementation                                                    | Location                     |
| ----------------------- | ----------------------------------------------------------------- | ---------------------------- |
| **Factory**             | TransactionFactory creates Income/Expense/Investment transactions | Class + Transaction Sequence |
| **Composite**           | Category parent-child hierarchy with recursive operations         | Class + Report Sequence      |
| **Iterator**            | TransactionHistoryIterator navigates transaction history          | Class + Report Sequence      |
| **Exception Shielding** | Input validation and error handling                               | Transaction Sequence         |
| **Strategy**            | ConservativeBudgetingStrategy vs AggressiveBudgetingStrategy      | Class + Report Sequence      |
| **Observer**            | BudgetObserver receives budget notifications                      | Class + Transaction Sequence |
| **Singleton**           | DatabaseManager single instance for connection management         | Class + Architecture         |

| Pattern                 | Implementation                                                    | Diagram Reference            |
| ----------------------- | ----------------------------------------------------------------- | ---------------------------- |
| **Factory**             | TransactionFactory creates Income/Expense/Investment transactions | Class + Transaction Sequence |
| **Composite**           | Category parent-child hierarchy with recursive operations         | Class + Report Sequence      |
| **Iterator**            | TransactionHistoryIterator navigates transaction history          | Class + Report Sequence      |
| **Exception Shielding** | Try-with-resources, input validation, error propagation           | Transaction Sequence         |
| **Strategy**            | ConservativeBudgetingStrategy vs AggressiveBudgetingStrategy      | Class + Report Sequence      |
| **Observer**            | BudgetObserver receives budget exceeded notifications             | Class + Transaction Sequence |
| **Singleton**           | DatabaseManager single instance for connection management         | Class + Architecture         |

## Summary

The UML diagrams document complete system structure with all design patterns, transaction flows, and architectural overview.
