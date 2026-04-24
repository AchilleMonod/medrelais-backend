package vbm.medrelais.service;

import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.enums.StatutCreneau;

import java.time.LocalDateTime;
import java.util.List;

public interface CreneauService {

    CreneauBO getById(Long id);

    List<CreneauBO> getByPraticienId(Long praticienId);

    List<CreneauBO> getByPraticienIdAndStatut(Long praticienId, StatutCreneau statut);

    List<CreneauBO> getDisponiblesByPeriode(LocalDateTime debut, LocalDateTime fin);

    /** Crée un créneau ponctuel */
    CreneauBO create(CreneauBO creneau);

    CreneauBO update(Long id, CreneauBO creneau);

    /** Change uniquement le statut d'un créneau */
    CreneauBO updateStatut(Long id, StatutCreneau nouveauStatut);

    void delete(Long id);
}

