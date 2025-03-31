# Sakila Project in Spring Boot with Hexagonal Architecture

## Description
This project aims to implement the Sakila database (MySQL) in Spring Boot using Java, following the **Hexagonal Architecture (Ports and Adapters)** principles. The goal is to decouple business logic from external technologies (e.g., databases, frameworks), improving scalability, maintainability, and testability.

## Key Dependencies
- **Spring Boot Starter Web**: For REST layer.
- **Spring Data JPA**: Database integration.
- **MySQL Driver**: Connection to Sakila database.
- **Lombok**: Reduce boilerplate code.
- **H2 Database** (optional): For unit testing.

## Folder Structure (Hexagonal)
```
src/main/java/com/sakila/sakila_project
├── domain                # Core Business Logic
│   ├── model             # Domain entities
│   ├── ports             # Interfaces (Ports)
│   │   ├── input         # Input ports (use cases)
│   │   └── output        # Output ports (e.g., repositories)
│   └── service           # Domain services
│
├── application           # Use case orchestration
│   ├── dto               # Data Transfer Objects
│   └── usecase           # Use case implementations
│
├── infrastructure        # External adapters
│   ├── adapters          # Port implementations
│   │   ├── input         # E.g., REST controllers
│   │   └── output        # E.g., JPA repositories
│   └── config            # Spring/DB configurations
│
└── adapters              # Presentation/external layer (optional)
└── rest              # REST controllers (if not in infrastructure)
```

## Layer Explanation
1. **Domain**:
   - Contains pure business logic (entities, rules).
   - Defines `ports` (interfaces) to interact with the outside.
   - Example: `FilmRepositoryPort` (interface to save a film).

2. **Application**:
   - Implements use cases (services orchestrating workflows).
   - Uses DTOs to transfer data between layers.

3. **Infrastructure**:
   - Adapters implementing ports defined in `domain`.
   - Example: `FilmJpaRepository` (JPA implementation).
   - Technical configurations (DB, security, etc.).

4. **Adapters** (optional):
   - Entry points for the app (REST, GraphQL, events).

## Benefits
- **Decoupling**: Changing databases/frameworks doesn’t affect the core.
- **Testability**: Easy to mock external dependencies.
- **Maintainability**: Clearly separated layers.
