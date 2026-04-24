package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.AttributionNotFoundException;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.enums.StatutAttribution;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.port.AttributionRepository;
import vbm.medrelais.port.CreneauRepository;
import vbm.medrelais.service.AttributionService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AttributionServiceImpl implements AttributionService {

    private final AttributionRepository attributionRepository;
    private final CreneauRepository creneauRepository;

    @Override
    public AttributionBO getById(Long id) {
        log.info("Récupération de l'attribution id={}", id);
        return attributionRepository.findById(id)
                .orElseThrow(() -> new AttributionNotFoundException(id));
    }

    @Override
    public List<AttributionBO> getByCreneauId(Long creneauId) {
        log.info("Récupération des attributions pour le créneau id={}", creneauId);
        return attributionRepository.findByCreneauId(creneauId);
    }

    @Override
    public List<AttributionBO> getByRemplacantId(Long remplacantId) {
        log.info("Récupération des attributions du remplaçant id={}", remplacantId);
        return attributionRepository.findByRemplacantId(remplacantId);
    }

    @Override
    public List<AttributionBO> getByRemplacantIdAndStatut(Long remplacantId, StatutAttribution statut) {
        log.info("Récupération des attributions du remplaçant id={} avec statut={}", remplacantId, statut);
        return attributionRepository.findByRemplacantIdAndStatut(remplacantId, statut);
    }

    @Override
    public AttributionBO demanderRemplacement(AttributionBO attribution) {
        Long creneauId    = attribution.getCreneau().getId();
        Long remplacantId = attribution.getRemplacant().getId();
        log.info("Demande de remplacement : créneau id={}, remplaçant id={}", creneauId, remplacantId);

        // Vérifier que le créneau existe et est disponible
        CreneauBO creneau = creneauRepository.findById(creneauId)
                .orElseThrow(() -> new CreneauNotFoundException(creneauId));
        if (creneau.getStatut() != StatutCreneau.DISPONIBLE) {
            throw new IllegalStateException("Le créneau id=" + creneauId + " n'est plus disponible (statut=" + creneau.getStatut() + ")");
        }

        // Vérifier l'absence de doublon (demande déjà en attente ou acceptée)
        boolean dejaCandidat = attributionRepository.existsByCreneauIdAndRemplacantIdAndStatutIn(
                creneauId, remplacantId,
                List.of(StatutAttribution.EN_ATTENTE, StatutAttribution.ACCEPTEE)
        );
        if (dejaCandidat) {
            throw new IllegalStateException("Le remplaçant id=" + remplacantId + " a déjà une demande active sur ce créneau");
        }

        attribution.setStatut(StatutAttribution.EN_ATTENTE);
        AttributionBO saved = attributionRepository.save(attribution);

        // Passer le créneau en attente
        creneau.setStatut(StatutCreneau.EN_ATTENTE);
        creneauRepository.save(creneau);

        log.info("Demande de remplacement créée : attribution id={}", saved.getId());
        return saved;
    }

    @Override
    public AttributionBO accepter(Long attributionId) {
        log.info("Acceptation de l'attribution id={}", attributionId);
        AttributionBO attribution = attributionRepository.findById(attributionId)
                .orElseThrow(() -> new AttributionNotFoundException(attributionId));

        if (attribution.getStatut() != StatutAttribution.EN_ATTENTE) {
            throw new IllegalStateException("Seule une attribution EN_ATTENTE peut être acceptée (statut actuel=" + attribution.getStatut() + ")");
        }

        // Accepter cette attribution
        attribution.setStatut(StatutAttribution.ACCEPTEE);
        AttributionBO accepted = attributionRepository.save(attribution);

        // Refuser toutes les autres demandes en attente sur le même créneau
        Long creneauId = attribution.getCreneau().getId();
        attributionRepository.findByCreneauId(creneauId).stream()
                .filter(a -> !a.getId().equals(attributionId))
                .filter(a -> a.getStatut() == StatutAttribution.EN_ATTENTE)
                .forEach(a -> {
                    a.setStatut(StatutAttribution.REFUSEE);
                    attributionRepository.save(a);
                });

        // Marquer le créneau comme attribué
        creneauRepository.findById(creneauId).ifPresent(c -> {
            c.setStatut(StatutCreneau.ATTRIBUE);
            creneauRepository.save(c);
        });

        log.info("Attribution id={} acceptée, créneau id={} marqué ATTRIBUE", attributionId, creneauId);
        return accepted;
    }

    @Override
    public AttributionBO refuser(Long attributionId) {
        log.info("Refus de l'attribution id={}", attributionId);
        AttributionBO attribution = attributionRepository.findById(attributionId)
                .orElseThrow(() -> new AttributionNotFoundException(attributionId));

        if (attribution.getStatut() != StatutAttribution.EN_ATTENTE) {
            throw new IllegalStateException("Seule une attribution EN_ATTENTE peut être refusée (statut actuel=" + attribution.getStatut() + ")");
        }

        attribution.setStatut(StatutAttribution.REFUSEE);
        AttributionBO refused = attributionRepository.save(attribution);

        // Si plus aucune demande en attente sur ce créneau, le remettre DISPONIBLE
        Long creneauId = attribution.getCreneau().getId();
        boolean autresEnAttente = attributionRepository.findByCreneauId(creneauId).stream()
                .anyMatch(a -> a.getStatut() == StatutAttribution.EN_ATTENTE);
        if (!autresEnAttente) {
            creneauRepository.findById(creneauId).ifPresent(c -> {
                c.setStatut(StatutCreneau.DISPONIBLE);
                creneauRepository.save(c);
            });
        }

        log.info("Attribution id={} refusée", attributionId);
        return refused;
    }

    @Override
    public AttributionBO annuler(Long attributionId) {
        log.info("Annulation de l'attribution id={}", attributionId);
        AttributionBO attribution = attributionRepository.findById(attributionId)
                .orElseThrow(() -> new AttributionNotFoundException(attributionId));

        if (attribution.getStatut() == StatutAttribution.REFUSEE) {
            throw new IllegalStateException("Une attribution déjà refusée ne peut pas être annulée");
        }

        StatutAttribution ancienStatut = attribution.getStatut();
        attribution.setStatut(StatutAttribution.ANNULEE);
        AttributionBO cancelled = attributionRepository.save(attribution);

        // Si elle était acceptée, remettre le créneau disponible
        if (ancienStatut == StatutAttribution.ACCEPTEE) {
            Long creneauId = attribution.getCreneau().getId();
            creneauRepository.findById(creneauId).ifPresent(c -> {
                c.setStatut(StatutCreneau.DISPONIBLE);
                creneauRepository.save(c);
            });
            log.info("Attribution acceptée annulée — créneau id={} remis DISPONIBLE", creneauId);
        }

        log.info("Attribution id={} annulée", attributionId);
        return cancelled;
    }
}

