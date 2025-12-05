# Étape 1 : image de base Java
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : créer un répertoire de travail
WORKDIR /app

RUN echo "Current dir inside image:" && pwd

# Étape 3 : copier le JAR
COPY target/booking-service.jar app.jar

# Étape 4 : exposer le port (facultatif)
EXPOSE 8080

# Étape 5 : point d'entrée
ENTRYPOINT ["java", "-jar", "app.jar"]
