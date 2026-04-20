package vbm.medrelais.service;


import vbm.medrelais.model.DemandeDevisBO;
import java.util.List;

public interface DemandeDevisService {

    List<DemandeDevisBO> getAllDemandeDevis();
    DemandeDevisBO getDemandeDevisById(Long id);
    List<DemandeDevisBO> getAllDemandeDevisByClientId(Long id);

    void createDemandeDevis(DemandeDevisBO demandeDevisBO);
    void updateDemandeDevis(DemandeDevisBO demandeDevisBO);
    void deleteDemandeDevis(Long id);
}
