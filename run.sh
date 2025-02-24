#!/bin/bash
# ===========================================
# Spring Boot Application Runner for TodoAPI
# ===========================================
#
# ==============================================================================
# Usage:
#   ./run.sh [profile]
# Where [profile] can be:
#   - dev-fast:  Local development (fast startup, skips tests and Checkstyle)
#   - dev:       Local development (normal mode with tests and Checkstyle)
#   - prod-local: Local production build (standalone JAR)
#   - prod:      Production Docker build (Docker Compose with environment files)
# ==============================================================================

set -e

PROFILE="${1:-dev}"

ARTIFACT_NAME="todo-0.0.1-SNAPSHOT.jar"
TARGET_DIR="target/dist"

GREEN='\033[0;32m'
NC='\033[0m'

log() {
    echo -e "${GREEN}$1${NC}"
}

case "$PROFILE" in

    "dev-fast")
        log "Starting application in DEV-FAST mode..."
        mvn spring-boot:run -Dmaven.test.skip=true -Dcheckstyle.skip=true -Dspring-boot.run.fork=false
        ;;

    "dev")
        log "Starting application in DEV mode (normal)..."
        mvn clean verify spring-boot:run
        ;;

    "prod-local")
        log "Building application in PROD-LOCAL mode..."
        mvn clean package -DskipTests -Pprod-local
        log "Build successful. Running JAR..."
        java -jar "$TARGET_DIR/$ARTIFACT_NAME" --spring.profiles.active=prod-local
        ;;

    "prod")
        log "Building application in PROD mode (Docker deployment)..."
        mvn clean package -DskipTests -Pprod
        log "Starting Docker containers with environment file (.env.prod)..."
        docker-compose --env-file .env.prod up --build -d
        log "Docker containers are up and running!"
        ;;

    "stop")
        log "Stopping Docker containers..."
        docker-compose down
        log "Docker containers stopped."
        ;;

    "clean")
        log "Cleaning build artifacts..."
        mvn clean
        log "Cleanup complete."
        ;;

    *)
        echo "Unknown profile: $PROFILE"
        echo "Usage: $0 [dev-fast | dev | prod-local | prod | stop | clean]"
        exit 1
        ;;
esac
