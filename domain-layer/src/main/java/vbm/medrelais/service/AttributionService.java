package vbm.medrelais.service;

import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.model.enums.StatutAttribution;

import java.util.List;

public interface AttributionService {

    AttributionBO getById(Long id);

    List<AttributionBO> getByCreneauId(Long creneauId);

    List<AttributionBO> getByRemplacantId(Long remplacantId);

    List<AttributionBO> getByRemplacantIdAndStatut(Long remplacantId, StatutAttribution statut);

    /** Soumet une demande de remplacement sur un créneau */
    AttributionBO demanderRemplacement(AttributionBO attribution);

    /** Accepte une demande — refuse automatiquement toutes les autres pour ce créneau */
    AttributionBO accepter(Long attributionId);

    /** Refuse une demande */
    AttributionBO refuser(Long attributionId);

    /** Annule une demande (par le remplaçant lui-même) */
    AttributionBO annuler(Long attributionId);
}

