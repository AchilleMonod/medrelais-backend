package vbm.medrelais.port;

import vbm.medrelais.model.PraticienBO;

import java.util.List;
import java.util.Optional;

public interface PraticienRepository {

    Optional<PraticienBO> findById(Long id);

    List<PraticienBO> findAll();

    List<PraticienBO> findBySpecialite(String specialite);

    List<PraticienBO> findByVille(String ville);

    /**
     * Recherche géographique par rayon (pour la mise en relation de proximité).
     * L'implémentation utilisera une requête Haversine côté BDD.
     */
    List<PraticienBO> findNearby(double latitude, double longitude, double rayonKm);

    PraticienBO save(PraticienBO praticien);

    void deleteById(Long id);
}

