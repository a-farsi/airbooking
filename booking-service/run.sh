#!/bin/bash

set -e  # Stopper le script si une commande échoue

# 1. Compile le projet et génère le .jar
echo " >> Building the project..."
mvn clean package -DskipTests

# 2. Vérifier que le JAR est bien là
JAR_PATH="./target/booking-service.jar"
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR file not found: $JAR_PATH"
  exit 1
fi
echo ">> working directory ..."
pwd

# 2. Build l'image Docker du service
echo " >> Building Docker image..."
docker build -t booking-service .

# 3. Lancer les services avec Docker Compose
echo " >> Starting containers..."
docker-compose up -d --build
