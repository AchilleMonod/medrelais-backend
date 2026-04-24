# 📬 Guide de test Postman — MedRelais API

Ce guide décrit la marche à suivre complète pour tester tous les endpoints de l'API MedRelais via Postman, dans le bon ordre.

---

## ⚙️ Configuration initiale

### 1. Créer un environnement Postman

Dans Postman : **Environments → Add** → nommer `MedRelais - Dev`

Ajouter les variables suivantes :

| Variable       | Valeur initiale           | Description                        |
|----------------|---------------------------|------------------------------------|
| `base_url`     | `http://localhost:8080`   | URL de base de l'API               |
| `token`        | *(vide)*                  | Rempli automatiquement après login |
| `praticien_id` | *(vide)*                  | ID du praticien de test            |
| `creneau_id`   | *(vide)*                  | ID du créneau de test              |
| `attribution_id` | *(vide)*                | ID de l'attribution de test        |
| `regle_id`     | *(vide)*                  | ID de la règle de récurrence       |

### 2. Configurer l'authentification globale

Pour chaque requête protégée, ajouter dans l'onglet **Authorization** :
- **Type** : `Bearer Token`
- **Token** : `{{token}}`

> 💡 **Astuce** : configurer ce header au niveau de la **Collection** pour ne pas le répéter sur chaque requête.

### 3. Headers communs

Ajouter sur toutes les requêtes avec body :

| Header         | Valeur             |
|----------------|--------------------|
| `Content-Type` | `application/json` |

---

## 🔐 1. Authentification

### 1.1 — Inscription d'un praticien (register)

> ⚠️ Endpoint public, pas de token nécessaire.

**POST** `{{base_url}}/api/auth/register`

```json
{
  "email": "dr.dupont@medrelais.fr",
  "password": "motdepasse123",
  "nom": "Dupont",
  "prenom": "Jean",
  "telephone": "0612345678",
  "specialite": "Médecine générale",
  "adresseRue": "12 rue de la Paix",
  "adresseVille": "Paris",
  "adresseCodePostal": "75001",
  "latitude": 48.8698,
  "longitude": 2.3308,
  "bio": "Médecin généraliste avec 10 ans d'expérience"
}
```

**Réponse attendue : `200 OK`**
```json
{ "token": "eyJhbGci..." }
```

📌 **Script Tests Postman** — onglet `Tests` de la requête :
```javascript
const json = pm.response.json();
pm.environment.set("token", json.token);
pm.test("Token reçu", () => pm.expect(json.token).to.be.a('string'));
```

---

### 1.2 — Inscription d'un second praticien (remplaçant)

**POST** `{{base_url}}/api/auth/register`

```json
{
  "email": "dr.martin@medrelais.fr",
  "password": "motdepasse456",
  "nom": "Martin",
  "prenom": "Sophie",
  "telephone": "0698765432",
  "specialite": "Médecine générale",
  "adresseRue": "5 avenue Victor Hugo",
  "adresseVille": "Lyon",
  "adresseCodePostal": "69001",
  "latitude": 45.7640,
  "longitude": 4.8357,
  "bio": "Disponible pour des remplacements en Île-de-France"
}
```

---

### 1.3 — Connexion (login)

**POST** `{{base_url}}/api/auth/login`

```json
{
  "email": "dr.dupont@medrelais.fr",
  "password": "motdepasse123"
}
```

**Réponse attendue : `200 OK`**

📌 **Script Tests** :
```javascript
const json = pm.response.json();
pm.environment.set("token", json.token);
pm.test("Connexion réussie", () => pm.expect(json.token).to.be.a('string'));
```

---

## 👨‍⚕️ 2. Praticiens

### 2.1 — Lister tous les praticiens

**GET** `{{base_url}}/api/praticiens`

**Réponse attendue : `200 OK`**

📌 **Script Tests** :
```javascript
const json = pm.response.json();
pm.environment.set("praticien_id", json[0].id);
pm.test("Liste non vide", () => pm.expect(json).to.be.an('array').that.is.not.empty);
```

---

### 2.2 — Récupérer un praticien par ID

**GET** `{{base_url}}/api/praticiens/{{praticien_id}}`

**Réponse attendue : `200 OK`**

---

### 2.3 — Recherche par spécialité

**GET** `{{base_url}}/api/praticiens/specialite/Médecine générale`

**Réponse attendue : `200 OK`**

---

### 2.4 — Recherche par ville

**GET** `{{base_url}}/api/praticiens/ville/Paris`

**Réponse attendue : `200 OK`**

---

### 2.5 — Recherche géographique par proximité

**GET** `{{base_url}}/api/praticiens/nearby?latitude=48.8698&longitude=2.3308&rayonKm=10`

**Réponse attendue : `200 OK`** — praticiens dans un rayon de 10 km

---

### 2.6 — Mettre à jour un praticien

**PUT** `{{base_url}}/api/praticiens/{{praticien_id}}`

