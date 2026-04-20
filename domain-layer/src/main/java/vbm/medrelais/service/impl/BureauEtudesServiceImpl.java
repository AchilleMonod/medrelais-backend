package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.BureauEtudesServiceException;
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.port.BureauEtudeRepository;
import vbm.medrelais.service.BureauEtudesService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BureauEtudesServiceImpl implements BureauEtudesService {

    private final BureauEtudeRepository bureauEtudeRepository;

    @Override
    public List<BureauEtudesBO> getAllBureauEtude() {
        log.info("Récupération de tous les bureaux d'études");
        List<BureauEtudesBO> result = bureauEtudeRepository.getAllBureauEtude();
        log.info("{} bureau(x) d'études trouvé(s)", result.size());
        return result;
    }

    @Override
    public BureauEtudesBO getBureauEtudeByID(Long id) {
        log.info("Récupération du bureau d'études id={}", id);
        BureauEtudesBO result = bureauEtudeRepository.getBureauEtudeByID(id);
        if (result == null) {
            log.warn("Aucun bureau d'études trouvé pour id={}", id);
        }
        return result;
    }

    @Override
    public void createBureauEtude(BureauEtudesBO bureauEtudesBO) {
        log.info("Création d'un bureau d'études : raisonSociale={}", bureauEtudesBO.getRaisonSociale());
        bureauEtudeRepository.createBureauEtude(bureauEtudesBO);
        log.info("Bureau d'études créé avec succès : raisonSociale={}", bureauEtudesBO.getRaisonSociale());
    }

    @Override
    public void updateBureauEtude(BureauEtudesBO bureauEtudesBO) {
        log.info("Mise à jour du bureau d'études id={}", bureauEtudesBO.getId());
        if (bureauEtudeRepository.getBureauEtudeByID(bureauEtudesBO.getId()) == null) {
            log.warn("Tentative de mise à jour d'un bureau d'études inexistant id={}", bureauEtudesBO.getId());
            throw new BureauEtudesServiceException("Bureau d'études introuvable avec l'id : " + bureauEtudesBO.getId());
        }
        bureauEtudeRepository.updateBureauEtude(bureauEtudesBO);
        log.info("Bureau d'études mis à jour avec succès id={}", bureauEtudesBO.getId());
    }

    @Override
    public void deleteBureauEtude(Long id) {
        log.info("Suppression du bureau d'études id={}", id);
        if (bureauEtudeRepository.getBureauEtudeByID(id) == null) {
            log.warn("Tentative de suppression d'un bureau d'études inexistant id={}", id);
            throw new BureauEtudesServiceException("Bureau d'études introuvable avec l'id : " + id);
        }
        bureauEtudeRepository.deleteBureauEtude(id);
        log.info("Bureau d'études supprimé avec succès id={}", id);
    }
}

