package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vbm.medrelais.database.entities.CreneauEntity;
import vbm.medrelais.database.entities.enums.StatutCreneau;

import java.time.LocalDateTime;
import java.util.List;

public interface CreneauDao extends JpaRepository<CreneauEntity, Long> {

    List<CreneauEntity> findByPraticienId(Long praticienId);

    List<CreneauEntity> findByPraticienIdAndStatut(Long praticienId, StatutCreneau statut);

    /**
     * Créneaux disponibles dont la plage chevauche la période [debut, fin].
     */
    @Query("""
            SELECT c FROM CreneauEntity c
            WHERE c.statut = 'DISPONIBLE'
              AND c.dateDebut < :fin
              AND c.dateFin   > :debut
            """)
    List<CreneauEntity> findDisponiblesByPeriode(
            @Param("debut") LocalDateTime debut,
            @Param("fin")   LocalDateTime fin
    );

    List<CreneauEntity> findByRegleRecurrenceId(Long regleRecurrenceId);

    @Modifying
    @Query("DELETE FROM CreneauEntity c WHERE c.regleRecurrence.id = :regleId")
    void deleteByRegleRecurrenceId(@Param("regleId") Long regleId);
}

