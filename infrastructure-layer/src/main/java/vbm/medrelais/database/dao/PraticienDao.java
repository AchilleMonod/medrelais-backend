package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vbm.medrelais.database.entities.PraticienEntity;

import java.util.List;

public interface PraticienDao extends JpaRepository<PraticienEntity, Long> {

    List<PraticienEntity> findBySpecialiteIgnoreCase(String specialite);

    List<PraticienEntity> findByAdresseVilleIgnoreCase(String ville);

    /**
     * Recherche géographique par rayon via formule Haversine.
     * Retourne les praticiens dans un rayon de {@code rayonKm} kilomètres
     * autour du point (latitude, longitude).
     */
    @Query("""
            SELECT p FROM PraticienEntity p
            WHERE (6371 * acos(
                cos(radians(:latitude))  * cos(radians(p.latitude))
                * cos(radians(p.longitude) - radians(:longitude))
                + sin(radians(:latitude)) * sin(radians(p.latitude))
            )) <= :rayonKm
            """)
    List<PraticienEntity> findNearby(
            @Param("latitude")  double latitude,
            @Param("longitude") double longitude,
            @Param("rayonKm")   double rayonKm
    );
}

