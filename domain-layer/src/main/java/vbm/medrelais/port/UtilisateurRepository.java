package vbm.medrelais.port;

import vbm.medrelais.model.UtilisateurBO;

import java.util.Optional;

public interface UtilisateurRepository {

    Optional<UtilisateurBO> findById(Long id);

    Optional<UtilisateurBO> findByEmail(String email);

    boolean existsByEmail(String email);

    UtilisateurBO save(UtilisateurBO utilisateur);

    void deleteById(Long id);
}
