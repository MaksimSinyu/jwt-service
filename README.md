# JWT Service

JWT Service is a microservice for managing user authentication and authorization using JWT (JSON Web Tokens).

## Features

- User registration
- User authentication
- Generation and validation of JWT tokens
- User password change
- Secure password storage using advanced hashing methods
- Integration with H2 database for user data storage

## Technologies

- **Java 17**
- **Spring Boot 2.7.5**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database**
- **Docker**
- **Maven**

## Getting Started

### Prerequisites

- **JDK 17**
- **Maven**
- **Docker and Docker Compose**

### Installation and Running

1. **Clone the repository:**

   ```sh
   git clone https://github.com/yourusername/jwt-service.git
   cd jwt-service
   ```

2. **Build the project and run the Docker container:**

   ```sh
   ./run.sh
   ```

   This script will:
  - Build the project using Maven
  - Create a Docker image
  - Start the container using Docker Compose

3. The service will be available at: [http://localhost:8080](http://localhost:8080)

## API Endpoints

### Register a New User

- **URL:** `/api/auth/register`
- **Method:** `POST`
- **Request body:**
  ```json
  {
    "username": "newuser",
    "password": "password123"
  }
  ```
- **Successful response:**
  - **Code:** `200 OK`
  - **Body:**
    ```json
    {
      "message": "User registered successfully."
    }
    ```
- **Error response:**
  - **Code:** `400 Bad Request`
  - **Body:**
    ```json
    {
      "message": "Username already exists."
    }
    ```

### Authenticate a User

- **URL:** `/api/auth/login`
- **Method:** `POST`
- **Request body:**
  ```json
  {
    "username": "existinguser",
    "password": "password123"
  }
  ```
- **Successful response:**
  - **Code:** `200 OK`
  - **Body:**
    ```json
    {
      "message": "Login successful.",
      "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
      }
    }
    ```
- **Error response:**
  - **Code:** `401 Unauthorized`
  - **Body:**
    ```json
    {
      "message": "Invalid credentials."
    }
    ```

### Change User Password

- **URL:** `/api/auth/change-password`
- **Method:** `POST`
- **Request parameters:**
  - `username`: user's username
  - `newPassword`: new password
- **Successful response:**
  - **Code:** `200 OK`
  - **Body:**
    ```json
    {
      "message": "Password updated successfully."
    }
    ```
- **Error response:**
  - **Code:** `400 Bad Request`
  - **Body:**
    ```json
    {
      "message": "User not found."
    }
    ```

## Configuration

Main application settings are in the `application.properties` file. Use `application-prod.properties` for production environment.

## Testing

To run tests, execute the command:

```sh
mvn test
```

## Security

- Passwords are hashed before storing in the database
- Password history mechanism is used to prevent reuse of old passwords
- Advanced mathematical methods are applied for password generation and verification

## Development

The project uses a standard Maven structure. Main components:

- `JwtServiceApplication.java` - application entry point
- `AuthController.java` - controller for handling authentication requests
- `UserService.java` - service for user operations
- `PasswordService.java` - service for password operations
- `JwtUtil.java` - utility for working with JWT tokens

## License

This project is licensed under the MIT License - see the `LICENSE.md` file for details.