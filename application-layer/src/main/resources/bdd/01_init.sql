-- 1. Créer un utilisateur dédié à l'application
CREATE USER geoconnect_user WITH PASSWORD 'geoconnect_pass';

-- 2. Créer la base de données
CREATE DATABASE geoconnect_db
    WITH OWNER = geoconnect_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TEMPLATE = template0;

-- 3. Accorder les privilèges
GRANT ALL PRIVILEGES ON DATABASE geoconnect_db TO geoconnect_user;