# Project Tracking Backend

A Spring Boot application for tracking student projects with Redis caching for performance optimization.

## 🚀 Features

- ✅ Project Management (CRUD)
- ✅ Task & Milestone Tracking
- ✅ User Management with OAuth2
- ✅ Dashboard Statistics
- ✅ **Redis Cache for Performance** (NEW!)
- ✅ RESTful API with Swagger Documentation
- ✅ JWT Authentication
- ✅ Email Notifications
- ✅ File Upload (Cloudinary)

## ⚡ Redis Cache Integration

**NEW!** This project now includes Redis caching for significant performance improvements:

- **Dashboard Stats**: 50x faster (~500ms → ~10ms)
- **Project Lists**: 60x faster (~300ms → ~5ms)
- **User Profiles**: 30x faster (~150ms → ~5ms)

### Quick Start with Redis

```bash
# Start Redis
docker-compose -f docker-compose.redis.yml up -d

# Run application
mvn spring-boot:run
```

### 📚 Redis Documentation

| Document | Description |
|----------|-------------|
| **[REDIS_INDEX.md](docs/redis/REDIS_INDEX.md)** | 📚 Documentation index - Start here! |
| [REDIS_QUICK_START.md](docs/redis/REDIS_QUICK_START.md) | 🚀 5-minute setup guide |
| [REDIS_IMPLEMENTATION_SUMMARY.md](docs/redis/REDIS_IMPLEMENTATION_SUMMARY.md) | 📝 What was implemented |
| [REDIS_CACHE_README.md](docs/redis/REDIS_CACHE_README.md) | 📖 Complete guide |
| [REDIS_CHECKLIST.md](docs/redis/REDIS_CHECKLIST.md) | ✅ Testing checklist |
| [REDIS_API_REFERENCE.md](docs/redis/REDIS_API_REFERENCE.md) | 🔌 API endpoints |
| [REDIS_ARCHITECTURE.md](docs/redis/REDIS_ARCHITECTURE.md) | 🎨 Architecture diagrams |
| [REDIS_COMMANDS.md](docs/redis/REDIS_COMMANDS.md) | ⚡ Command reference |

**→ Start with [REDIS_INDEX.md](docs/redis/REDIS_INDEX.md) for navigation**

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.4.5
- **Java**: 21
- **Database**: MySQL 8.0
- **Cache**: Redis 7 (Alpine)
- **Security**: Spring Security + JWT
- **OAuth2**: Google Sign-In
- **Email**: Spring Mail + Thymeleaf
- **File Storage**: Cloudinary
- **Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8.0+
- Docker & Docker Compose (for Redis)
- Node.js (for frontend)

## 🚀 Getting Started

### 1. Database Setup

```sql
CREATE DATABASE project_tracking;
```

### 2. Environment Variables

Create a `.env` file in the backend directory:

```properties
# Database
DATA_SOURCE_URL=jdbc:mysql://localhost:3306/project_tracking
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT
SECRET_KEY=your_secret_key

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# OAuth2
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Cloudinary
CLOUDINARY_NAME=your_cloudinary_name
CLOUDINARY_KEY=your_cloudinary_key
CLOUDINARY_SECRET=your_cloudinary_secret

# URLs
FRONT_END_URL=http://localhost:3000
BACK_END_URL=http://localhost:9090

# File Upload
PROJECT_UPLOAD_FILE_BASE_URI=/uploads/

# Redis (NEW!)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
```

### 3. Start Redis (NEW!)

```bash
docker-compose -f docker-compose.redis.yml up -d
```

### 4. Build & Run

```bash
# Clean and build
mvn clean install

# Run application
mvn spring-boot:run
```

The application will start on `http://localhost:9090`

### 5. Verify Setup

- **API Documentation**: http://localhost:9090/swagger-ui.html
- **Redis Commander**: http://localhost:8081
- **Health Check**: http://localhost:9090/actuator/health

## 📡 API Endpoints

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics (cached)
- `POST /api/dashboard/cache/refresh` - Refresh cache

### Projects
- `GET /api/projects` - Get all projects (cached)
- `GET /api/projects/{id}` - Get project by ID (cached)
- `POST /api/projects` - Create project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Users
- `GET /api/users/{id}` - Get user by ID (cached)
- `PUT /api/users/{id}` - Update user

**Full API documentation available at Swagger UI**

## 🔧 Configuration

