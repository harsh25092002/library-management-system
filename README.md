# Library Management System

A backend Library Management System built with **Core Java, JDBC, SQL, J2EE concepts and Spring Boot**. It manages books, members, and the issue/return workflow through a REST API, with transaction-safe operations to keep book copy counts and issue records consistent.

## Features
- CRUD REST APIs for **Books** and **Members**
- Issue and return workflow with automatic due-date calculation (14-day loan period)
- JDBC-based data access layer (`JdbcTemplate`) with hand-written SQL for full control over queries
- `@Transactional` service methods so a book issue/return either fully succeeds or fully rolls back
- Centralized exception handling (404 for missing records, 409 when a book has no available copies)
- Runs out of the box on an in-memory H2 database; MySQL config included for production use

## Tech Stack
Java 17, Spring Boot 3, Spring JDBC, MySQL / H2, Maven

## Project Structure
```
src/main/java/com/library/app
├── model         # Book, Member, IssueRecord
├── dao           # JDBC data access (BookDao, MemberDao, IssueRecordDao)
├── service        # Business logic + transaction boundaries
├── controller     # REST controllers
└── exception      # Custom exceptions + global handler
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/books | List all books |
| GET | /api/books/{id} | Get a book by id |
| POST | /api/books | Add a new book |
| PUT | /api/books/{id} | Update a book |
| DELETE | /api/books/{id} | Delete a book |
| GET | /api/members | List all members |
| POST | /api/members | Add a new member |
| POST | /api/issues/issue?bookId=1&memberId=1 | Issue a book to a member |
| POST | /api/issues/{issueRecordId}/return | Return an issued book |
| GET | /api/issues/member/{memberId}/active | Active loans for a member |

## Running Locally
```bash
mvn spring-boot:run
```
The app starts on `http://localhost:8080` using an in-memory H2 database pre-loaded with sample books and members (see `src/main/resources/data.sql`).

To use MySQL instead, update `src/main/resources/application.properties` with your MySQL credentials (a commented-out block is provided).

## Running Tests
```bash
mvn test
```

## Example Request
```bash
curl -X POST "http://localhost:8080/api/issues/issue?bookId=1&memberId=1"
```
