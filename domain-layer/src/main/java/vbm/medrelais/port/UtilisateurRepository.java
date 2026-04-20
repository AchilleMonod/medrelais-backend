package vbm.medrelais.port;

import vbm.medrelais.model.UtilisateurBO;

import java.util.Optional;

public interface UtilisateurRepository {
    Optional<UtilisateurBO> findByEmail(String email);
    UtilisateurBO save(UtilisateurBO user);
}

