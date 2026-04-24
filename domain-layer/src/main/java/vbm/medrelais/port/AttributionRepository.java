package vbm.medrelais.port;

import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.model.enums.StatutAttribution;

import java.util.List;
import java.util.Optional;

public interface AttributionRepository {

    Optional<AttributionBO> findById(Long id);

    List<AttributionBO> findByCreneauId(Long creneauId);

    List<AttributionBO> findByRemplacantId(Long remplacantId);

    List<AttributionBO> findByRemplacantIdAndStatut(Long remplacantId, StatutAttribution statut);

    /** Vérifie si un remplaçant a déjà une demande en attente ou acceptée sur un créneau */
    boolean existsByCreneauIdAndRemplacantIdAndStatutIn(Long creneauId, Long remplacantId, List<StatutAttribution> statuts);

    AttributionBO save(AttributionBO attribution);

    void deleteById(Long id);
}

