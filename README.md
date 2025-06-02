# Spring Boot-Redis Integration API

## 📖 Information

**Redis Integration API** is a comprehensive Spring Boot application that demonstrates advanced Redis integration patterns including ***caching***, ***rate limiting***, ***JWT token management***, and ***OTP*** verification. The project showcases real-world Redis usage scenarios with security implementations.

### Key Features:
- **JWT Authentication**: Redis-backed JWT token management with blacklisting capability
- **Rate Limiting**: IP-based request throttling with automatic blacklisting
- **OTP Verification**: Redis-cached OTP system with email notifications
- **Caching**: Intelligent caching strategies for user data operations
- **Security**: Comprehensive security configuration with role-based access
- **CORS Support**: Cross-origin resource sharing for frontend integration

### Authentication & Security:
- Users must register before accessing protected endpoints
- JWT tokens are stored and validated through Redis
- Token invalidation (logout) removes tokens from Redis cache
- Rate limiting prevents abuse with automatic IP blacklisting
- Password encryption using BCrypt

### Caching Strategy:
- User data cached with configurable TTL (10 minutes default)
- Cache eviction on data modifications
- Intelligent cache key generation
- Support for multiple cache configurations

## 🚀 Endpoints Summary

| Method | URL | Description | Request Body | Path Variable | Response |
|--------|-----|-------------|--------------|---------------|----------|
| **Authentication** |  |  |  |  |  |
| POST | `/auth/addNewUser` | Register new user for auth purpose | User | - | User |
| POST | `/auth/users` | Create user (alternative) | User | - | User |
| POST | `/auth/generateToken` | Login and generate JWT token | User | - | String (JWT Token) |
| GET | `/auth/test` | Test authenticated endpoint | - | - | String |
| **JWT Management** |  |  |  |  |  |
| GET | `/login` | Login page (Thymeleaf) | - | - | HTML Page |
| POST | `/auth/login` | Web login with cookie | Form Data | - | Redirect |
| POST | `/auth/logout` | Logout and invalidate token | Cookie | - | Redirect |
| GET | `/success` | Success page after login | - | - | HTML Page |
| **Caching** |  |  |  |  |  |
| POST | `/api` | Add new user | User | - | User |
| GET | `/api/users` | Get all users (cached) | - | - | List\<User\> |
| GET | `/api/{id}` | Get user by ID (cached) | - | id (Long) | User |
| PUT | `/api/{id}` | Update user by ID | User | id (Long) | User |
| DELETE | `/api/{id}` | Delete user by ID | - | id (Long) | Void |
| **OTP Service** |  |  |  |  |  |
| GET | `/api/otp/` | Getting OTP page (React) | -  | - | String |
| POST | `/api/otp/send` | Send OTP to email | phone (param) | - | String |
| POST | `/api/otp/verify` | Verify OTP code | {phone, otp} | - | String |
| **Rate Limiting** |  |  |  |  |  |
| GET | `/rate/test` | Test rate limiting (10 req/10min) | - | - | String |

## 🛠️ Technologies

- **Java 17** - Programming Language
- **Spring Boot 3.4.4** - Application Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data Access Layer
- **Spring Data Redis** - Redis Integration
- **PostgreSQL** - Primary Database
- **Redis** - Caching & Session Storage
- **JWT (JJWT 0.12.6)** - Token Authentication
- **Lombok** - Code Generation
- **Thymeleaf** - Template Engine
- **Spring Mail** - Email Service
- **Maven** - Dependency Management
- **OpenAPI 3 (Swagger)** - API Documentation

## ⚙️ Prerequisites

### Environment Variables (.env file)

```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/redis_db
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET_KEY=your_base64_encoded_secret_key

# Mail Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

### Application Properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis Configuration
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.cache.type=redis

# JWT Configuration
jwt.key=${JWT_SECRET_KEY}

# Mail Configuration
spring.mail.host=${SPRING_MAIL_HOST}
spring.mail.port=${SPRING_MAIL_PORT}
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 🚀 Running the Application

### Maven Run
```bash
cd redis
mvn clean install
mvn spring-boot:run
```

### Docker Run
```bash
cd redis
docker-compose up -d
```

### Docker Compose File Example
```yaml
version: '3'
services:
  redis:
    image: "bitnami/redis:latest"
    platform: linux/amd64
    ports:
      - "6379:6379"
    volumes:
      - ./data:/data
    environment:
      - ALLOW_EMPTY_PASSWORD=yes

  db:
    image: debezium/example-postgres
    platform: linux/amd64
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 12345
    ports:
      - 5432:5432
    extra_hosts:
      - "host.docker.internal:host-gateway"
    command:
      - "postgres"
      - "-c"
      - "wal_level=logical"

  adminer:
    image: adminer
    platform: linux/amd64
    restart: always
    ports:
      - 8001:8080  # Provides external access to Adminer.
