package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.EtudeServiceException;
import vbm.medrelais.model.EtudeBO;
import vbm.medrelais.port.EtudeRepository;
import vbm.medrelais.service.EtudeService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class EtudeServiceImpl implements EtudeService {

    private final EtudeRepository etudeRepository;

    @Override
    public List<EtudeBO> getAllEtudes() {
        log.info("Récupération de toutes les études");
        List<EtudeBO> result = etudeRepository.getAllEtudes();
        log.info("{} étude(s) trouvée(s)", result.size());
        return result;
    }

    @Override
    public EtudeBO getEtudeById(Long id) {
        log.info("Récupération de l'étude id={}", id);
        EtudeBO result = etudeRepository.getEtudeById(id);
        if (result == null) {
            log.warn("Aucune étude trouvée pour id={}", id);
        }
        return result;
    }

    @Override
    public List<EtudeBO> getAllEtudesByBureauEtudeId(Long id) {
        log.info("Récupération des études pour le bureau d'études id={}", id);
        List<EtudeBO> result = etudeRepository.getAllEtudesByBureauEtudeId(id);
        log.info("{} étude(s) trouvée(s) pour le bureau d'études id={}", result.size(), id);
        return result;
    }

    @Override
    public List<EtudeBO> getAllEtudesByClientId(Long id) {
        log.info("Récupération des études pour le client id={}", id);
        List<EtudeBO> result = etudeRepository.getAllEtudesByClientId(id);
        log.info("{} étude(s) trouvée(s) pour le client id={}", result.size(), id);
        return result;
    }

    @Override
    public void createEtude(EtudeBO etudeBO) {
        log.info("Création d'une étude : etat={}", etudeBO.getEtat());
        etudeRepository.createEtude(etudeBO);
        log.info("Étude créée avec succès");
    }

    @Override
    public void updateEtude(EtudeBO etudeBO) {
        Long id = etudeBO.getId();
        log.info("Mise à jour de l'étude id={}", id);
        if (etudeRepository.getEtudeById(id) == null) {
            log.warn("Tentative de mise à jour d'une étude inexistante id={}", id);
            throw new EtudeServiceException("Étude introuvable avec l'id : " + id);
        }
        etudeRepository.updateEtude(etudeBO);
        log.info("Étude mise à jour avec succès id={}", id);
    }

    @Override
    public void deleteEtude(Long id) {
        log.info("Suppression de l'étude id={}", id);
        if (etudeRepository.getEtudeById(id) == null) {
            log.warn("Tentative de suppression d'une étude inexistante id={}", id);
            throw new EtudeServiceException("Étude introuvable avec l'id : " + id);
        }
        etudeRepository.deleteEtude(id);
        log.info("Étude supprimée avec succès id={}", id);
    }
}
