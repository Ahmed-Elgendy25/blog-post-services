# HATEOAS Implementation for Blog Post Comments and Replies

## Overview

This implementation provides a HATEOAS-compliant REST API for handling blog posts, comments, and replies with proper hypermedia controls.

## API Structure

### 1. Posts with HATEOAS Links

**Endpoint:** `GET /api/posts/{id}/hateoas`

**Example Response:**

```json
{
    "id": 10,
    "content": "<h1>GET BUSY BUSY BUSY</h1>{{image:img0}}",
    "authorId": 1,
    "durationRead": "25 MIN",
    "postImg": "https://wyhuqismvawczoawlkhd.supabase.co/storage/v1/object/public/posts//upload/YEAAAAAAAAT/banner/YEAT.webp",
    "title": "YEAAAAAAAAT",
    "subTitle": "GET BUSY",
    "date": "2025-08-04",
    "likes": 0,
    "_links": {
        "self": { "href": "/api/posts/10/hateoas" },
        "comments": { "href": "/api/comments/post/10?topLevelOnly=true" },
        "author": { "href": "/api/users/1" }
    }
}
```

### 2. Comments with HATEOAS Links

**Endpoint:** `GET /api/comments/{id}`

**Example Response:**

```json
{
    "id": 77,
    "content": "This is a fire post! 🔥",
    "authorId": 2,
    "date": "2025-09-30",
    "likes": 3,
    "_links": {
        "self": { "href": "/api/comments/77" },
        "post": { "href": "/api/posts/10/hateoas" },
        "author": { "href": "/api/users/2" },
        "replies": { "href": "/api/comments/77/replies" }
    }
}
```

### 3. Replies with HATEOAS Links

**Endpoint:** `GET /api/replies/{id}`

**Example Response:**

```json
{
    "id": 101,
    "content": "Haha true 😂",
    "authorId": 3,
    "date": "2025-09-30",
    "likes": 1,
    "parentReplyId": 77,
    "_links": {
        "self": { "href": "/api/replies/101" },
        "comment": { "href": "/api/comments/77" },
        "author": { "href": "/api/users/3" },
        "childReplies": { "href": "/api/replies/101/replies" }
    }
}
```

## Key Features

### 1. Hierarchical Structure

-   **Posts** → contain top-level comments
-   **Comments** → can have replies (nested comments)
-   **Replies** → can have child replies (deeper nesting)

### 2. HATEOAS Navigation

-   Each resource includes links to related resources
-   No need to embed full objects - use links instead
-   Clients can discover available actions through links

### 3. Separate Controllers

-   **PostsController**: Handles blog posts
-   **CommentController**: Handles top-level comments
-   **ReplyController**: Handles replies as separate resources

### 4. DTOs for Different Purposes

-   **PostHateoasDTO**: For HATEOAS post responses
-   **CommentResponseDTO**: For comment responses
-   **ReplyResponseDTO**: For reply responses with parent reference

## API Endpoints

### Posts

-   `GET /api/posts/{id}/hateoas` - Get post with HATEOAS links
-   `POST /api/posts/create-article` - Create new post
-   `POST /api/posts/{id}/like` - Like a post

### Comments

-   `GET /api/comments/{id}` - Get specific comment
-   `POST /api/comments` - Create new comment
-   `GET /api/comments/post/{postId}` - Get comments for a post
-   `GET /api/comments/{parentId}/replies` - Get replies to a comment
-   `POST /api/comments/{id}/like` - Like a comment
-   `PUT /api/comments/{id}` - Update comment
-   `DELETE /api/comments/{id}` - Delete comment

### Replies

-   `GET /api/replies/{id}` - Get specific reply
-   `POST /api/replies` - Create new reply
-   `GET /api/replies/{parentId}/replies` - Get child replies
-   `POST /api/replies/{id}/like` - Like a reply
-   `DELETE /api/replies/{id}` - Delete reply

### Users

-   `GET /api/users/{id}` - Get user information

## Security

-   JWT-based authentication
-   Role-based access control (author, user)
-   Protected endpoints require authentication

## Benefits of This Implementation

1. **Discoverability**: Clients can navigate the API through links
2. **Loose Coupling**: Resources are linked but not embedded
3. **Scalability**: Efficient pagination and lazy loading
4. **RESTful**: Follows REST principles with proper HTTP methods
5. **Hierarchical**: Supports nested comments and replies naturally

## Usage Example

1. **Get a post**: `GET /api/posts/10/hateoas`
2. **Follow comments link**: Use the `comments` link from the post response
3. **Get replies**: Use the `replies` link from any comment
4. **Like content**: Use the `like` links for posts, comments, or replies
5. **Navigate to author**: Use the `author` links to get user information

This implementation provides a clean, navigable, and RESTful API that follows HATEOAS principles for better client-server interaction.
