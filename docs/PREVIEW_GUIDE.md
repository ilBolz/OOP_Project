# UML Diagrams Preview Guide

## Quick Preview Instructions

### Using VS Code (Recommended)

1. Install the **PlantUML** extension by jebbs
2. Open any `.puml` file in this directory
3. Press `Alt+D` or `Ctrl+Shift+P` → "PlantUML: Preview Current Diagram"
4. The diagram will render in a preview pane

### Using Online Viewer

1. Copy the content of any `.puml` file
2. Go to [plantuml.com](http://www.plantuml.com/plantuml/uml/)
3. Paste the content and click "Submit"
4. View the rendered diagram

### Diagram Files

- `class-diagram.puml` - Main class structure (most important for assignment)
- `transaction-sequence.puml` - Transaction insertion flow
- `report-sequence.puml` - Report generation process
- `architecture-diagram.puml` - System overview

### Key Design Patterns Highlighted

Each diagram clearly shows the implementation of:

- Factory Pattern (TransactionFactory)
- Composite Pattern (Category hierarchy)
- Iterator Pattern (TransactionHistoryIterator)
- Strategy Pattern (Budgeting strategies)
- Observer Pattern (Budget notifications)
- Singleton Pattern (DatabaseManager)
- Exception Shielding (try-with-resources)
