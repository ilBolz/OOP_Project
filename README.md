# Personal Finance Manager

A Java-based personal finance management application with comprehensive transaction tracking, budgeting, and reporting capabilities.

## Features

- Transaction management (income, expenses, investments)
- Hierarchical category organization
- Budget monitoring with automated notifications
- Financial reporting and analytics
- Local H2 database persistence
- Interactive command-line interface

## Technology Stack

- Java SE 17
- H2 Database
- JDBC
- Maven
- JUnit 5 + Mockito

## Design Patterns and Technologies

### Design Patterns Implementation

- **Factory Pattern** - Dynamic transaction creation based on type (Income, Expense, Investment)
- **Composite Pattern** - Hierarchical category management enabling parent-child relationships
- **Iterator Pattern** - Sequential navigation through transaction history with filtering capabilities
- **Observer Pattern** - Real-time budget threshold notifications when limits are exceeded
- **Strategy Pattern** - Configurable budgeting algorithms (conservative vs aggressive approaches)
- **Singleton Pattern** - Centralized database connection management ensuring single instance
- **Exception Shielding** - Comprehensive input validation and controlled error propagation

### Technology Justification

- **Java SE 17** - Leverages modern language features including records, pattern matching, and enhanced stream operations
- **H2 Database** - Embedded database providing zero-configuration persistence with full SQL support
- **JDBC** - Standard database connectivity ensuring portability and performance
- **Maven** - Dependency management and build automation with standardized project structure
- **JUnit 5 + Mockito** - Comprehensive testing framework supporting modern testing patterns and dependency mocking

## Architecture

The application follows a layered architecture:

- **Model Layer** - Domain entities (Transaction, Category, Budget)
- **Repository Layer** - Data access with H2 database
- **Service Layer** - Business logic implementation
- **Presentation Layer** - Command-line interface

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Installation and Setup

1. Clone the repository:

   ```bash
   git clone <repository-url>
   cd personal-finance-manager
   ```

2. Compile the project:

   ```bash
   mvn clean compile
   ```

3. Run tests:

   ```bash
   mvn test
   ```

4. Start the application:
   ```bash
   mvn exec:java -Dexec.mainClass="org.finance.cli.PersistentFinanceCLI"
   ```

### Alternative Execution Method

If you encounter classpath issues:

```bash
mvn compile dependency:copy-dependencies
java -cp "target/classes;target/dependency/*" org.finance.cli.PersistentFinanceCLI
```

## Database Schema

The application uses H2 database with the following schema:

```sql
CREATE TABLE categories (
    name VARCHAR(100) PRIMARY KEY,
    description VARCHAR(500),
    parent_name VARCHAR(100),
    FOREIGN KEY (parent_name) REFERENCES categories(name)
);

CREATE TABLE transactions (
    id VARCHAR(100) PRIMARY KEY,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(500),
    timestamp TIMESTAMP NOT NULL,
    category_name VARCHAR(100),
    type VARCHAR(20) NOT NULL,
    currency VARCHAR(10) DEFAULT 'EUR',
    FOREIGN KEY (category_name) REFERENCES categories(name)
);

CREATE TABLE budgets (
    id VARCHAR(100) PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    limit_amount DECIMAL(15,2) NOT NULL,
    period VARCHAR(10) NOT NULL,
    FOREIGN KEY (category_name) REFERENCES categories(name)
);
```

## Project Structure

```
src/
├── main/java/org/finance/
│   ├── cli/                    # Command-line interface
│   ├── database/               # Database management
│   ├── factory/                # Factory pattern implementation
│   ├── iterator/               # Iterator pattern implementation
│   ├── model/                  # Domain models
│   ├── observer/               # Observer pattern implementation
│   ├── repository/             # Data access layer
│   ├── service/                # Business logic
│   └── strategy/               # Strategy pattern implementation
└── test/java/org/finance/      # Unit tests
```

## Usage

The application provides an interactive CLI with the following capabilities:

- **Transaction Management** - Add, view, and categorize transactions
- **Budget Control** - Set category-specific budgets with notifications
- **Financial Reports** - Generate monthly and annual financial summaries
- **Category Management** - Create and organize hierarchical categories

## UML Diagrams

The project includes comprehensive UML documentation demonstrating architectural design and pattern implementations:

### Class Diagrams

- **System Structure** - Complete class hierarchy showing relationships between domain models
- **Design Patterns** - Visual representation of Factory, Composite, Iterator, Observer, Strategy, and Singleton patterns
- **Repository Layer** - Generic repository interfaces with concrete H2 implementations

### Architectural Diagrams

- **Layered Architecture** - Separation of concerns across Model, Repository, Service, and Presentation layers
- **Component Interactions** - Data flow between CLI, services, repositories, and database
- **Dependency Injection** - Manual constructor-based dependency management structure

**Viewing Instructions:**

- Install PlantUML extension in VS Code and press Alt+D to preview
- Copy diagram content to plantuml.com for online viewing
- All diagrams located in `/docs` directory with detailed documentation

## Testing

The project includes comprehensive unit tests covering all major components:

```bash
mvn test
```

Test coverage includes repository operations, business logic, and design pattern implementations.

## Known Limitations and Future Work

### Current Limitations

- Single-user operation without authentication system
- Local H2 database limits scalability for enterprise use
- CLI-only interface may not suit all user preferences
- Report generation limited to CSV/JSON formats

### Planned Enhancements

- **Multi-user Support** - User authentication and session management
- **Web Interface** - REST API with modern frontend framework
- **Advanced Reporting** - PDF generation and interactive charts
- **Data Import/Export** - Bank statement integration and backup functionality
- **Mobile Application** - Cross-platform mobile interface
- **Real-time Sync** - Cloud-based data synchronization across devices
