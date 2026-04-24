package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.AttributionEntity;
import vbm.medrelais.database.entities.enums.StatutAttribution;

import java.util.List;

public interface AttributionDao extends JpaRepository<AttributionEntity, Long> {

    List<AttributionEntity> findByCreneauId(Long creneauId);

    List<AttributionEntity> findByRemplacantId(Long remplacantId);

    List<AttributionEntity> findByRemplacantIdAndStatut(Long remplacantId, StatutAttribution statut);

    boolean existsByCreneauIdAndRemplacantIdAndStatutIn(
            Long creneauId,
            Long remplacantId,
            List<StatutAttribution> statuts
    );
}

