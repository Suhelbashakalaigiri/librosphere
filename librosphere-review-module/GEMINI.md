# Review Module

This module manages the approval and review workflow for books in Librosphere.

## Features

- **Review Submission**: Move books from `DRAFT` to `REVIEW` status.
- **Approval Workflow**: Move books from `REVIEW` to `PUBLISHED` status.
- **Rejection/Changes Workflow**: Move books from `REVIEW` back to `DRAFT` status with feedback.
- **Audit Trail**: Every action is recorded in the `ReviewHistory`.
- **Event-Driven**: Publishes events for status changes.

## Business Rules

1. Only `DRAFT` books can be submitted for review.
2. `submitBookForReview` transitions status to `REVIEW`.
3. `approveBook` transitions status to `PUBLISHED`.
4. `rejectBook` and `requestChanges` transition status back to `DRAFT`.
5. Invalid transitions throw specific exceptions.
6. History is immutable and mandatory for every action.

## Tech Stack

- **Java 21**: Using Records for DTOs and Pattern Matching.
- **Spring Boot 4.x**: Core framework.
- **Spring GraphQL**: API layer.
- **Spring Data JPA**: Persistence layer with MySQL.
- **MapStruct**: Object mapping.
- **Lombok**: Boilerplate reduction.

## API Documentation (GraphQL)

### Mutations

- `submitBookForReview(bookId: ID!)`: Submits a draft book for review.
- `approveBook(reviewId: ID!)`: Approves a book in review.
- `rejectBook(reviewId: ID!, reason: String!)`: Rejects a book with a reason.
- `requestChanges(reviewId: ID!, feedback: String!)`: Requests changes with feedback.

### Queries

- `getReviewStatus(bookId: ID!)`: Gets the current review status of a book.
- `getReviewHistory(bookId: ID!)`: Gets the complete history of review actions for a book.

## Integration

Integrates with `BookModule` via `BookService`. Depends on `BookStatus` enum and `Book` entity from the book module.