```json
{
  "email": "dr.dupont@medrelais.fr",
  "password": "motdepasse123",
  "nom": "Dupont",
  "prenom": "Jean",
  "telephone": "0612345678",
  "specialite": "Médecine générale",
  "adresseRue": "15 rue de Rivoli",
  "adresseVille": "Paris",
  "adresseCodePostal": "75001",
  "latitude": 48.8566,
  "longitude": 2.3522,
  "bio": "Bio mise à jour"
}
```

**Réponse attendue : `200 OK`**

---

## 📅 3. Créneaux

### 3.1 — Créer un créneau ponctuel

**POST** `{{base_url}}/api/creneaux`

```json
{
  "praticienId": {{praticien_id}},
  "dateDebut": "2026-05-15T08:00:00",
  "dateFin": "2026-05-15T12:00:00",
  "typeDuree": "MATIN",
  "titre": "Remplacement cabinet du 15 mai",
  "description": "Remplacement pour congés, cabinet de ville, patientèle habituelle"
}
```

**Réponse attendue : `201 Created`**

📌 **Script Tests** :
```javascript
const json = pm.response.json();
pm.environment.set("creneau_id", json.id);
pm.test("Créneau créé avec statut DISPONIBLE", () => pm.expect(json.statut).to.eql("DISPONIBLE"));
```

---

### 3.2 — Lister les créneaux d'un praticien

**GET** `{{base_url}}/api/creneaux/praticien/{{praticien_id}}`

**Réponse attendue : `200 OK`**

---

### 3.3 — Lister les créneaux par statut

**GET** `{{base_url}}/api/creneaux/praticien/{{praticien_id}}/statut/DISPONIBLE`

Valeurs possibles : `DISPONIBLE`, `EN_ATTENTE`, `ATTRIBUE`, `ANNULE`

**Réponse attendue : `200 OK`**

---

### 3.4 — Rechercher les créneaux disponibles sur une période

**GET** `{{base_url}}/api/creneaux/disponibles?debut=2026-05-01T00:00:00&fin=2026-05-31T23:59:59`

**Réponse attendue : `200 OK`**

---

### 3.5 — Récupérer un créneau par ID

**GET** `{{base_url}}/api/creneaux/{{creneau_id}}`

**Réponse attendue : `200 OK`**

---

### 3.6 — Changer le statut d'un créneau (PATCH)

**PATCH** `{{base_url}}/api/creneaux/{{creneau_id}}/statut?statut=ANNULE`

**Réponse attendue : `200 OK`**

> ⚠️ Remettre le créneau en `DISPONIBLE` avant de tester les attributions :
> `PATCH {{base_url}}/api/creneaux/{{creneau_id}}/statut?statut=DISPONIBLE`

---

## 🔁 4. Règles de récurrence

### 4.1 — Créer une règle de récurrence hebdomadaire

**POST** `{{base_url}}/api/regles-recurrence`

```json
{
  "praticienId": {{praticien_id}},
  "typeRecurrence": "HEBDOMADAIRE",
  "intervalle": 1,
  "joursSemaine": "1,3",
  "dateDebut": "2026-06-01",
  "dateFin": "2026-08-31",
  "heureDebut": "08:00:00",
  "heureFin": "12:00:00",
  "typeDuree": "MATIN"
}
```

> Génère automatiquement tous les **lundis et mercredis matins** du 1er juin au 31 août 2026.

**Réponse attendue : `201 Created`**

📌 **Script Tests** :
```javascript
const json = pm.response.json();
pm.environment.set("regle_id", json.id);
pm.test("Règle créée et active", () => pm.expect(json.active).to.be.true);
```

---

### 4.2 — Lister les règles actives d'un praticien

**GET** `{{base_url}}/api/regles-recurrence/praticien/{{praticien_id}}/actives`

**Réponse attendue : `200 OK`**

---

### 4.3 — Désactiver une règle

**PATCH** `{{base_url}}/api/regles-recurrence/{{regle_id}}/desactiver`

**Réponse attendue : `200 OK`** — `active: false`, créneaux existants conservés

---

### 4.4 — Supprimer une règle

**DELETE** `{{base_url}}/api/regles-recurrence/{{regle_id}}`

**Réponse attendue : `204 No Content`** — créneaux `DISPONIBLE` supprimés, autres conservés

---

## 🤝 5. Attributions (demandes de remplacement)

> Avant de tester : vérifier que `{{creneau_id}}` est un créneau en statut `DISPONIBLE`.

### 5.1 — Soumettre une demande de remplacement

> Se connecter en tant que **Dr. Martin** (le remplaçant) et récupérer son token.

**POST** `{{base_url}}/api/auth/login`
```json
{ "email": "dr.martin@medrelais.fr", "password": "motdepasse456" }
```
Sauvegarder le token du remplaçant dans une variable `token_remplacant`.

---

**POST** `{{base_url}}/api/attributions`

*(utiliser `Authorization: Bearer {{token_remplacant}}`)*

