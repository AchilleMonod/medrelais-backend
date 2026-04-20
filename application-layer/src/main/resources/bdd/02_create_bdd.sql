-- =============================================
-- GEOCONNECT - Schéma v1.0
-- =============================================

-- 1. Création du schéma dédié
CREATE SCHEMA IF NOT EXISTS geoconnect;

-- 2. Accorder les droits sur le schéma à l'utilisateur applicatif
GRANT USAGE  ON SCHEMA geoconnect TO geoconnect_user;
GRANT CREATE ON SCHEMA geoconnect TO geoconnect_user;

-- Définir le search_path par défaut pour cet utilisateur
-- (plus besoin de préfixer chaque requête avec "geoconnect.")
ALTER ROLE geoconnect_user SET search_path TO geoconnect;

-- Cibler le schéma pour la session courante (exécution du script)
SET search_path TO geoconnect;

-- =============================================
-- 3. Tables de base (sans FK sortantes)
-- =============================================
CREATE TABLE geoconnect.adresse (
                                    id          BIGSERIAL    PRIMARY KEY,
                                    rue         VARCHAR(255) NOT NULL,
                                    code_postal VARCHAR(10)  NOT NULL,
                                    ville       VARCHAR(100) NOT NULL,
                                    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE geoconnect.document (
                                     id              BIGSERIAL    PRIMARY KEY,
                                     chemin_document VARCHAR(500) NOT NULL,
                                     created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. Authentification
-- =============================================
CREATE TABLE geoconnect.utilisateur (
                                        id         BIGSERIAL    PRIMARY KEY,
                                        email      VARCHAR(255) NOT NULL UNIQUE,
                                        password   VARCHAR(255) NOT NULL,
                                        role       VARCHAR(20)  NOT NULL
                                            CHECK (role IN ('CLIENT', 'BUREAU_ETUDE', 'ADMIN')),
                                        created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 5. Profils métier
-- =============================================
CREATE TABLE geoconnect.client (
                                   id                     BIGSERIAL    PRIMARY KEY,
                                   user_id                BIGINT       NOT NULL UNIQUE
                                       REFERENCES geoconnect.utilisateur(id) ON DELETE CASCADE,
                                   nom                    VARCHAR(100) NOT NULL,
                                   prenom                 VARCHAR(100) NOT NULL,
                                   adresse_facturation_id BIGINT       NOT NULL
                                       REFERENCES geoconnect.adresse(id),
                                   created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE geoconnect.bureau_etudes (
                                          id             BIGSERIAL    PRIMARY KEY,
                                          user_id        BIGINT       NOT NULL UNIQUE
                                              REFERENCES geoconnect.utilisateur(id) ON DELETE CASCADE,
                                          raison_sociale VARCHAR(255) NOT NULL,
                                          email          VARCHAR(255) NOT NULL,
                                          tel            VARCHAR(20)  NOT NULL,
                                          created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 6. Flux métier
-- =============================================
CREATE TABLE geoconnect.demande_devis (
                                          id          BIGSERIAL  PRIMARY KEY,
                                          delai_max   DATE,
                                          adresse_id  BIGINT     NOT NULL UNIQUE
                                              REFERENCES geoconnect.adresse(id),
                                          client_id   BIGINT     NOT NULL
                                              REFERENCES geoconnect.client(id),
                                          docs_devis_id BIGINT     UNIQUE
                                              REFERENCES geoconnect.document(id),
                                          created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE geoconnect.proposition_devis (
                                              id               BIGSERIAL      PRIMARY KEY,
                                              bureau_etudes_id BIGINT         NOT NULL
                                                  REFERENCES geoconnect.bureau_etudes(id),
                                              demande_devis_id BIGINT         NOT NULL
                                                  REFERENCES geoconnect.demande_devis(id),
                                              date_rendu       DATE           NOT NULL,
                                              prix             NUMERIC(15, 2) NOT NULL,
                                              devis_PDF_id      BIGINT         UNIQUE
                                                  REFERENCES geoconnect.document(id),
                                              refusee          BOOLEAN        NOT NULL DEFAULT FALSE,
                                              created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE geoconnect.etude (
                                  id                   BIGSERIAL   PRIMARY KEY,
                                  bureau_etudes_id     BIGINT      NOT NULL
                                      REFERENCES geoconnect.bureau_etudes(id),
                                  client_id            BIGINT      NOT NULL
                                      REFERENCES geoconnect.client(id),
                                  proposition_devis_id BIGINT      NOT NULL UNIQUE
                                      REFERENCES geoconnect.proposition_devis(id),
                                  etat                 VARCHAR(40) NOT NULL
                                      CHECK (etat IN (
                                                      'DEVIS_VALIDE',
                                                      'DATE_INTERVENTION_PROPOSEE',
                                                      'DATE_INTERVENTION_FIXEE',
                                                      'INTERVENTION_EFFECTUEE',
                                                      'RAPPORT_TERMINE',
                                                      'PAIEMENT_EFFECTUE'
                                          )),
                                  rapport_id           BIGINT      UNIQUE
                                      REFERENCES geoconnect.document(id),
                                  created_at           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 7. Index
-- =============================================
CREATE INDEX idx_demande_devis_client ON geoconnect.demande_devis(client_id);
CREATE INDEX idx_proposition_demande  ON geoconnect.proposition_devis(demande_devis_id);
CREATE INDEX idx_proposition_bureau   ON geoconnect.proposition_devis(bureau_etudes_id);
CREATE INDEX idx_etude_bureau         ON geoconnect.etude(bureau_etudes_id);
CREATE INDEX idx_etude_client         ON geoconnect.etude(client_id);
CREATE INDEX idx_etude_etat           ON geoconnect.etude(etat);

-- =============================================
-- 8. Accorder les droits sur les tables créées
-- =============================================
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA geoconnect TO geoconnect_user;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA geoconnect TO geoconnect_user;

-- Pour les futures tables créées
ALTER DEFAULT PRIVILEGES IN SCHEMA geoconnect
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES    TO geoconnect_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA geoconnect
    GRANT USAGE, SELECT                  ON SEQUENCES TO geoconnect_user;