#!/bin/bash

# Script para testar a aplicação Docker localmente
# Uso: ./test-docker.sh [build|run|restart|logs|stop]

set -e  # Exit on error

IMAGE_NAME="forum-one-api"
CONTAINER_NAME="forum-one-container"
ENV_FILE="env.properties"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
build() {
    echo -e "${BLUE}🏗️  Building Docker image...${NC}"
    docker build -t $IMAGE_NAME .
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Build successful!${NC}"
        echo ""
        docker images | grep $IMAGE_NAME
    else
        echo -e "${RED}❌ Build failed!${NC}"
        exit 1
    fi
}

run() {
    # Check if env file exists
    if [ ! -f "$ENV_FILE" ]; then
        echo -e "${RED}❌ File $ENV_FILE not found!${NC}"
        echo "Create it with your environment variables."
        exit 1
    fi
    
    # Stop and remove old container if exists
    if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
        echo -e "${YELLOW}🧹 Removing old container...${NC}"
        docker rm -f $CONTAINER_NAME
    fi
    
    echo -e "${BLUE}🚀 Starting container...${NC}"
    echo -e "${YELLOW}Application will be available at: http://localhost:8080${NC}"
    echo -e "${YELLOW}Health check: http://localhost:8080/actuator/health${NC}"
    echo ""
    echo -e "${YELLOW}Press Ctrl+C to stop${NC}"
    echo ""
    
    docker run -it --rm \
        -p 8080:8080 \
        --env-file $ENV_FILE \
        --name $CONTAINER_NAME \
        $IMAGE_NAME
}

logs() {
    echo -e "${BLUE}📋 Container logs:${NC}"
    docker logs -f $CONTAINER_NAME
}

stop() {
    echo -e "${YELLOW}🛑 Stopping container...${NC}"
    docker stop $CONTAINER_NAME 2>/dev/null || true
    docker rm $CONTAINER_NAME 2>/dev/null || true
    echo -e "${GREEN}✅ Container stopped${NC}"
}

restart() {
    stop
    run
}

show_help() {
    echo "Usage: ./test-docker.sh [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  build       Build Docker image"
    echo "  run         Run container (builds if image doesn't exist)"
    echo "  restart     Stop and start container"
    echo "  logs        Show container logs"
    echo "  stop        Stop and remove container"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./test-docker.sh build     # Build only"
    echo "  ./test-docker.sh run       # Build and run"
    echo "  ./test-docker.sh restart   # Restart container"
}

# Main
case "${1:-run}" in
    build)
        build
        ;;
    run)
        # Build if image doesn't exist
        if [ -z "$(docker images -q $IMAGE_NAME)" ]; then
            build
        fi
        run
        ;;
    restart)
        restart
        ;;
    logs)
        logs
        ;;
    stop)
        stop
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}❌ Unknown command: $1${NC}"
        echo ""
        show_help
        exit 1
        ;;
esac
