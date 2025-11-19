# C4 Model Architecture Documentation - Blog Post Application

This document provides a comprehensive architectural view of the Blog Post Application using the C4 Model with Mermaid diagrams.

## Table of Contents

1. [System Context Diagram (Level 1)](#level-1-system-context-diagram)
2. [Container Diagram (Level 2)](#level-2-container-diagram)
3. [Component Diagram (Level 3)](#level-3-component-diagram)
4. [Code Diagram (Level 4)](#level-4-code-diagram)

---

## Level 1: System Context Diagram

The System Context diagram shows the big picture - how the Blog Post Application fits into the world around it.

```mermaid
C4Context
    title System Context Diagram for Blog Post Application

    Person(reader, "Blog Reader", "A user who reads blog posts and subscribes to updates")
    Person(author, "Blog Author", "A content creator who writes and publishes blog posts")

    System(blogSystem, "Blog Post Application", "Allows authors to create and publish blog posts, and readers to view posts and subscribe to notifications")

    System_Ext(emailSystem, "Email System", "Gmail SMTP Service for sending notification emails to subscribers")
    System_Ext(database, "PostgreSQL Database", "Supabase-hosted PostgreSQL database storing users, posts, and subscribers")

    Rel(reader, blogSystem, "Reads posts, subscribes to updates", "HTTPS/JSON")
    Rel(author, blogSystem, "Creates and manages posts, notifies subscribers", "HTTPS/JSON")
    Rel(blogSystem, emailSystem, "Sends email notifications", "SMTP")
    Rel(blogSystem, database, "Reads from and writes to", "JDBC/PostgreSQL")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

**Key Elements:**

-   **Blog Reader**: End users who consume content and can subscribe to updates
-   **Blog Author**: Authenticated users with author privileges who create content
-   **Blog Post Application**: The core Spring Boot application
-   **Email System**: Gmail SMTP for subscriber notifications
-   **PostgreSQL Database**: Supabase-hosted database for data persistence

---

## Level 2: Container Diagram

The Container diagram zooms into the Blog Post Application and shows the high-level technical building blocks.

```mermaid
C4Container
    title Container Diagram for Blog Post Application

    Person(reader, "Blog Reader", "Reads posts and subscribes")
    Person(author, "Blog Author", "Creates and publishes posts")

    System_Boundary(blogSystem, "Blog Post Application") {
        Container(webApp, "Spring Boot API", "Java 17, Spring Boot 3.2.3", "Provides REST API for blog functionality, handles authentication and authorization")
        ContainerDb(database, "Database", "PostgreSQL", "Stores users, posts, subscribers, and relationships")
    }

    System_Ext(emailService, "Email Service", "Gmail SMTP", "Sends notification emails")

    Rel(reader, webApp, "Views posts, subscribes", "HTTPS/JSON, Port 8080")
    Rel(author, webApp, "Manages posts, notifies", "HTTPS/JSON + JWT, Port 8080")
    Rel(webApp, database, "Reads/writes data", "JDBC/PostgreSQL, Port 5432")
    Rel(webApp, emailService, "Sends emails", "SMTP, Port 587")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

**Key Technologies:**

-   **Spring Boot 3.2.3** with Java 17
-   **PostgreSQL** on Supabase (AWS EU Central)
-   **JWT Authentication** for security
-   **Spring Security** for authorization
-   **Spring Mail** for email notifications

---

## Level 3: Component Diagram

The Component diagram shows the internal structure of the Spring Boot application container.

```mermaid
C4Component
    title Component Diagram for Spring Boot API

    Person(reader, "Blog Reader")
    Person(author, "Blog Author")

    Container_Boundary(api, "Spring Boot API") {
        Component(authController, "Auth Controller", "Spring @RestController", "Handles user registration and login")
        Component(postsController, "Posts Controller", "Spring @RestController", "Manages blog post CRUD operations")
        Component(userController, "User Controller", "Spring @RestController", "Manages user profile operations")
        Component(notificationController, "Notification Controller", "Spring @RestController", "Handles subscriber notifications")

        Component(securityConfig, "Security Configuration", "Spring Security", "JWT authentication and authorization")
        Component(jwtFilter, "JWT Filter", "OncePerRequestFilter", "Validates JWT tokens on each request")
        Component(tokenManager, "Token Manager", "Component", "Generates and validates JWT tokens")

        Component(postService, "Post Service", "Service Layer", "Business logic for posts")
        Component(userService, "User Service", "Service Layer", "Business logic for users")
        Component(emailService, "Email Service", "Service Layer", "Email notification logic")

        Component(postRepo, "Post Repository", "JPA Repository", "Data access for posts")
        Component(userRepo, "User Repository", "JPA Repository", "Data access for users")
        Component(subscriberRepo, "Subscriber Repository", "JPA Repository", "Data access for subscribers")

        Component(entities, "Domain Entities", "JPA Entities", "PostEntity, UserEntity, SubscribersEntity")
        Component(dtos, "DTOs", "Data Transfer Objects", "PostDTO, UserDTO, LoginUserDTO, etc.")
    }

    ContainerDb(db, "Database", "PostgreSQL", "Data storage")
    System_Ext(emailSystem, "Email System", "Gmail SMTP")

    Rel(reader, postsController, "GET /api/posts", "HTTPS/JSON")
    Rel(reader, notificationController, "POST /api/notifications/subscribe", "HTTPS/JSON")
    Rel(author, authController, "POST /api/auth/login", "HTTPS/JSON")
    Rel(author, postsController, "POST /api/posts/create-article", "HTTPS/JSON")
    Rel(author, notificationController, "POST /api/notifications/notify-new-post", "HTTPS/JSON")

    Rel(authController, userService, "Uses")
    Rel(authController, tokenManager, "Uses")
    Rel(postsController, postService, "Uses")
    Rel(userController, userService, "Uses")
    Rel(notificationController, emailService, "Uses")

    Rel(jwtFilter, tokenManager, "Validates tokens")
    Rel(jwtFilter, userService, "Loads user details")

    Rel(postService, postRepo, "Uses")
    Rel(userService, userRepo, "Uses")
    Rel(emailService, subscriberRepo, "Uses")

    Rel(postRepo, entities, "Maps to")
    Rel(userRepo, entities, "Maps to")
    Rel(subscriberRepo, entities, "Maps to")

    Rel(postRepo, db, "JDBC")
    Rel(userRepo, db, "JDBC")
    Rel(subscriberRepo, db, "JDBC")

    Rel(emailService, emailSystem, "Sends emails", "SMTP")

    UpdateLayoutConfig($c4ShapeInRow="4", $c4BoundaryInRow="1")
```

**Component Layers:**

1. **Controller Layer**:

    - `AuthController`: User authentication (register/login)
    - `PostsController`: Blog post management (CRUD, pagination)
    - `UserController`: User profile management
    - `NotificationController`: Subscriber and notification management

2. **Security Layer**:

    - `WebSecurityConfig`: Security configuration
    - `JwtFilter`: Request interception and token validation
    - `TokenManager`: JWT token generation and validation
    - `JwtUserDetailsService`: User authentication service

3. **Service Layer**:

    - `PostService`: Post business logic
    - `UserService`: User business logic
    - `EmailService`: Email notification logic

4. **Repository Layer**:

    - `PostRepository`: Post data access
    - `UserRepository`: User data access
    - `SubscribersRepository`: Subscriber data access

5. **Domain Layer**:
    - DTOs for data transfer
    - Entities for database mapping

---

## Level 4: Code Diagram

The Code diagram shows the internal structure of key components. Here we focus on the Post Management flow.

### Post Management Component Details

```mermaid
classDiagram
    class PostsController {
        -PostServiceImp postServices
        +getPaginatedPosts(page, size, sortBy, direction) ResponseEntity~Page~PostSummaryDTO~~
        +uploadPost(PostDTO) ResponseEntity
        +getPostById(id) ResponseEntity
        +updatePost(id, PostDTO) ResponseEntity
        +deletePost(id) ResponseEntity
        +getPostsByAuthor(authorId) ResponseEntity
    }

    class PostServiceImp {
        -PostRepository postRepository
        -UserRepository userRepository
        +uploadPost(PostEntity) PostEntity
        +getPostById(id) Optional~PostEntity~
        +getAllPosts() List~PostEntity~
        +updatePost(id, PostEntity) PostEntity
        +deletePost(id) void
        +existsByContent(content) boolean
        +getPostsByAuthorId(authorId) List~PostEntity~
    }

    class PostRepository {
        <<interface>>
        +findById(id) Optional~PostEntity~
        +findAll() List~PostEntity~
        +save(PostEntity) PostEntity
        +deleteById(id) void
        +existsByContent(content) boolean
        +findByAuthorId(authorId) List~PostEntity~
    }

    class PostEntity {
        -Integer id
        -UserEntity author
        -LocalDate date
        -String durationRead
        -String title
        -String subTitle
        -String postImg
        -String content
        -Set~UserEntity~ collaboratingUsers
        +getId() Integer
        +setAuthor(UserEntity) void
        +getTitle() String
        +setContent(String) void
    }

    class PostDTO {
        <<record>>
        +Integer id
        +Integer authorId
        +String date
        +String durationRead
        +String title
        +String subTitle
        +String postImg
        +String content
    }

    class PostSummaryDTO {
        <<record>>
        +Integer id
        +String title
        +String subTitle
        +String date
        +String durationRead
        +String postImg
        +String authorName
    }

    PostsController --> PostServiceImp : uses
    PostServiceImp --> PostRepository : uses
    PostRepository --> PostEntity : manages
    PostsController ..> PostDTO : uses
    PostsController ..> PostSummaryDTO : returns
    PostServiceImp ..> PostEntity : creates/updates
```

### Authentication and Security Flow

```mermaid
classDiagram
    class AuthController {
        -AuthenticationManager authenticationManager
        -UserServiceImp userService
        -PasswordEncoder passwordEncoder
        -JwtUserDetailsService userDetailsService
        -TokenManager tokenManager
        +register(RegisterUserDTO) ResponseEntity
        +createToken(JwtRequestModel) ResponseEntity
    }

    class JwtFilter {
        -JwtUserDetailsService userDetailsService
        -TokenManager tokenManager
        +doFilterInternal(request, response, chain) void
        -getTokenFromRequest(request) String
    }

    class TokenManager {
        -String jwtSecret
        -long tokenValidity
        +generateJwtToken(UserDetails) String
        +validateJwtToken(token, UserDetails) Boolean
        +getUsernameFromToken(token) String
    }

    class JwtUserDetailsService {
        -UserRepository userRepository
        +loadUserByUsername(email) UserDetails
    }

    class UserEntity {
        <<implements UserDetails>>
        -Integer id
        -String firstName
        -String lastName
        -String email
        -String password
        -String userImg
        -Set~PostEntity~ posts
        -Set~PostEntity~ collaboratedPosts
        +getAuthorities() Collection~GrantedAuthority~
        +getUsername() String
        +isEnabled() boolean
    }

    class WebSecurityConfig {
        +securityFilterChain(HttpSecurity) SecurityFilterChain
        +authenticationManager(AuthenticationConfiguration) AuthenticationManager
        +passwordEncoder() PasswordEncoder
    }

    AuthController --> TokenManager : uses
    AuthController --> JwtUserDetailsService : uses
    JwtFilter --> TokenManager : validates
    JwtFilter --> JwtUserDetailsService : loads user
    JwtUserDetailsService --> UserEntity : returns
    TokenManager ..> UserEntity : validates
    WebSecurityConfig --> JwtFilter : configures
```

### Email Notification Flow

```mermaid
classDiagram
    class NotificationController {
        -EmailService emailService
        +notifySubscribers(title, url) ResponseEntity
        +subscribe(email) ResponseEntity
        +unsubscribe(email) ResponseEntity
        +getSubscribers() ResponseEntity
    }

    class EmailServiceImp {
        -JavaMailSender mailSender
        -SubscribersRepository subscribersRepository
        -String fromEmail
        -boolean mockEmailEnabled
        +sendUpdateToSubscribers(title, url) void
        +subscribe(email) SubscribersEntity
        +unsubscribe(email) boolean
        -sendEmailToSubscriber(email, title, url) void
    }

    class SubscribersRepository {
        <<interface>>
        +findAllEmails() List~String~
        +findByEmail(email) Optional~SubscribersEntity~
        +save(SubscribersEntity) SubscribersEntity
        +deleteByEmail(email) void
        +existsByEmail(email) boolean
    }

    class SubscribersEntity {
        -Long id
        -String email
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +prePersist() void
        +preUpdate() void
    }

    class JavaMailSender {
        <<external>>
        +send(SimpleMailMessage) void
    }

    NotificationController --> EmailServiceImp : uses
    EmailServiceImp --> SubscribersRepository : uses
    EmailServiceImp --> JavaMailSender : sends via
    SubscribersRepository --> SubscribersEntity : manages
```

---

## API Endpoints Overview

### Authentication Endpoints

-   `POST /api/auth/register` - Register a new user
-   `POST /api/auth/login` - Authenticate and receive JWT token

### Post Management Endpoints

-   `GET /api/posts/paginated` - Get paginated posts
-   `GET /api/posts/{id}` - Get specific post by ID
-   `POST /api/posts/create-article` - Create new post (Author only)
-   `PUT /api/posts/{id}` - Update existing post (Author only)
-   `DELETE /api/posts/{id}` - Delete post (Author only)

### User Management Endpoints

-   `GET /api/users/{id}` - Get user profile by ID

### Notification Endpoints

-   `POST /api/notifications/subscribe` - Subscribe to email notifications
-   `POST /api/notifications/unsubscribe` - Unsubscribe from notifications
-   `POST /api/notifications/notify-new-post` - Notify subscribers (Author only)
-   `GET /api/notifications/subscribers` - Get all subscribers (Author only)

---

## Data Model

```mermaid
erDiagram
    USERS ||--o{ POSTS : "authors"
    USERS ||--o{ USER_POST : "collaborates"
    POSTS ||--o{ USER_POST : "has_collaborators"

    USERS {
        int id PK
        string first_name
        string last_name
        string email UK
        string password
        string user_img
        string linkedin_profile
        string twitter_profile
        string instagram_profile
    }

    POSTS {
        int id PK
        int author_id FK
        date date
        string duration_read
        text title
        text sub_title
        text banner
        text content
    }

    USER_POST {
        int author_id FK
        int post_id FK
    }

    SUBSCRIBERS {
        long id PK
        string email UK
        timestamp created_at
        timestamp updated_at
    }
```

---

## Security Architecture

### JWT Authentication Flow

```mermaid
sequenceDiagram
    actor User
    participant Client
    participant JwtFilter
    participant AuthController
    participant TokenManager
    participant UserService
    participant Database

    User->>Client: Login Request
    Client->>AuthController: POST /api/auth/login
    AuthController->>UserService: Authenticate credentials
    UserService->>Database: Query user
    Database-->>UserService: User data
    UserService-->>AuthController: UserDetails
    AuthController->>TokenManager: Generate JWT
    TokenManager-->>AuthController: JWT Token
    AuthController-->>Client: JWT Token
    Client-->>User: Token received

    Note over User,Database: Subsequent Authenticated Requests

    User->>Client: API Request with JWT
    Client->>JwtFilter: Request + JWT in Header
    JwtFilter->>TokenManager: Validate token
    TokenManager-->>JwtFilter: Token valid
    JwtFilter->>UserService: Load user details
    UserService-->>JwtFilter: UserDetails
    JwtFilter->>JwtFilter: Set authentication context
    JwtFilter->>AuthController: Forward request
    AuthController-->>Client: Response
    Client-->>User: Data
```

---

## Deployment Architecture

```mermaid
C4Deployment
    title Deployment Diagram for Blog Post Application

    Deployment_Node(userDevice, "User Device", "Windows/Mac/Linux/Mobile") {
        Container(browser, "Web Browser", "Chrome, Firefox, Safari", "User interface")
    }

    Deployment_Node(appServer, "Application Server", "Cloud Platform") {
        Container(springBoot, "Spring Boot Application", "Java 17, Port 8080", "REST API")
    }

    Deployment_Node(dbServer, "Database Server", "Supabase (AWS EU Central)") {
        ContainerDb(postgres, "PostgreSQL", "Port 5432", "Data storage")
    }

    Deployment_Node(emailServer, "Email Server", "Gmail") {
        System_Ext(smtp, "SMTP Service", "Port 587", "Email delivery")
    }

    Rel(browser, springBoot, "HTTPS requests", "Port 8080")
    Rel(springBoot, postgres, "JDBC/PostgreSQL", "Port 5432, SSL")
    Rel(springBoot, smtp, "SMTP", "Port 587, TLS")
```

---

## Technology Stack

### Backend Framework

-   **Spring Boot 3.2.3**
-   **Java 17**

### Security

-   **Spring Security**
-   **JWT (JSON Web Tokens)** - io.jsonwebtoken v0.11.2
-   **BCrypt Password Encoder**

### Data Persistence

-   **Spring Data JPA**
-   **Hibernate ORM**
-   **PostgreSQL Driver**

### Email Service

-   **Spring Mail**
-   **JavaMailSender**

### Build Tool

-   **Maven**

### Additional Libraries

-   **Spring Boot DevTools** - Development utilities
-   **Spring Validation** - Input validation
-   **Spring HATEOAS** - REST API enhancement

---

## Key Architectural Patterns

### 1. Layered Architecture

The application follows a clear layered architecture:

-   **Controller Layer**: REST endpoints
-   **Service Layer**: Business logic
-   **Repository Layer**: Data access
-   **Entity Layer**: Domain models

### 2. Dependency Injection

All components use constructor-based dependency injection for better testability and immutability.

### 3. DTO Pattern

Data Transfer Objects separate internal domain models from API contracts.

### 4. Repository Pattern

JPA repositories provide abstraction over data access logic.

### 5. Filter Chain Pattern

JWT authentication uses Spring Security filter chain for request interception.

### 6. Builder Pattern

DTOs use Java records for immutable data transfer.

---

## Security Features

1. **JWT Authentication**: Stateless token-based authentication
2. **Role-Based Authorization**: `@PreAuthorize` annotations for author-only endpoints
3. **Password Encryption**: BCrypt hashing for secure password storage
4. **SSL/TLS**: Database connections use SSL, email uses TLS
5. **CORS Configuration**: Controlled cross-origin resource sharing
6. **Exception Handling**: Global exception handler for security errors

---

## Quality Attributes

### Scalability

-   Stateless architecture with JWT
-   Connection pooling (HikariCP)
-   Pagination support for large datasets

### Security

-   JWT-based authentication
-   Password encryption
-   SSL/TLS for all external connections
-   Input validation

### Maintainability

-   Clear separation of concerns
-   Consistent naming conventions
-   Comprehensive logging
-   Exception handling strategy

### Performance

-   Connection pooling (max 5 connections)
-   Lazy loading for entity relationships
-   Indexed database columns (email)
-   Efficient query patterns

---

## Future Considerations

### Potential Enhancements

1. **Caching Layer**: Redis for post caching
2. **API Gateway**: For microservices migration
3. **Message Queue**: For asynchronous email processing
4. **CDN Integration**: For image storage and delivery
5. **Monitoring**: Application performance monitoring (APM)
6. **API Documentation**: OpenAPI/Swagger integration

---

## Glossary

-   **C4 Model**: A hierarchical set of software architecture diagrams for different levels of detail
-   **JWT**: JSON Web Token, a compact token format for authentication
-   **DTO**: Data Transfer Object, objects used to transfer data between layers
-   **JPA**: Java Persistence API, specification for object-relational mapping
-   **SMTP**: Simple Mail Transfer Protocol, for sending emails
-   **SSL/TLS**: Secure Sockets Layer/Transport Layer Security, encryption protocols

---

_This documentation was generated on November 19, 2025_
_Application Version: 0.0.1-SNAPSHOT_
