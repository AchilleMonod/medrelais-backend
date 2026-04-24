package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.RegleRecurrenceNotFoundException;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.RegleRecurrenceBO;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.port.CreneauRepository;
import vbm.medrelais.port.RegleRecurrenceRepository;
import vbm.medrelais.service.RegleRecurrenceService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RegleRecurrenceServiceImpl implements RegleRecurrenceService {

    private final RegleRecurrenceRepository regleRecurrenceRepository;
    private final CreneauRepository creneauRepository;

    @Override
    public RegleRecurrenceBO getById(Long id) {
        log.info("Récupération de la règle de récurrence id={}", id);
        return regleRecurrenceRepository.findById(id)
                .orElseThrow(() -> new RegleRecurrenceNotFoundException(id));
    }

    @Override
    public List<RegleRecurrenceBO> getByPraticienId(Long praticienId) {
        log.info("Récupération des règles du praticien id={}", praticienId);
        return regleRecurrenceRepository.findByPraticienId(praticienId);
    }

    @Override
    public List<RegleRecurrenceBO> getActivesByPraticienId(Long praticienId) {
        log.info("Récupération des règles actives du praticien id={}", praticienId);
        return regleRecurrenceRepository.findActivesByPraticienId(praticienId);
    }

    @Override
    public RegleRecurrenceBO create(RegleRecurrenceBO regle) {
        log.info("Création d'une règle de récurrence pour le praticien id={}", regle.getPraticien().getId());
        regle.setActive(true);
        RegleRecurrenceBO saved = regleRecurrenceRepository.save(regle);

        // Générer les créneaux à partir de la règle
        List<CreneauBO> creneaux = genererCreneaux(saved);
        if (!creneaux.isEmpty()) {
            creneauRepository.saveAll(creneaux);
            log.info("{} créneau(x) généré(s) pour la règle id={}", creneaux.size(), saved.getId());
        }

        log.info("Règle de récurrence créée avec succès : id={}", saved.getId());
        return saved;
    }

    @Override
    public RegleRecurrenceBO update(Long id, RegleRecurrenceBO regle) {
        log.info("Mise à jour de la règle de récurrence id={}", id);
        RegleRecurrenceBO existing = regleRecurrenceRepository.findById(id)
                .orElseThrow(() -> new RegleRecurrenceNotFoundException(id));

        regle.setId(existing.getId());
        regle.setCreatedAt(existing.getCreatedAt());

        // Supprimer les créneaux DISPONIBLES liés à l'ancienne règle et en regénérer
        creneauRepository.findByRegleRecurrenceId(id).stream()
                .filter(c -> c.getStatut() == StatutCreneau.DISPONIBLE)
                .forEach(c -> creneauRepository.deleteById(c.getId()));

        RegleRecurrenceBO updated = regleRecurrenceRepository.save(regle);

        List<CreneauBO> creneaux = genererCreneaux(updated);
        if (!creneaux.isEmpty()) {
            creneauRepository.saveAll(creneaux);
            log.info("{} créneau(x) regénéré(s) pour la règle id={}", creneaux.size(), id);
        }

        log.info("Règle de récurrence mise à jour avec succès : id={}", id);
        return updated;
    }

    @Override
    public RegleRecurrenceBO desactiver(Long id) {
        log.info("Désactivation de la règle de récurrence id={}", id);
        RegleRecurrenceBO regle = regleRecurrenceRepository.findById(id)
                .orElseThrow(() -> new RegleRecurrenceNotFoundException(id));
        regle.setActive(false);
        RegleRecurrenceBO updated = regleRecurrenceRepository.save(regle);
        log.info("Règle de récurrence id={} désactivée", id);
        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression de la règle de récurrence id={}", id);
        regleRecurrenceRepository.findById(id)
                .orElseThrow(() -> new RegleRecurrenceNotFoundException(id));

        // Supprimer uniquement les créneaux encore DISPONIBLES
        creneauRepository.findByRegleRecurrenceId(id).stream()
                .filter(c -> c.getStatut() == StatutCreneau.DISPONIBLE)
                .forEach(c -> creneauRepository.deleteById(c.getId()));

        regleRecurrenceRepository.deleteById(id);
        log.info("Règle de récurrence supprimée avec succès : id={}", id);
    }

    // =========================================================
    // Génération des créneaux selon les paramètres de la règle
    // =========================================================

    private List<CreneauBO> genererCreneaux(RegleRecurrenceBO regle) {
        List<CreneauBO> creneaux = new ArrayList<>();
        LocalDate dateCourante = regle.getDateDebut();
        LocalDate dateLimite   = resolverDateFin(regle);

        int occurrences = 0;

        while (!dateCourante.isAfter(dateLimite)) {
            if (dateCorrespond(dateCourante, regle)) {
                LocalDateTime debut = dateCourante.atTime(regle.getHeureDebut());
                LocalDateTime fin   = dateCourante.atTime(regle.getHeureFin());

                creneaux.add(CreneauBO.builder()
                        .praticien(regle.getPraticien())
                        .dateDebut(debut)
                        .dateFin(fin)
                        .typeDuree(regle.getTypeDuree())
                        .statut(StatutCreneau.DISPONIBLE)
                        .regleRecurrence(regle)
                        .estException(false)
                        .build());

                occurrences++;
                if (regle.getNombreOccurrences() != null && occurrences >= regle.getNombreOccurrences()) {
                    break;
                }
            }
            dateCourante = dateCourante.plusDays(1);
        }

        return creneaux;
    }

    private LocalDate resolverDateFin(RegleRecurrenceBO regle) {
        if (regle.getDateFin() != null) {
            return regle.getDateFin();
        }
        // Pas de date de fin ni d'occurrences → générer sur 1 an par défaut
        return regle.getDateDebut().plusYears(1);
    }

    /**
     * Vérifie si la date donnée correspond aux critères de la règle
     * (jour de semaine, jour du mois, mois de l'année).
     */
    private boolean dateCorrespond(LocalDate date, RegleRecurrenceBO regle) {
        if (regle.getJoursSemaine() != null && !regle.getJoursSemaine().isBlank()
                && csvNonContient(regle.getJoursSemaine(), date.getDayOfWeek().getValue())) {
            return false;
        }
        if (regle.getJoursMois() != null && !regle.getJoursMois().isBlank()
                && csvNonContient(regle.getJoursMois(), date.getDayOfMonth())) {
            return false;
        }
        return regle.getMoisAnnee() == null || regle.getMoisAnnee().isBlank()
                || !csvNonContient(regle.getMoisAnnee(), date.getMonthValue());
    }

    private boolean csvNonContient(String csv, int valeur) {
        String val = String.valueOf(valeur);
        for (String s : csv.split(",")) {
            if (s.trim().equals(val)) return false;
        }
        return true;
    }
}