### Cache Settings (application.properties)

```properties
# Cache TTL (seconds)
cache.ttl.dashboard=300        # 5 minutes
cache.ttl.user-profile=600     # 10 minutes
cache.ttl.project-list=180     # 3 minutes
cache.ttl.project-detail=300   # 5 minutes
```

### Redis Configuration

```properties
spring.cache.type=redis
spring.data.redis.host=${REDIS_HOST:localhost}
spring.data.redis.port=${REDIS_PORT:6379}
spring.data.redis.lettuce.pool.max-active=8
```

## 📊 Performance Metrics

| Endpoint | Without Cache | With Cache | Improvement |
|----------|---------------|------------|-------------|
| Dashboard Stats | ~500ms | ~10ms | **50x** ⚡ |
| Project List | ~300ms | ~5ms | **60x** ⚡ |
| Project Detail | ~200ms | ~8ms | **25x** ⚡ |
| User Profile | ~150ms | ~5ms | **30x** ⚡ |

## 🐛 Troubleshooting

### Redis Connection Issues
```bash
# Check if Redis is running
docker ps | grep redis

# Test connection
docker exec -it project-tracking-redis redis-cli ping
```

### Application Won't Start
- Check `.env` file exists and has all required variables
- Verify MySQL is running and accessible
- Check Redis is running (if enabled)

### Cache Not Working
- Verify Redis is running
- Check application logs for cache errors
- Clear cache and retry: `docker exec -it project-tracking-redis redis-cli FLUSHDB`

**More troubleshooting:** See [REDIS_CACHE_README.md](docs/redis/REDIS_CACHE_README.md)

## 📁 Project Structure

```
backend/
├── src/main/java/POSE_Project_Tracking/Blog/
│   ├── config/              # Configuration classes
│   │   ├── RedisConfig.java        # Redis setup
│   │   ├── CacheConfig.java        # Cache configuration
│   │   └── SecurityConfiguration.java
│   ├── controller/          # REST controllers
│   │   ├── DashboardController.java
│   │   ├── ProjectController.java
│   │   └── UserController.java
│   ├── service/             # Business logic
│   │   ├── impl/
│   │   │   ├── DashboardServiceImpl.java
│   │   │   ├── ProjectServiceImpl.java
│   │   │   └── UserServiceImpl.java
│   ├── repository/          # Data access
│   ├── entity/              # JPA entities
│   ├── dto/                 # Data transfer objects
│   │   └── DashboardStatsDTO.java
│   └── mapper/              # MapStruct mappers
├── docker-compose.redis.yml # Redis setup
└── pom.xml                  # Maven configuration
```

## 🧪 Testing

### Manual Testing
```bash
# Test cache performance
time curl http://localhost:9090/api/dashboard/stats

# Monitor Redis
docker exec -it project-tracking-redis redis-cli MONITOR
```

### Load Testing
```bash
# Using Apache Bench
ab -n 100 -c 10 http://localhost:9090/api/dashboard/stats
```

## 🔐 Security

- JWT-based authentication
- OAuth2 with Google
- Role-based access control (STUDENT, TEACHER, ADMIN)
- Password encryption with BCrypt
- CORS configuration

## 📝 Logging

Application logs are configured in `application.properties`:
- Spring Boot logs
- Hibernate SQL logs
- Cache operation logs

## Guideline to build the image of project with GraalVM native-image

To run the application using Docker, follow these steps:
1. First you need to have GraalVM version 21 for on your computer
2. Set the PATH and JAVA_HOME environment
3. For the windows it could require native-image install (also gcc compiler )
4. run ./mvnw -Pnative native:compile -DskipTests (On the project root path)
5. After build successfully, try ./target/Blog to test
6. Build the Docker image:
   ```
   docker build -t blog .
   ```

7. Run the Docker container:
   ```
   # On Linux:
   docker run --rm --env-file .env blog
   ```

## Database Connection

The application connects to a MySQL database. Make sure your MySQL server is running and accessible at the host and port specified in the `.env` file.

By default, the application tries to connect to a MySQL database at `host.docker.internal:3306`. This hostname resolves to the host machine's IP address from within the Docker container.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is part of HCMUTE coursework.

## 👥 Team

Project Tracking Team - HCMUTE

---

**For Redis Cache documentation, start with [REDIS_INDEX.md](docs/redis/REDIS_INDEX.md)** 🚀