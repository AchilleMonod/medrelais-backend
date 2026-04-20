package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.PropositionDevisServiceException;
import vbm.medrelais.model.PropositionDevisBO;
import vbm.medrelais.port.PropositionDevisRepository;
import vbm.medrelais.service.PropositionDevisService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PropositionDevisServiceImpl implements PropositionDevisService {

    private final PropositionDevisRepository propositionDevisRepository;

    @Override
    public List<PropositionDevisBO> getAllPropositionDevis() {
        log.info("Récupération de toutes les propositions de devis");
        List<PropositionDevisBO> result = propositionDevisRepository.getAllPropositionDevis();
        log.info("{} proposition(s) de devis trouvée(s)", result.size());
        return result;
    }

    @Override
    public PropositionDevisBO getPropositionDevisById(Long id) {
        log.info("Récupération de la proposition de devis id={}", id);
        PropositionDevisBO result = propositionDevisRepository.getPropositionDevisById(id);
        if (result == null) {
            log.warn("Aucune proposition de devis trouvée pour id={}", id);
        }
        return result;
    }

    @Override
    public List<PropositionDevisBO> getAllPropositionDevisByBureauEtudeId(Long id) {
        log.info("Récupération des propositions de devis pour le bureau d'études id={}", id);
        List<PropositionDevisBO> result = propositionDevisRepository.getAllPropositionDevisByBureauEtudeId(id);
        log.info("{} proposition(s) de devis trouvée(s) pour le bureau d'études id={}", result.size(), id);
        return result;
    }

    @Override
    public List<PropositionDevisBO> getAllPropositionDevisByDemandeDevisId(Long id) {
        log.info("Récupération des propositions de devis pour la demande id={}", id);
        List<PropositionDevisBO> result = propositionDevisRepository.getAllPropositionDevisByDemandeDevisId(id);
        log.info("{} proposition(s) de devis trouvée(s) pour la demande id={}", result.size(), id);
        return result;
    }

    @Override
    public void createPropositionDevis(PropositionDevisBO propositionDevisBO) {
        log.info("Création d'une proposition de devis : prix={}, dateRendu={}", propositionDevisBO.getPrix(), propositionDevisBO.getDateRendu());
        propositionDevisRepository.createPropositionDevis(propositionDevisBO);
        log.info("Proposition de devis créée avec succès");
    }

    @Override
    public void updatePropositionDevis(PropositionDevisBO propositionDevisBO) {
        Long id = propositionDevisBO.getId();
        log.info("Mise à jour de la proposition de devis id={}", id);
        if (propositionDevisRepository.getPropositionDevisById(id) == null) {
            log.warn("Tentative de mise à jour d'une proposition de devis inexistante id={}", id);
            throw new PropositionDevisServiceException("Proposition de devis introuvable avec l'id : " + id);
        }
        propositionDevisRepository.updatePropositionDevis(propositionDevisBO);
        log.info("Proposition de devis mise à jour avec succès id={}", id);
    }

    @Override
    public void deletePropositionDevis(Long id) {
        log.info("Suppression de la proposition de devis id={}", id);
        if (propositionDevisRepository.getPropositionDevisById(id) == null) {
            log.warn("Tentative de suppression d'une proposition de devis inexistante id={}", id);
            throw new PropositionDevisServiceException("Proposition de devis introuvable avec l'id : " + id);
        }
        propositionDevisRepository.deletePropositionDevis(id);
        log.info("Proposition de devis supprimée avec succès id={}", id);
    }
}