```json
{
  "creneauId": {{creneau_id}},
  "remplacantId": <id_dr_martin>,
  "dateDebutAttribution": "2026-05-15T08:00:00",
  "dateFinAttribution": "2026-05-15T12:00:00",
  "message": "Disponible pour ce remplacement, cabinet proche de mon domicile"
}
```

**Réponse attendue : `201 Created`** — statut `EN_ATTENTE`, créneau passe à `EN_ATTENTE`

📌 **Script Tests** :
```javascript
const json = pm.response.json();
pm.environment.set("attribution_id", json.id);
pm.test("Demande EN_ATTENTE", () => pm.expect(json.statut).to.eql("EN_ATTENTE"));
```

---

### 5.2 — Lister les demandes pour un créneau

*(Repasser sur le token de Dr. Dupont)*

**GET** `{{base_url}}/api/attributions/creneau/{{creneau_id}}`

**Réponse attendue : `200 OK`**

---

### 5.3 — Lister les demandes d'un remplaçant

**GET** `{{base_url}}/api/attributions/remplacant/<id_dr_martin>`

**Réponse attendue : `200 OK`**

---

### 5.4 — Lister les demandes d'un remplaçant par statut

**GET** `{{base_url}}/api/attributions/remplacant/<id_dr_martin>/statut/EN_ATTENTE`

Valeurs possibles : `EN_ATTENTE`, `ACCEPTEE`, `REFUSEE`, `ANNULEE`

---

### 5.5 — Accepter une demande

*(En tant que Dr. Dupont — le praticien propriétaire du créneau)*

**PATCH** `{{base_url}}/api/attributions/{{attribution_id}}/accepter`

**Réponse attendue : `200 OK`**
- Attribution → `ACCEPTEE`
- Créneau → `ATTRIBUE`
- Les autres demandes EN_ATTENTE sur ce créneau → `REFUSEE` automatiquement

---

### 5.6 — Refuser une demande

> Créer d'abord une nouvelle demande sur un créneau disponible, puis :

**PATCH** `{{base_url}}/api/attributions/{{attribution_id}}/refuser`

**Réponse attendue : `200 OK`**
- Attribution → `REFUSEE`
- Si plus aucune demande EN_ATTENTE → créneau revient à `DISPONIBLE`

---

### 5.7 — Annuler une demande (par le remplaçant)

*(En tant que Dr. Martin)*

**PATCH** `{{base_url}}/api/attributions/{{attribution_id}}/annuler`

**Réponse attendue : `200 OK`**
- Attribution → `ANNULEE`
- Si elle était `ACCEPTEE` → créneau revient à `DISPONIBLE`

---

## ❌ 6. Cas d'erreur à tester

### 6.1 — Ressource introuvable → 404

**GET** `{{base_url}}/api/praticiens/99999`

```json
{
  "typeError": "FIELD_VALIDATION",
  "message": "Praticien introuvable avec l'id : 99999"
}
```

---

### 6.2 — Validation échouée → 400

**POST** `{{base_url}}/api/creneaux` avec un body incomplet :

```json
{ "titre": "Test sans champs obligatoires" }
```

```json
{
  "typeError": "FIELD_VALIDATION",
  "message": "praticienId : L'identifiant du praticien est obligatoire; ..."
}
```

---

### 6.3 — Doublon de demande → 409

Soumettre deux fois la même demande avec le même remplaçant sur le même créneau.

```json
{
  "typeError": "FIELD_VALIDATION",
  "message": "Le remplaçant id=X a déjà une demande active sur ce créneau"
}
```

---

### 6.4 — Créneau non disponible → 409

Tenter une demande sur un créneau `ATTRIBUE` ou `ANNULE`.

---

### 6.5 — Token absent → 401

Appeler un endpoint protégé sans header `Authorization`.

```json
{ "typeError": "ACCESS_DENIED", "message": "Token JWT absent, expiré ou invalide" }
```

---

### 6.6 — Token valide mais rôle insuffisant → 403

Appeler `DELETE /api/praticiens/{{praticien_id}}` avec un token `PRATICIEN` (réservé `ADMIN`).

---

## 🗂 Ordre de test recommandé (scénario complet)

```
1. POST /api/auth/register        → Créer Dr. Dupont (praticien)
2. POST /api/auth/register        → Créer Dr. Martin (remplaçant)
3. POST /api/auth/login           → Se connecter en tant que Dr. Dupont
4. GET  /api/praticiens           → Récupérer la liste + sauvegarder praticien_id
5. POST /api/creneaux             → Créer un créneau ponctuel
6. POST /api/regles-recurrence    → Créer une règle hebdomadaire
7. GET  /api/creneaux/disponibles → Vérifier les créneaux générés
8. POST /api/auth/login           → Se connecter en tant que Dr. Martin
9. POST /api/attributions         → Dr. Martin postule sur un créneau
10. PATCH /api/attributions/{id}/accepter → Dr. Dupont accepte
11. GET  /api/creneaux/{id}       → Vérifier statut ATTRIBUE
12. PATCH /api/regles-recurrence/{id}/desactiver → Désactiver la règle
```

