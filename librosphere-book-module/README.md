# Librosphere - Book Module

The **Book Module** is a core component of the Smart Faculty Book & Academic Resource Management System. It is designed as a production-grade library module within a **Modular Monolith** architecture, providing robust management for academic resources and their version history.

## 🚀 Features

*   **GraphQL API**: Pure GraphQL interface for all book management operations.
*   **Version Control**: Mandatory versioning for published books, ensuring a complete audit trail of changes.
*   **State Machine**: Strict enforcement of book lifecycle states using Java 21 Sealed Classes: `DRAFT` → `REVIEW` → `PUBLISHED` → `ARCHIVED`.
*   **Soft Delete**: Implemented using Hibernate `@SQLDelete` and `@SQLRestriction` to preserve data integrity.
*   **Advanced Search**: Support for title, ISBN, faculty ID, and status filtering with pagination.
*   **Production Standards**: JPA auditing, unique ISBN validation, and MapStruct-based high-performance mapping.

## 🛠️ Tech Stack

*   **Java 21**: Utilizing modern features like **Records** for DTOs and **Sealed Classes** for state management.
*   **Spring Boot 4.0.2**: Core framework.
*   **Spring GraphQL**: For API exposure.
*   **Spring Data JPA**: For persistence.
*   **MySQL**: Production database.
*   **MapStruct**: For efficient Entity-to-DTO conversion.
*   **Lombok**: To reduce boilerplate.
*   **JUnit 5 & Mockito**: For comprehensive testing.

## 🏗️ Architecture

### Package Structure
The module follows a **package-by-feature** modular structure:
*   `entity`: JPA entities (`Book`, `BookVersion`).
*   `dto`: Immutable Java Records for data transfer.
*   `state`: Sealed interface hierarchy for the Book State pattern.
*   `graphql`: GraphQL controllers and schema.
*   `service`: Core business logic implementation.
*   `repository`: Spring Data repositories.
*   `mapper`: MapStruct interfaces.
*   `exception`: Custom exceptions and GraphQL error resolvers.

### State Management (Sealed Classes)
The book lifecycle is managed via a `sealed interface BookState`, ensuring that state transitions are strictly governed by business rules:
*   `DraftState`: Allows transitions to `REVIEW` or `ARCHIVED`.
*   `ReviewState`: Allows transitions to `PUBLISHED`, `DRAFT`, or `ARCHIVED`.
*   `PublishedState`: Allows transition to `ARCHIVED`. Changes require the `versionBook` mutation.
*   `ArchivedState`: Allows transition back to `DRAFT`.

## 🛰️ GraphQL API Samples

### Mutations

#### 1. Create a new Book (DRAFT)
```graphql
mutation {
  createBook(input: {
    title: "Advanced Java 21 Architecture"
    isbn: "978-3-16-148410-0"
    description: "Deep dive into modern Java features"
    facultyId: 101
    initialContent: "Initial manuscript content..."
  }) {
    id
    title
    status
    currentVersion
  }
}
```

#### 2. Transition to Published
```graphql
mutation {
  publishBook(id: "1") {
    id
    status
  }
}
```

#### 3. Create a New Version
*Note: Only allowed for PUBLISHED books. The book status resets to DRAFT for the new version.*
```graphql
mutation {
  versionBook(input: {
    bookId: "1"
    content: "Updated content for the new edition"
    changeLog: "Added sections on Project Loom and Virtual Threads"
  }) {
    id
    currentVersion
    status
  }
}
```

### Queries

#### 1. Search Books with Filters
```graphql
query {
  searchBooks(criteria: {
    status: PUBLISHED
    facultyId: "101"
    page: 0
    size: 10
  }) {
    id
    title
    isbn
    currentVersion
  }
}
```

#### 2. Get Version History
```graphql
query {
  getBookVersions(bookId: "1") {
    versionNumber
    content
    changeLog
    createdAt
  }
}
```

## 🔧 Development

### Build
This is a library module. Repackaging is disabled as it is intended to be consumed by the main `application` module.

```bash
mvn clean install -pl librosphere-book-module
```

### Testing
The module includes a comprehensive test suite covering business rules, state transitions, and service logic.

```bash
mvn test -pl librosphere-book-module
```

## 📝 Configuration
Production settings are located in `src/main/resources/application.yaml`. Default environment variables:
*   `MYSQL_HOST`: (default: localhost)
*   `MYSQL_PORT`: (default: 3306)
*   `MYSQL_DATABASE`: (default: librosphere_book)
*   `MYSQL_USER`: (default: root)
*   `MYSQL_PASSWORD`: (default: root)
