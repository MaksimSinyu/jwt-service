# JWT Service

JWT Service is a microservice for managing user authentication and authorization using JSON Web Tokens (JWT).

## Features

- User registration
- User authentication
- JWT token generation and validation
- Password change with history tracking
- Secure password storage
- Token invalidation upon password change
- Integration with H2 database

## Technologies

- **Java 17**
- **Spring Boot 2.7.5**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database**
- **Docker**
- **Maven**

## How the System Works

### User Registration:

- Users register by providing a unique username and password.
- Passwords are hashed using BCrypt before storage.
- A unique randomHash is generated for each user to enhance token security.
- User data, including password history, is stored in the H2 database.

### User Authentication:

- Users log in by submitting their username and password.
- The service verifies credentials and, upon success, generates a JWT token.
- The token is signed using a key derived from a service-wide key, the user's randomHash, and password vectors.

### JWT Token Management:

- Tokens include the username and have an expiration time.
- Validation checks the token's signature and expiration.
- Changing a user's password regenerates the randomHash, invalidating existing tokens.

### Password Management:

- Users can change their passwords via an endpoint.
- The service maintains a history of the last five passwords to prevent reuse.
- Password changes update the randomHash, ensuring old tokens are no longer valid.

## Getting Started

### Prerequisites

- **JDK 17**
- **Maven**
- **Docker and Docker Compose**

### Installation and Running

1. **Clone the Repository:**

   ```sh
   git clone https://github.com/yourusername/jwt-service.git
   cd jwt-service
   ```

2. **Build and Run:**

   Execute the provided script to build the project and start the Docker container:

   ```sh
   ./run.sh
   ```

3. **Access the Service:**

   The service will be available at: [http://localhost:8080](http://localhost:8080)

## API Endpoints

### Register a New User

- **Endpoint:** `/api/auth/register`
- **Method:** `POST`
- **Body:**
  ```json
  {
    "username": "newuser",
    "password": "password123"
  }
  ```
- **Responses:**
  - `201 Created` on success
  - `400 Bad Request` if username exists

### Authenticate a User

- **Endpoint:** `/api/auth/login`
- **Method:** `POST`
- **Body:**
  ```json
  {
    "username": "existinguser",
    "password": "password123"
  }
  ```
- **Responses:**
  - `200 OK` with JWT token on success
  - `401 Unauthorized` on failure

### Change User Password

- **Endpoint:** `/api/auth/change-password`
- **Method:** `POST`
- **Parameters:**
  - `username`: User's username
  - `newPassword`: New password
- **Responses:**
  - `200 OK` on success
  - `404 Not Found` if user doesn't exist

## Testing

Run tests using Maven:

```sh
mvn test
```