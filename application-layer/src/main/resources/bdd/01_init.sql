-- 1. Créer un utilisateur dédié à l'application
CREATE USER medrelais_user WITH PASSWORD 'medrelais_pass';

-- 2. Créer la base de données
CREATE DATABASE medrelais_db
    WITH OWNER = medrelais_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TEMPLATE = template0;

-- 3. Accorder les privilèges
GRANT ALL PRIVILEGES ON DATABASE medrelais_db TO medrelais_user;
