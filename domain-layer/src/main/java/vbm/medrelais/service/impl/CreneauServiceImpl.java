package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.port.CreneauRepository;
import vbm.medrelais.service.CreneauService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CreneauServiceImpl implements CreneauService {

    private final CreneauRepository creneauRepository;

    @Override
    public CreneauBO getById(Long id) {
        log.info("Récupération du créneau id={}", id);
        return creneauRepository.findById(id)
                .orElseThrow(() -> new CreneauNotFoundException(id));
    }

    @Override
    public List<CreneauBO> getByPraticienId(Long praticienId) {
        log.info("Récupération des créneaux du praticien id={}", praticienId);
        List<CreneauBO> result = creneauRepository.findByPraticienId(praticienId);
        log.info("{} créneau(x) trouvé(s) pour praticien id={}", result.size(), praticienId);
        return result;
    }

    @Override
    public List<CreneauBO> getByPraticienIdAndStatut(Long praticienId, StatutCreneau statut) {
        log.info("Récupération des créneaux du praticien id={} avec statut={}", praticienId, statut);
        return creneauRepository.findByPraticienIdAndStatut(praticienId, statut);
    }

    @Override
    public List<CreneauBO> getDisponiblesByPeriode(LocalDateTime debut, LocalDateTime fin) {
        log.info("Recherche de créneaux disponibles entre {} et {}", debut, fin);
        List<CreneauBO> result = creneauRepository.findDisponiblesByPeriode(debut, fin);
        log.info("{} créneau(x) disponible(s) trouvé(s)", result.size());
        return result;
    }

    @Override
    public CreneauBO create(CreneauBO creneau) {
        log.info("Création d'un créneau pour le praticien id={}", creneau.getPraticien().getId());
        creneau.setStatut(StatutCreneau.DISPONIBLE);
        CreneauBO saved = creneauRepository.save(creneau);
        log.info("Créneau créé avec succès : id={}", saved.getId());
        return saved;
    }

    @Override
    public CreneauBO update(Long id, CreneauBO creneau) {
        log.info("Mise à jour du créneau id={}", id);
        CreneauBO existing = creneauRepository.findById(id)
                .orElseThrow(() -> new CreneauNotFoundException(id));
        creneau.setId(existing.getId());
        creneau.setCreatedAt(existing.getCreatedAt());
        CreneauBO updated = creneauRepository.save(creneau);
        log.info("Créneau mis à jour avec succès : id={}", id);
        return updated;
    }

    @Override
    public CreneauBO updateStatut(Long id, StatutCreneau nouveauStatut) {
        log.info("Changement de statut du créneau id={} vers {}", id, nouveauStatut);
        CreneauBO creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new CreneauNotFoundException(id));
        creneau.setStatut(nouveauStatut);
        CreneauBO updated = creneauRepository.save(creneau);
        log.info("Statut du créneau id={} mis à jour : {}", id, nouveauStatut);
        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression du créneau id={}", id);
        creneauRepository.findById(id)
                .orElseThrow(() -> new CreneauNotFoundException(id));
        creneauRepository.deleteById(id);
        log.info("Créneau supprimé avec succès : id={}", id);
    }
}

