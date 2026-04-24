package vbm.medrelais.service;

import vbm.medrelais.model.RegleRecurrenceBO;

import java.util.List;

public interface RegleRecurrenceService {

    RegleRecurrenceBO getById(Long id);

    List<RegleRecurrenceBO> getByPraticienId(Long praticienId);

    List<RegleRecurrenceBO> getActivesByPraticienId(Long praticienId);

    /**
     * Crée une règle de récurrence et génère les créneaux correspondants
     * sur la période définie.
     */
    RegleRecurrenceBO create(RegleRecurrenceBO regle);

    RegleRecurrenceBO update(Long id, RegleRecurrenceBO regle);

    /**
     * Désactive la règle sans supprimer les créneaux déjà générés.
     */
    RegleRecurrenceBO desactiver(Long id);

    /**
     * Supprime la règle et tous les créneaux encore disponibles liés à celle-ci.
     */
    void delete(Long id);
}