```

## 📖 API Usage Examples

### Authentication Flow

1. **Register User**
```bash
curl -X POST http://localhost:8080/auth/addNewUser \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

2. **Generate Token**
```bash
curl -X POST http://localhost:8080/auth/generateToken \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

3. **Access Protected Endpoint**
```bash
curl -X GET http://localhost:8080/auth/test \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### OTP Verification

1. **Send OTP**
```bash
curl -X POST "http://localhost:8080/api/otp/send?phone=1234567890"
```

2. **Verify OTP**
```bash
curl -X POST http://localhost:8080/api/otp/verify \
  -H "Content-Type: application/json" \
  -d '{"phone": "1234567890", "otp": "123456"}'
```

### User Management with Caching

1. **Create User**
```bash
curl -X POST http://localhost:8080/api \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "password123"}'
```

2. **Get User (First call - DB hit)**
```bash
curl -X GET http://localhost:8080/api/1
```

3. **Get User Again (Second call - Cache hit)**
```bash
curl -X GET http://localhost:8080/api/1
```

## 🔧 Redis Configuration Details

### Multiple Redis Templates
- **General Template**: JSON serialization for complex objects
- **Blacklist Template**: String-based for IP blacklisting
- **Rate Limit Template**: Integer-based for counters

### Cache Configuration
- **Default TTL**: 10 minutes
- **Cache Names**: `users`, `user_id`

### Rate Limiting Logic
- **Limit**: 10 requests per 10 minutes per IP
- **Blacklist Duration**: 10 minutes
- **Thread-safe**: Uses Redis INCR for atomic operations

## 📚 OpenAPI Documentation

Access Swagger UI at: `http://localhost:8080/swagger-ui/index.html`

API Documentation JSON: `http://localhost:8080/v3/api-docs`

## 🔒 Security Features

### JWT Token Management
- Tokens stored in Redis with expiration
- Token validation against Redis cache
- Secure token invalidation on logout
- Added Custom Claims {"Muharrem","Aslan"} as a example to token

### Rate Limiting
- IP-based request throttling
- Automatic blacklisting for abuse prevention
- Configurable limits and timeouts
- Thread-safe implementation

### CORS Configuration
- Frontend integration support

## 🧪 Testing the Rate Limiter

```bash
# Test rate limiting (run multiple times quickly)
for i in {1..15}; do
  curl -X GET http://localhost:8080/rate/test
  echo " - Request $i"
done
```

After 10 requests, you'll receive:
```
Rate limit exceeded. Try again later.
```

## 📋 Project Structure

```
src/main/java/com/MuharremAslan/redis/
├── Config/
│   ├── CorsConfig.java
│   ├── RedisConfig.java
│   ├── SwaggerConfig.java
│   └── WebConfig.java
├── Controller/
│   ├── AuthController.java
│   ├── JwtController.java
│   ├── OTPController.java
│   ├── RateLimitController.java
│   └── UserController.java
├── Model/
│   ├── ROLE.java
│   └── User.java
├── Security/
│   ├── SecurityConfiguration.java
│   ├── UserDetailsServiceImpl.java
│   └── jwt/JwtAuthFilter.java
└── Service/
    ├── JwtService.java
    ├── MailService.java
    ├── OTPService.java
    ├── RateLimiterInterceptor.java
    ├── RateLimiterService.java
    ├── UserService.java
    └── UserServiceSecurity.java
```

## 🚨 Important Notes

- Ensure Redis server is running before starting the application
- Configure email settings for OTP functionality



---

<details>
  <summary>FIGUREs</summary>
  ![rate_test2](https://github.com/user-attachments/assets/a64f3094-88a5-4f0f-804b-4e8eb0c2931c)
  *Multiple requests made via phone were rejected*
  ![rate_test](https://github.com/user-attachments/assets/62f696ed-4cdc-4bac-97cf-11d18e01f7dd)
  *Blacklist*
  ![redis_otp](https://github.com/user-attachments/assets/6f82350c-d7e9-4865-b618-77d1154ed84f)
  *OTP*
  ![redis_jwt](https://github.com/user-attachments/assets/a7582d3e-3a17-4a6c-9617-ff39c5e537ba)
  *JWT*
  ![redis_cache](https://github.com/user-attachments/assets/7044759f-ea5e-4efc-97a1-7a12547acdb8)
  *Caching*
</details>

*This project demonstrates professional-grade Redis integration patterns with Spring Boot, focusing on performance, security, and scalability.*
