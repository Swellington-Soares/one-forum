#!/bin/bash

# Script para testar a aplicação Docker localmente
# Uso: ./test-docker.sh

echo "🏗️  Building Docker image..."
docker build -t forum-app-local .

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "🚀 Starting container..."
    echo "Press Ctrl+C to stop"
    echo ""
    
    docker run -it --rm \
        -p 8080:8080 \
        --env-file .env \
        --name forum-app-container \
        forum-app-local
else
    echo "❌ Build failed!"
    exit 1
fi
