-- =============================================
-- MEDRELAIS - Schéma v1.0
-- =============================================

-- 1. Création du schéma dédié
CREATE SCHEMA IF NOT EXISTS medrelais;

-- 2. Accorder les droits sur le schéma à l'utilisateur applicatif
GRANT USAGE  ON SCHEMA medrelais TO medrelais_user;
GRANT CREATE ON SCHEMA medrelais TO medrelais_user;

-- Définir le search_path par défaut pour cet utilisateur
-- (plus besoin de préfixer chaque requête avec "medrelais.")
ALTER ROLE medrelais_user SET search_path TO medrelais;

-- Cibler le schéma pour la session courante (exécution du script)
SET search_path TO medrelais;

-- =============================================
-- 3. Table utilisateur (SINGLE_TABLE inheritance)
--    Le discriminant "role" détermine le sous-type (PRATICIEN, ADMIN)
-- =============================================
CREATE TABLE medrelais.utilisateur (
    id           BIGSERIAL    PRIMARY KEY,
    role         VARCHAR(20)  NOT NULL
                     CHECK (role IN ('PRATICIEN', 'ADMIN')),
    email        VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom          VARCHAR(100) NOT NULL,
    prenom       VARCHAR(100) NOT NULL,
    telephone    VARCHAR(20),

    -- Champs spécifiques à PraticienEntity (NULL pour les ADMIN)
    specialite          VARCHAR(100),
    adresse_rue         VARCHAR(255),
    adresse_ville       VARCHAR(100),
    adresse_code_postal VARCHAR(10),
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    bio                 TEXT,

    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. Règle de récurrence
--    Permet à un praticien de définir une série de créneaux récurrents
-- =============================================
CREATE TABLE medrelais.regle_recurrence (
    id                 BIGSERIAL   PRIMARY KEY,
    praticien_id       BIGINT      NOT NULL
                           REFERENCES medrelais.utilisateur(id) ON DELETE CASCADE,
    type_recurrence    VARCHAR(20) NOT NULL
                           CHECK (type_recurrence IN ('QUOTIDIEN', 'HEBDOMADAIRE', 'MENSUEL', 'ANNUEL', 'CUSTOM')),
    intervalle         INT         NOT NULL DEFAULT 1,

    -- Jours concernés (stockés en chaînes CSV pour portabilité multi-BDD)
    -- Ex: jours_semaine = '1,3,5' pour lundi, mercredi, vendredi
    jours_semaine      VARCHAR(20),  -- 1=lundi … 7=dimanche
    jours_mois         VARCHAR(60),  -- ex: '1,15'
    mois_annee         VARCHAR(30),  -- ex: '6,7,8'

    date_debut         DATE        NOT NULL,
    date_fin           DATE,                  -- NULL = infini (exclusif avec nombre_occurrences)
    nombre_occurrences INT,                   -- exclusif avec date_fin

    heure_debut        TIME        NOT NULL,
    heure_fin          TIME        NOT NULL,
    type_duree         VARCHAR(20) NOT NULL
                           CHECK (type_duree IN ('MATIN', 'APRES_MIDI', 'JOURNEE', 'CUSTOM')),
    active             BOOLEAN     NOT NULL DEFAULT TRUE,

    created_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 5. Créneau
--    Représente un slot de remplacement proposé par un praticien
-- =============================================
CREATE TABLE medrelais.creneau (
    id                  BIGSERIAL   PRIMARY KEY,
    praticien_id        BIGINT      NOT NULL
                            REFERENCES medrelais.utilisateur(id) ON DELETE CASCADE,
    date_debut          TIMESTAMP   NOT NULL,
    date_fin            TIMESTAMP   NOT NULL,
    type_duree          VARCHAR(20) NOT NULL
                            CHECK (type_duree IN ('MATIN', 'APRES_MIDI', 'JOURNEE', 'CUSTOM')),
    statut              VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
                            CHECK (statut IN ('DISPONIBLE', 'EN_ATTENTE', 'ATTRIBUE', 'ANNULE')),
    titre               VARCHAR(255),
    description         TEXT,
    regle_recurrence_id BIGINT
                            REFERENCES medrelais.regle_recurrence(id) ON DELETE SET NULL,
    est_exception       BOOLEAN     NOT NULL DEFAULT FALSE,

    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 6. Attribution
--    Demande d'un remplaçant pour un créneau donné
-- =============================================
CREATE TABLE medrelais.attribution (
    id                   BIGSERIAL   PRIMARY KEY,
    creneau_id           BIGINT      NOT NULL
                             REFERENCES medrelais.creneau(id) ON DELETE CASCADE,
    remplacant_id        BIGINT      NOT NULL
                             REFERENCES medrelais.utilisateur(id) ON DELETE CASCADE,
    date_debut_attribution TIMESTAMP NOT NULL,
    date_fin_attribution   TIMESTAMP NOT NULL,
    statut               VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE'
                             CHECK (statut IN ('EN_ATTENTE', 'ACCEPTEE', 'REFUSEE', 'ANNULEE')),
    message              TEXT,

    created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 7. Index
-- =============================================
CREATE INDEX idx_utilisateur_role        ON medrelais.utilisateur(role);
CREATE INDEX idx_utilisateur_email       ON medrelais.utilisateur(email);
CREATE INDEX idx_utilisateur_geo         ON medrelais.utilisateur(latitude, longitude);

CREATE INDEX idx_regle_praticien         ON medrelais.regle_recurrence(praticien_id);

CREATE INDEX idx_creneau_praticien       ON medrelais.creneau(praticien_id);
CREATE INDEX idx_creneau_statut          ON medrelais.creneau(statut);
CREATE INDEX idx_creneau_dates           ON medrelais.creneau(date_debut, date_fin);
CREATE INDEX idx_creneau_regle           ON medrelais.creneau(regle_recurrence_id);

CREATE INDEX idx_attribution_creneau     ON medrelais.attribution(creneau_id);
CREATE INDEX idx_attribution_remplacant  ON medrelais.attribution(remplacant_id);
CREATE INDEX idx_attribution_statut      ON medrelais.attribution(statut);

-- =============================================
-- 8. Accorder les droits sur les tables créées
-- =============================================
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA medrelais TO medrelais_user;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA medrelais TO medrelais_user;

-- Pour les futures tables créées
ALTER DEFAULT PRIVILEGES IN SCHEMA medrelais
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES    TO medrelais_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA medrelais
    GRANT USAGE, SELECT                  ON SEQUENCES TO medrelais_user;
