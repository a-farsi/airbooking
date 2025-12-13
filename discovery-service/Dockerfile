# Étape 1 : image de base Java
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : installer netcat pour les healthchecks
RUN apk add --no-cache netcat-openbsd

# Étape 3 : créer un répertoire de travail
WORKDIR /app

# Étape 4 : copier le JAR
COPY target/discovery-service.jar app.jar

# Étape 5 : exposer le port
EXPOSE 8761

# Étape 6 : point d'entrée
ENTRYPOINT ["java", "-jar", "app.jar"]



