#!/bin/bash

# Script to build Native Image using Docker
# This will take 5-10 minutes on first build

set -e

echo "🚀 Building Native Image with GraalVM..."
echo "⏰ This will take 5-10 minutes. Please be patient..."
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running!${NC}"
    exit 1
fi

echo -e "${YELLOW}📦 Step 1: Building with Docker multi-stage...${NC}"
docker build -f Dockerfile.native -t project-tracker-native:latest .

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ Native Image built successfully!${NC}"
    echo ""
    echo "📊 Image info:"
    docker images project-tracker-native:latest
    echo ""
    echo "🎯 Next steps:"
    echo "  1. Test locally: docker run -p 9090:9090 project-tracker-native:latest"
    echo "  2. Tag for Docker Hub: docker tag project-tracker-native:latest letmehear/project-tracker-backend:native"
    echo "  3. Push: docker push letmehear/project-tracker-backend:native"
else
    echo -e "${RED}❌ Build failed! Check the error messages above.${NC}"
    exit 1
fi
