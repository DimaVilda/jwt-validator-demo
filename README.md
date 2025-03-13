# JWT Signature Validation Demo

## Project Overview

This demo project illustrates the process of JWT (JSON Web Token) signature validation in a mvc Java application, focusing on security best practices and authentication mechanisms.
The project used in this article https://medium.com/@dimavilda/validating-jwt-signatures-what-happens-under-the-hood-44d91db54c2ahelps as an example that help engineers to understand the technical and mathematical details of JWT validation process in Java

## 🚀 Technologies Used

- **Java 17**
- **Spring Boot 3.1.5**
- **Keycloak 22.0.1** - Open-source identity and access management
- **Docker Compose** - For containerized infrastructure
- **jjwt (Java JWT)** - JWT parsing and validation library


## 🔐 Key Features

- Automated JWT token validation
- Key ID (kid) claim extraction and verification
- MD5 hash-based key identification
- Secure public key validation
- Comprehensive error handling

## 📋 Prerequisites

- Java Development Kit (JDK) 17+
- Maven
- Docker
- Docker Compose

## 🛠️ Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/jwt-validation-demo.git
cd jwt-validation-demo
```

### 2. Start Keycloak and Database

```bash
docker-compose up -d
```

### 3. Configure Keycloak

1. Open Keycloak Admin Console at `http://localhost:8080`
2. Login with credentials:
   - Username: `admin`
   - Password: `admin`
3. Create a new realm  (e.g., `jwt-demo-realm`)
4. Create a new client (e.g., `demo-app`) with the following configurations:
   - Client type: `OpenID Connect`
   - Client authentication: `On`
   - Standard flow: `On`
   - Direct access grants: `On`
5. In the client settings:
   - Set Valid redirect URIs to `http://localhost:3000/*`
   - Generate a client secret
6. Create a new user:
   - Navigate to Users section
   - Click "Add user"
   - Set a username
   - Go to Credentials tab
   - Set a password
   - Disable "Temporary" to allow immediate login

### 4. Build and Run the Application

```bash
# Build the project
mvn clean package
```
```bash
# Run the application
mvn spring-boot:run
```
The application server will start on port 3000

## 🧪 Testing the Endpoint

### Obtaining JWT Access Token

1. Use this api to obtain an access token from Keycloak:

```bash
curl -X POST http://localhost:8080/realms/{YOUR_REALM_NAME}/protocol/openid-connect/token \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "client_id=..." \
     -d "client_secret=..." \
     -d "username=..." \
     -d "password=..." \
     -d "grant_type=password"
```

This will return a JSON response containing an `access_token`.

### Validating the JWT Token

Use the obtained access token to test the validation endpoint:

```bash
curl -X GET http://localhost:3000/demo/data \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## 🔍 Understanding the Validation Process

The application performs several critical steps:

1. Extract token header and claims
2. Verify Key ID (kid) claim
3. Validate token signature using public key
4. Check token integrity and authenticity

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 📬 Contact

https://www.linkedin.com/in/dima-vilda/