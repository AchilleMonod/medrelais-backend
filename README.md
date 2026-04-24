# MedRelais — Backend

API REST Spring Boot pour la plateforme **MedRelais**, permettant la mise en relation de praticiens médicaux et paramédicaux pour des remplacements.

---

## 📋 Sommaire

1. [Prérequis](#-prérequis)
2. [Architecture hexagonale](#-architecture-hexagonale)
3. [Modèle de données](#-modèle-de-données)
4. [Configuration de la base de données](#-configuration-de-la-base-de-données)
5. [Lancement avec IntelliJ IDEA](#-lancement-avec-intellij-idea)
6. [Profils Spring](#-profils-spring)
7. [Sécurité JWT](#-sécurité-jwt)
8. [Documentation Swagger](#-documentation-swagger)
9. [Tests](#-tests)
10. [Build Maven](#-build-maven)

---

## ✅ Prérequis

| Outil            | Version minimale |
|------------------|-----------------|
| Java (JDK)       | 21              |
| Maven            | 3.9+            |
| PostgreSQL       | 14+             |
| IntelliJ IDEA    | 2023+           |

---

## 🏛 Architecture hexagonale

Le projet est découpé en **3 modules Maven** respectant les principes de l'architecture hexagonale (Ports & Adapters) :

```
medrelais-backend/
├── domain-layer/           → Cœur métier (pur Java, aucune dépendance framework)
├── infrastructure-layer/   → Adaptateurs techniques (JPA, Sécurité, JWT)
└── application-layer/      → Point d'entrée (Controllers REST, DTOs, Swagger, Spring Boot)
```

### Détail des couches

#### 🟢 `domain-layer` — Domaine métier
> Contient uniquement la logique métier. **Aucune dépendance** vers Spring, JPA ou tout autre framework.

| Package       | Rôle                                                                 |
|---------------|----------------------------------------------------------------------|
| `model/`      | Objets métier (BO) : `UtilisateurBO`, `PraticienBO`, `CreneauBO`... |
| `model/enums` | Enums du domaine : `Role`, `StatutCreneau`, `StatutAttribution`...  |
| `port/`       | Interfaces repository (contrats vers l'infrastructure)              |
| `service/`    | Interfaces des services métier                                      |
| `service/impl/` | Implémentations des services avec toute la logique métier         |
| `exception/`  | Exceptions métier (`PraticienNotFoundException`, etc.)              |

#### 🔵 `infrastructure-layer` — Adaptateurs techniques
> Implémente les ports définis dans le domaine. Contient tout ce qui est technique.

| Package                    | Rôle                                              |
|----------------------------|---------------------------------------------------|
| `database/entities/`       | Entités JPA (SINGLE_TABLE inheritance)            |
| `database/entities/enums/` | Enums JPA miroir des enums domaine                |
| `database/dao/`            | Interfaces Spring Data JPA (`JpaRepository`)      |
| `database/adapter/`        | Implémentations des ports (DAO + mapper)          |
| `database/mapper/`         | Mappers MapStruct Entity ↔ BO                     |
| `security/`                | Filtre JWT, `JwtService`, `UserDetailsServiceImpl`|

#### 🟡 `application-layer` — Point d'entrée
> Orchestre l'exposition REST et la configuration Spring Boot.

| Package              | Rôle                                              |
|----------------------|---------------------------------------------------|
| `webapp/controller/` | Controllers REST (`@RestController`)              |
| `webapp/model/request/`  | DTOs entrants avec validations `@Valid`       |
| `webapp/model/response/` | DTOs sortants                                 |
| `webapp/mapper/`     | Mappers MapStruct DTO ↔ BO                        |
| `webapp/exception/`  | `ApiExceptionHandlerAdvice`, `ApiError`           |
| `webapp/configuration/` | `OpenApiConfig` (Swagger/SpringDoc)            |

---

## 🗄 Modèle de données

Le schéma repose sur **5 tables** dans le schéma PostgreSQL `medrelais` :

```
utilisateur        (SINGLE_TABLE — PRATICIEN, ADMIN)
    │
    ├──< regle_recurrence   (règles de génération de créneaux récurrents)
    │        │
    │        └──< creneau   (créneaux ponctuels ou issus d'une règle)
    │                │
    └──< attribution └──< attribution  (demandes de remplacement)
```

| Table               | Description                                              |
|---------------------|----------------------------------------------------------|
| `utilisateur`       | Héritage SINGLE_TABLE — champs spécifiques `PRATICIEN` nullable pour `ADMIN` |
| `regle_recurrence`  | Règle de génération récurrente (hebdo, mensuel...) avec CSV pour les jours |
| `creneau`           | Slot de remplacement, statut géré automatiquement        |
| `attribution`       | Candidature d'un remplaçant sur un créneau               |

### Cycle de vie du statut créneau

```
DISPONIBLE ──→ EN_ATTENTE ──→ ATTRIBUE
     ↑               │
     └───────────────┘ (si toutes les demandes sont refusées/annulées)
     └──────────────────────────────────── ANNULE
```

### Cycle de vie du statut attribution

```
EN_ATTENTE ──→ ACCEPTEE
     │     └──→ REFUSEE
     └─────────→ ANNULEE
```

---

## 🐘 Configuration de la base de données

### Initialisation (à exécuter une seule fois en tant que superuser PostgreSQL)

```bash
# 1. Créer l'utilisateur et la base
psql -U postgres -f application-layer/src/main/resources/bdd/01_init.sql

# 2. Se connecter à la base medrelais_db et créer le schéma
psql -U medrelais_user -d medrelais_db -f application-layer/src/main/resources/bdd/02_create_bdd.sql
```

### Configuration `application-dev.yaml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/medrelais_db
    username: medrelais_user
    password: medrelais_pass
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        default_schema: medrelais

jwt:
  secret: <votre-clé-base64-256bits>
  expiration: 86400000   # 24h en ms
```

> ⚠️ Ne jamais commiter de mot de passe ou clé JWT en clair. Utilisez des variables d'environnement en production.

---

## 🚀 Lancement avec IntelliJ IDEA

1. **Ouvrir le projet** : `File → Open` → sélectionner le dossier `medrelais-backend`
2. **Importer les dépendances Maven** : clic droit sur `pom.xml` → `Maven → Reload Project`
3. **Configurer le profil** : dans la configuration de lancement, ajouter `-Dspring.profiles.active=dev`
4. **Lancer** : exécuter `Application.java` dans `application-layer`

---

## 🔀 Profils Spring

| Profil | Usage                  | BDD              |
|--------|------------------------|------------------|
| `dev`  | Développement local    | PostgreSQL local |
| `test` | Tests unitaires/intégr.| H2 en mémoire    |

---

## 🔐 Sécurité JWT

L'authentification repose sur des **tokens JWT Bearer** (stateless, pas de session).

### Flux d'authentification

```
POST /api/auth/register   → Inscription praticien → retourne { token }
POST /api/auth/login      → Connexion             → retourne { token }

Tous les autres endpoints → Header: Authorization: Bearer <token>
```

### Rôles

| Rôle       | Accès                                       |
|------------|---------------------------------------------|
| `PRATICIEN`| Lecture/écriture sur ses propres ressources |
| `ADMIN`    | Accès total, dont suppression et création   |

---

## 📖 Documentation Swagger

Une fois l'application démarrée, accéder à :

```
http://localhost:8080/swagger-ui.html
```

L'interface permet de tester tous les endpoints directement depuis le navigateur après avoir renseigné le token JWT via le bouton **Authorize 🔒**.

---

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test

# Lancer les tests d'un module spécifique
mvn test -pl domain-layer
mvn test -pl infrastructure-layer
```

Les tests utilisent le profil `test` avec une base H2 en mémoire.

---

## 🔨 Build Maven

```bash
# Compiler et packager
mvn clean package

# Ignorer les tests
mvn clean package -DskipTests

# Lancer le jar généré
java -jar application-layer/target/application-layer-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## 📦 Stack technique

| Technologie      | Version  | Usage                          |
|------------------|----------|--------------------------------|
| Java             | 21       | Langage                        |
| Spring Boot      | 3.4.3    | Framework applicatif           |
| Spring Security  | 6.x      | Authentification JWT           |
| Spring Data JPA  | 3.x      | Persistance                    |
| PostgreSQL       | 14+      | Base de données                |
| MapStruct        | 1.6.3    | Mapping objet (Entity↔BO↔DTO)  |
| Lombok           | 1.18.42  | Réduction du boilerplate       |
| JJWT             | 0.12.6   | Génération/validation JWT      |
| SpringDoc OpenAPI| 2.7.0    | Documentation Swagger          |
| H2               | -        | BDD en mémoire (tests)         |
| JaCoCo           | 0.8.12   | Couverture de tests            |
