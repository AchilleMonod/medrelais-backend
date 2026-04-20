package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.UtilisateurEntity;

import java.util.Optional;

public interface UserDAO extends JpaRepository<UtilisateurEntity, Long> {
    Optional<UtilisateurEntity> findByEmail(String email);
}

