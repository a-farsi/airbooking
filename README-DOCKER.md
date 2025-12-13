# Guide Docker pour AirBooking

Ce guide explique comment lancer tous les microservices dans des conteneurs Docker.

## Prérequis

- Docker et Docker Compose installés
- Maven installé (pour construire les JARs)
- Java 17 installé (pour la compilation)

## Architecture des Services

Les services suivants seront lancés :

1. **discovery-service** (port 8761) - Eureka Server
2. **config-service** (port 8888) - Spring Cloud Config Server
3. **booking-db** (port 5432) - PostgreSQL pour booking-service
4. **booking-service** (port 8081) - Service de réservation
5. **gateway-service** (port 8080) - API Gateway

## Ordre de Démarrage

Les services démarrent dans cet ordre automatiquement grâce aux dépendances :

1. `discovery-service` (Eureka) - Doit démarrer en premier
2. `config-service` - Attend que discovery-service soit prêt
3. `booking-db` - Base de données PostgreSQL
4. `booking-service` - Attend discovery, config et la base de données
5. `gateway-service` - Attend discovery et booking-service

## Étapes pour Lancer les Services

### Étape 1 : Construire les JARs

Avant de créer les images Docker, vous devez construire les JARs de chaque service :

```bash
# Option 1 : Utiliser le script automatique
./build-all.sh

# Option 2 : Construire manuellement chaque service
cd discovery-service && mvn clean package -DskipTests && cd ..
cd config-service && mvn clean package -DskipTests && cd ..
cd booking-service && mvn clean package -DskipTests && cd ..
cd gateway-service && mvn clean package -DskipTests && cd ..
```

### Étape 2 : Lancer avec Docker Compose

```bash
# Depuis la racine du projet
docker-compose up -d
```

Pour voir les logs en temps réel :
```bash
docker-compose up
```

### Étape 3 : Vérifier que tous les services sont démarrés

```bash
# Vérifier le statut des conteneurs
docker-compose ps

# Vérifier les logs d'un service spécifique
docker-compose logs -f discovery-service
docker-compose logs -f config-service
docker-compose logs -f booking-service
docker-compose logs -f gateway-service
```

## Accès aux Services

Une fois tous les services démarrés, vous pouvez accéder à :

- **Eureka Dashboard** : http://localhost:8761
- **Config Server** : http://localhost:8888
- **Booking Service API** : http://localhost:8081/api/bookings
- **Gateway API** : http://localhost:8080/api/bookings (routage via gateway)

## Commandes Utiles

### Arrêter tous les services
```bash
docker-compose down
```

### Arrêter et supprimer les volumes (⚠️ supprime les données)
```bash
docker-compose down -v
```

### Reconstruire les images
```bash
docker-compose build --no-cache
```

### Redémarrer un service spécifique
```bash
docker-compose restart booking-service
```

### Voir les logs d'un service
```bash
docker-compose logs -f booking-service
```

### Exécuter une commande dans un conteneur
```bash
docker-compose exec booking-service sh
```

## Dépannage

### Vérifier que les services sont en bonne santé

```bash
# Vérifier la santé de discovery-service
curl http://localhost:8761/actuator/health

# Vérifier la santé de config-service
curl http://localhost:8888/actuator/health

# Vérifier la santé de booking-service
curl http://localhost:8081/actuator/health

# Vérifier la santé de gateway-service
curl http://localhost:8080/actuator/health
```

### Problèmes courants

1. **Port déjà utilisé** : Vérifiez qu'aucun autre service n'utilise les ports 8761, 8888, 8080, 8081, 5432
2. **Service ne démarre pas** : Vérifiez les logs avec `docker-compose logs <service-name>`
3. **Erreur de connexion à la base de données** : Vérifiez que booking-db est démarré et en bonne santé
4. **Service ne s'enregistre pas dans Eureka** : Vérifiez que discovery-service est démarré en premier

## Structure des Fichiers Docker

Chaque service a son propre Dockerfile :
- `discovery-service/Dockerfile`
- `config-service/Dockerfile`
- `booking-service/Dockerfile`
- `gateway-service/Dockerfile`

Le fichier `docker-compose.yml` à la racine orchestre tous les services.

## Réseau Docker

Tous les services communiquent via le réseau `airbooking-network`. Les services peuvent se référencer par leur nom de conteneur :
- `discovery-service:8761`
- `config-service:8888`
- `booking-db:5432`
- `booking-service:8081`
- `gateway-service:8080`

