# GeoConnect — Backend

API REST Spring Boot pour la plateforme GeoConnect, permettant la mise en relation entre **clients** et **bureaux d'études** pour des missions géotechniques.

---

## 📋 Sommaire

1. [Prérequis](#-prérequis)
2. [Architecture hexagonale](#-architecture-hexagonale)
3. [Configuration de la base de données](#-configuration-de-la-base-de-données)
4. [Lancement avec IntelliJ IDEA](#-lancement-avec-intellij-idea)
5. [Profils Spring](#-profils-spring)
6. [Sécurité JWT](#-sécurité-jwt)
7. [Documentation Swagger](#-documentation-swagger)
8. [Tests](#-tests)
9. [Build Maven](#-build-maven)

---

## ✅ Prérequis

| Outil | Version minimale |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9+ |
| PostgreSQL | 14+ (profil `dev`) |
| IntelliJ IDEA | 2023+ (Community ou Ultimate) |

---

## 🏛 Architecture hexagonale

Le projet est découpé en **3 modules Maven** qui respectent les principes de l'architecture hexagonale (Ports & Adapters) :

```
geoconnect-backend/
├── domain-layer/           → Cœur métier (pur Java, aucune dépendance framework)
├── infrastructure-layer/   → Adaptateurs techniques (BDD, Sécurité, JWT)
└── application-layer/      → Point d'entrée (Controllers REST, Swagger, Spring Boot)
```

### Détail des couches

#### 🟢 `domain-layer` — Domaine métier
> Contient uniquement la logique métier. **Aucune dépendance** vers Spring, JPA ou tout autre framework.

| Package | Rôle |
|---|---|
| `model/` | Objets métier (Business Objects) : `BureauEtudeBO`, `ClientBO`, `EtudeBO`... |
| `model/enums/` | Énumérations métier : `RoleEnum`, `EtatEtudeEnum`, `StatutAppelEnum` |
| `service/` | Interfaces des services métier |
| `service/impl/` | Implémentations des services |
| `port/` | **Interfaces** des ports sortants (ex: `UtilisateurRepository`, `PropositionDevisRepository`) |
| `exception/` | Exceptions métier |

#### 🔵 `infrastructure-layer` — Adaptateurs techniques
> Implémente les ports définis dans le domaine. Contient tout ce qui est lié à des technologies spécifiques.

| Package | Rôle |
|---|---|
| `database/entities/` | Entités JPA (mapping BDD) |
| `database/dao/` | Repositories Spring Data JPA |
| `database/adapter/` | Implémentations des ports du domaine |
| `database/mapper/` | Mappers MapStruct Entity ↔ BO |
| `database/configuration/` | Configuration JPA / Auditing |
| `security/` | Filtre JWT, service JWT, `UserDetails` |
| `security/configuration/` | Configuration Spring Security |

#### 🟡 `application-layer` — Couche applicative
> Point d'entrée de l'application. Orchestre les appels vers le domaine.

| Package | Rôle |
|---|---|
| `resource/` | Controllers REST (auth, bureauEtude, client, etude...) |
| `model/` | DTOs d'entrée/sortie des endpoints |
| `mapper/` | Mappers MapStruct DTO ↔ BO |
| `webapp/exception/` | Gestionnaire global d'exceptions (`@ControllerAdvice`) |
| `webapp/configuration/` | Configuration OpenAPI/Swagger |

### Flux d'une requête

```
HTTP Request
    ↓
[application-layer]  Controller REST
    ↓  (DTO → BO via MapStruct)
[domain-layer]       Service métier
    ↓  (appel via interface Port)
[infrastructure-layer]  Adapter → Repository JPA → PostgreSQL
```

---

## 🗄 Configuration de la base de données

### Initialisation PostgreSQL

Exécuter les scripts SQL dans l'ordre depuis un superutilisateur PostgreSQL (`postgres`) :

```bash
# 1. Créer l'utilisateur et la base
psql -U postgres -f application-layer/src/main/resources/bdd/01_init.sql

# 2. Créer le schéma et les tables (connecté sur geoconnect_db)
psql -U geoconnect_user -d geoconnect_db -f application-layer/src/main/resources/bdd/02_create_bdd.sql
```

### Paramètres de connexion (profil `dev`)

| Paramètre | Valeur |
|---|---|
| URL | `jdbc:postgresql://localhost:5432/geoconnect_db` |
| Schéma | `geoconnect` |
| Utilisateur | `geoconnect_user` |
| Mot de passe | `geoconnect_pass` |

> ⚠️ Ces valeurs sont celles par défaut pour le développement local. Ne jamais les utiliser en production.

---

## 🚀 Lancement avec IntelliJ IDEA

### 1. Importer le projet

1. **File** → **Open** → sélectionner le dossier `geoconnect-backend`
2. IntelliJ détecte automatiquement le projet Maven multi-module
3. Attendre le chargement des dépendances (barre de progression en bas)

### 2. Marquer les sources générées

Les classes générées par **MapStruct** ne sont pas automatiquement reconnues par IntelliJ :

1. Clic droit sur `application-layer/target/generated-sources/annotations`
2. **Mark Directory as** → **Generated Sources Root**
3. Répéter pour `infrastructure-layer/target/generated-sources/annotations`

### 3. Configurer la Run Configuration

1. **Run** → **Edit Configurations** → **+** → **Spring Boot**
2. Remplir les champs :

| Champ | Valeur |
|---|---|
| **Name** | `GeoConnect - Dev` |
| **Module** | `application-layer` |
| **Main class** | `vbm.medrelais.Application` |
| **Active profiles** | `dev` |

3. Cliquer sur **OK** puis **▶ Run**

### 4. Vérifier le démarrage

L'application démarre sur le port **8080** par défaut.  
Tester avec : `GET http://localhost:8080/actuator/health`

---

## ⚙️ Profils Spring

| Profil | Datasource | Usage |
|---|---|---|
| `dev` | PostgreSQL local (`localhost:5432`) | Développement local |
| `test` | H2 en mémoire | Tests automatisés (CI/CD) |

### Fichiers de configuration

```
application-layer/src/main/resources/
├── application.yaml          → Config commune (active le profil dev par défaut)
├── application-dev.yaml      → Config PostgreSQL
└── application-test.yaml     → Config H2 (pour les tests)
```

> Pour lancer en mode `dev`, passer `-Dspring.profiles.active=dev` en argument JVM ou configurer le profil dans IntelliJ (voir section précédente).

---

## 🔐 Sécurité JWT

L'API est sécurisée par **JSON Web Token (JWT)**. Tous les endpoints (sauf `/api/auth/**` et Swagger) nécessitent un token valide.

### Obtenir un token

#### 1. Créer un compte
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "motdepasse"
}
```

#### 2. Se connecter
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "motdepasse"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2..."
}
```

#### 3. Utiliser le token
Ajouter le header suivant à chaque requête protégée :
```
Authorization: Bearer <votre_token>
```

### Configuration JWT (`application.yaml`)

| Propriété | Valeur par défaut |
|---|---|
| `jwt.secret` | Clé HMAC-SHA256 (256 bits) |
| `jwt.expiration` | `86400000` ms = **24 heures** |

---

## 📖 Documentation Swagger

Une interface Swagger UI est disponible automatiquement après démarrage.

### Accès

| URL | Description |
|---|---|
| **http://localhost:8080/swagger-ui/index.html** | 🖥️ Interface graphique Swagger UI |
| `http://localhost:8080/v3/api-docs` | 📄 Spec OpenAPI au format JSON |
| `http://localhost:8080/v3/api-docs.yaml` | 📄 Spec OpenAPI au format YAML |

### Authentification dans Swagger UI

Pour tester les endpoints protégés directement depuis l'interface :

1. Récupérer un token via `POST /api/auth/login`
2. Cliquer sur le bouton **🔒 Authorize** (en haut à droite)
3. Saisir : `Bearer <votre_token>`
4. Cliquer sur **Authorize** puis **Close**

Tous les endpoints afficheront désormais le cadenas **🔒 fermé**.

---

## 🧪 Tests

### Lancer les tests

```bash
# Tous les tests
mvn test

# Tests d'un module spécifique
mvn test -pl application-layer
mvn test -pl infrastructure-layer
```

### Profil utilisé pour les tests

Les tests utilisent automatiquement le profil `test` (H2 en mémoire) grâce au fichier `src/test/resources/application.yaml` présent dans `application-layer`. Aucune instance PostgreSQL n'est nécessaire.

### Couverture de code (JaCoCo)

Un rapport de couverture est généré après chaque build :

```bash
mvn verify
```

Rapport disponible dans :
```
application-layer/target/site/jacoco/index.html
infrastructure-layer/target/site/jacoco/index.html
```

---

## 🔨 Build Maven

```bash
# Nettoyer et construire tous les modules
mvn clean install

# Construire sans les tests
mvn clean install -DskipTests

# Construire un module spécifique
mvn clean install -pl domain-layer
mvn clean install -pl infrastructure-layer -am
mvn clean install -pl application-layer -am
```

### Ordre de build des modules

```
1. domain-layer          (aucune dépendance interne)
2. infrastructure-layer  (dépend de domain-layer)
3. application-layer     (dépend de domain-layer + infrastructure-layer)
```

> L'option `-am` (also-make) construit automatiquement les modules dont dépend le module ciblé.

