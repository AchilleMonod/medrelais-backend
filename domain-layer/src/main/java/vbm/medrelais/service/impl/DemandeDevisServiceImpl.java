package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.DemandeDevisServiceException;
import vbm.medrelais.model.DemandeDevisBO;
import vbm.medrelais.port.DemandeDevisRepository;
import vbm.medrelais.service.DemandeDevisService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class DemandeDevisServiceImpl implements DemandeDevisService {

    private final DemandeDevisRepository demandeDevisRepository;

    @Override
    public List<DemandeDevisBO> getAllDemandeDevis() {
        log.info("Récupération de toutes les demandes de devis");
        List<DemandeDevisBO> result = demandeDevisRepository.getAllDemandeDevis();
        log.info("{} demande(s) de devis trouvée(s)", result.size());
        return result;
    }

    @Override
    public DemandeDevisBO getDemandeDevisById(Long id) {
        log.info("Récupération de la demande de devis id={}", id);
        DemandeDevisBO result = demandeDevisRepository.getDemandeDevisById(id);
        if (result == null) {
            log.warn("Aucune demande de devis trouvée pour id={}", id);
        }
        return result;
    }

    @Override
    public List<DemandeDevisBO> getAllDemandeDevisByClientId(Long id) {
        log.info("Récupération des demandes de devis pour le client id={}", id);
        List<DemandeDevisBO> result = demandeDevisRepository.getAllDemandeDevisByClientId(id);
        log.info("{} demande(s) de devis trouvée(s) pour le client id={}", result.size(), id);
        return result;
    }

    @Override
    public void createDemandeDevis(DemandeDevisBO demandeDevisBO) {
        log.info("Création d'une demande de devis pour le client id={}", demandeDevisBO.getClient() != null ? demandeDevisBO.getClient().getId() : "inconnu");
        demandeDevisRepository.createDemandeDevis(demandeDevisBO);
        log.info("Demande de devis créée avec succès");
    }

    @Override
    public void updateDemandeDevis(DemandeDevisBO demandeDevisBO) {
        Long id = demandeDevisBO.getId();
        log.info("Mise à jour de la demande de devis id={}", id);
        if (demandeDevisRepository.getDemandeDevisById(id) == null) {
            log.warn("Tentative de mise à jour d'une demande de devis inexistante id={}", id);
            throw new DemandeDevisServiceException("Demande de devis introuvable avec l'id : " + id);
        }
        demandeDevisRepository.updateDemandeDevis(demandeDevisBO);
        log.info("Demande de devis mise à jour avec succès id={}", id);
    }

    @Override
    public void deleteDemandeDevis(Long id) {
        log.info("Suppression de la demande de devis id={}", id);
        if (demandeDevisRepository.getDemandeDevisById(id) == null) {
            log.warn("Tentative de suppression d'une demande de devis inexistante id={}", id);
            throw new DemandeDevisServiceException("Demande de devis introuvable avec l'id : " + id);
        }
        demandeDevisRepository.deleteDemandeDevis(id);
        log.info("Demande de devis supprimée avec succès id={}", id);
    }
}
