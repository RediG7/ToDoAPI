# To-Do List API

This is a To-Do List API built using **Spring Boot 3.x** and **PostgreSQL**. It provides CRUD functionality, supports both local development and production deployment, and uses Basic Authentication for security

## Table of Contents
- [Prerequisites](#prerequisites)
- [Environment Configuration](#environment-configuration)
- [Running the Project](#running-the-project)
  - [Using the Script](#using-the-script)
  - [Manual Commands](#manual-commands)
- [API Endpoints](#api-endpoints)
- [Testing the APIs](#testing-the-apis)

## Prerequisites
- Java 17+
- Maven
- Docker & Docker Compose

## Environment Configuration

You need to create the following environment files in the root directory:

- `.env.dev`
- `.env.prod`
- `.env.prod-local`

Each file should follow this structure:

```plaintext
DB_URL=your_jdbc_url
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

---

## Running the Project

### Using the Script
Use the `run.sh` script to start the application in different profiles:

```bash
./run.sh [profile]
```

Where `[profile]` can be:
- **dev-fast:** Local development (fast startup, skips tests, Checkstyle, and SpotBugs)
- **dev:** Local development (standard mode with tests, Checkstyle, and SpotBugs)
- **prod-local:** Local production build (standalone JAR)
- **prod:** Production Docker build (Docker Compose with environment files)
- **stop:** Stop Docker containers
- **clean:** Clean build artifacts

### Manual Commands

#### Development Mode

##### **Fast Mode** (Skips non-essential phases for faster startup)
```bash
mvn spring-boot:run -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Dspring-boot.run.fork=false
```

##### **Standard Mode** (Runs with all lifecycle phases, including tests)
```bash
mvn clean verify spring-boot:run
```

#### Production Mode

1. **Build the JAR:**
```bash
mvn clean package -DskipTests -Pprod-local
```

2. **Run the JAR:**
```bash
java -jar target/dist/todo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod-local
```

3. **Docker Deployment:**
```bash
mvn clean package -DskipTests -Pprod
```
```bash
docker-compose --env-file .env.prod up --build -d
```

> **Important:** When using the `prod` profile (Docker deployment), ensure you **stop the PostgreSQL service** running locally since Docker maps `5432:5432`. Otherwise, **start it** if using `prod-local`.

---

## API Endpoints

All API endpoints are documented in **Swagger UI**:
- [**Live Swagger UI**](https://testing-deployment-73v6.onrender.com/swagger-ui/index.html)
- [**Local Swagger UI**](http://localhost:9090/swagger-ui/index.html)

> **Note:** Due to Render’s cold-start mechanism, the live deployment might take up to **50 seconds** to start.

Additionally, the OpenAPI specification is available in `src/main/resources/openapi.yml`.

---

## Testing the APIs

### Using Swagger UI
1. Start the application.
2. Open [Swagger UI](http://localhost:9090/swagger-ui/index.html) in your browser.
3. Execute API requests and inspect responses.

### Using Postman
- Import the OpenAPI specification (`src/main/resources/openapi.yml`) into **Postman** for convenient testing and documentation.

---

**Unit Tests:**
- To run unit tests, use:
```bash
mvn test
```

**Static Code Analysis:**
- To run Checkstyle and SpotBugs:
```bash
mvn checkstyle:check spotbugs:check
```

---

