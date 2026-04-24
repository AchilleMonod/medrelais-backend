package vbm.medrelais.port;

import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.enums.StatutCreneau;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CreneauRepository {

    Optional<CreneauBO> findById(Long id);

    List<CreneauBO> findByPraticienId(Long praticienId);

    List<CreneauBO> findByPraticienIdAndStatut(Long praticienId, StatutCreneau statut);

    /** Créneaux disponibles chevauchant la période demandée */
    List<CreneauBO> findDisponiblesByPeriode(LocalDateTime debut, LocalDateTime fin);

    List<CreneauBO> findByRegleRecurrenceId(Long regleRecurrenceId);

    CreneauBO save(CreneauBO creneau);

    List<CreneauBO> saveAll(List<CreneauBO> creneaux);

    void deleteById(Long id);

    void deleteByRegleRecurrenceId(Long regleRecurrenceId);
}

