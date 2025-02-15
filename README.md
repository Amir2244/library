# Library Management System Documentation

## Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Project Setup Instructions](#project-setup-instructions)
4. [Project Structure](#project-structure)
5. [Features and Capabilities](#features-and-capabilities)
6. [Testing Instructions](#testing-instructions)
7. [Build and Deployment](#build-and-deployment)
8. [Project Configuration](#project-configuration)
9. [Quick Start Guide](#quick-start-guide)
10. [Endpoints](#endpoints)
---

## Overview

This is the documentation for the **Library Management System**, a system designed to manage library resources
efficiently.

- **Version**: 1.0

---

## Project Setup Instructions

To set up the project, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone [repository-url]

## Overview

This is the documentation for the **Library Management System**, a system designed to manage library resources
efficiently.

- **Version**: 1.0
- **Author**: Library Team

---

## Technology Stack

The following technologies are used in this project:

- Spring Boot
- Spring Cache
- Spring AOP
- Gradle
- JUnit
- Mockito
- H2 Database
- Hibernate
- JWT Authentication

---

## Project Setup Instructions

To set up the project, follow these steps:

1. **Clone the repository**:
   ```bash
   git clone [repository-url]
   ```

2. **Build the project**:
    - On Windows:
      ```bash
      gradlew.bat clean build
      ```
    - On Unix/Linux/Mac:
      ```bash
      ./gradlew clean build
      ```

3. **Run the application**:
    - On Windows:
      ```bash
      gradlew.bat bootRun
      ```
    - On Unix/Linux/Mac:
      ```bash
      ./gradlew bootRun
      ```

---

## Project Structure

The project follows the standard structure below:

```
library/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── library/
│   │           └── LibraryApplication.java
│   └── test/
│       └── java/
│           └── library/
│               └── LibraryApplicationTests.java
├── gradle/
├── gradlew
├── gradlew.bat
└── README.md
```

---

## Features and Capabilities

### Caching Support

- **Description**: Performance optimization through caching.
- **Annotation**: `@EnableCaching`

### AOP Support

- **Description**: Cross-cutting concerns management.
- **Annotation**: `@EnableAspectJAutoProxy`

### JWT Authentication

- **Description**: Secure authentication using JSON Web Tokens.
- **Annotation**: `@EnableWebSecurity`

---

## Testing Instructions

To run tests, use the following commands:

- On Windows:
  ```bash
  gradlew.bat test
  ```

- On Unix/Linux/Mac:
  ```bash
  ./gradlew test
  ```

---

## Build and Deployment

### Build the JAR File

```bash
./gradlew build
```

### Run the Application from the JAR File

```bash
java -jar build/libs/library-[version].jar
```

---

## Project Configuration

The project supports the following profiles:

- `development`
- `testing`

These profiles can be configured as needed for different environments.

---

## Quick Start Guide

Follow these steps to quickly get started with the Library Management System:

1. Clone the repository:
   ```bash
   git clone [repository-url]
   ```

2. Build the project:
   ```bash
   ./gradlew clean build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

4. Access the application at:
   ```
   http://localhost:8080
   ```



---
## Endpoints
## Authentication Controller Endpoints

### Login
- **Endpoint**: `POST /api/auth/login`
- **Request Body**:
  ```json
  {
      "email": "user@example.com",
      "password": "password123"
  }
  ```
- **Returns**: JWT Token

### Register
- **Endpoint**: `POST /api/auth/register`
- **Request Body**:
  ```json
  {
      "email": "user@example.com",
      "password": "password123"
  }
  ```
- **Returns**: JWT Token

---

## Book Controller Endpoints

### Get All Books
- **Endpoint**: `GET /api/books`

### Get Book by ID
- **Endpoint**: `GET /api/books/{id}`

### Create a New Book
- **Endpoint**: `POST /api/books`
- **Request Body**:
  ```json
  {
      "title": "Book Title",
      "author": "Author Name",
      "isbn": "1234567890"
  }
  ```

### Update a Book
- **Endpoint**: `PUT /api/books/{id}`
- **Request Body**:
  ```json
  {
      "title": "Updated Title",
      "author": "Updated Author",
      "isbn": "0987654321"
  }
  ```

### Delete a Book
- **Endpoint**: `DELETE /api/books/{id}`

---

## Patron Controller Endpoints

### Get All Patrons
- **Endpoint**: `GET /api/patrons`

### Get Patron by ID
- **Endpoint**: `GET /api/patrons/{id}`

### Create a New Patron
- **Endpoint**: `POST /api/patrons`
- **Request Body**:
  ```json
  {
      "name": "Patron Name",
      "email": "patron@example.com"
  }
  ```

### Update a Patron
- **Endpoint**: `PUT /api/patrons/{id}`
- **Request Body**:
  ```json
  {
      "name": "Updated Name",
      "email": "updated@example.com"
  }
  ```

### Delete a Patron
- **Endpoint**: `DELETE /api/patrons/{id}`

### Get Borrowing History for a Patron
- **Endpoint**: `GET /api/patrons/{id}/borrowings`

---

## Borrowing Controller Endpoints

### Borrow a Book
- **Endpoint**: `POST /api/borrow/{bookId}/patron/{patronId}`

### Return a Book
- **Endpoint**: `PUT /api/return/{bookId}/patron/{patronId}`

---

## Authentication Requirements

- **Authorization Header**:
  ```
  Authorization: Bearer {jwt_token}
  ```

- **Secured Endpoints**:
   - `/api/books/**`
   - `/api/patrons/**`
   - `/api/borrow/**`
   - `/api/return/**`

---

## Response Status Codes

- `200`: OK
- `201`: Created
- `204`: No Content
- `400`: Bad Request
- `401`: Unauthorized
- `404`: Not Found
- `409`: Conflict

---

## Testing Instructions

To test the API, follow these steps:

1. **Register** using the `AuthEndpoints.REGISTER` endpoint.
2. **Login** using the `AuthEndpoints.LOGIN` endpoint to obtain a JWT token.
3. Add the JWT token to the `Authorization` header as follows:
   ```
   Authorization: Bearer {jwt_token}
   ```
4. Test other endpoints with the valid token.

---

## Testing Tools

The following tools are recommended for testing the API:
- Postman
- curl
- HTTP client libraries
