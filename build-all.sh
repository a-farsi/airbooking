#!/bin/bash

# Script pour construire tous les microservices avant de lancer Docker Compose

echo "🔨 Construction de tous les microservices..."

# Discovery Service
echo "📦 Construction de discovery-service..."
cd discovery-service
mvn clean package -DskipTests
cd ..

# Config Service
echo "📦 Construction de config-service..."
cd config-service
mvn clean package -DskipTests
cd ..

# Booking Service
echo "📦 Construction de booking-service..."
cd booking-service
mvn clean package -DskipTests
cd ..

# Gateway Service
echo "📦 Construction de gateway-service..."
cd gateway-service
mvn clean package -DskipTests
cd ..

echo "✅ Tous les services ont été construits avec succès!"
echo "🚀 Vous pouvez maintenant lancer: docker-compose up -d"

