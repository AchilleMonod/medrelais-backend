package vbm.medrelais.port;

import vbm.medrelais.model.EtudeBO;

import java.util.List;

public interface EtudeRepository {

    List<EtudeBO> getAllEtudes();

    EtudeBO getEtudeById(Long id);

    List<EtudeBO> getAllEtudesByBureauEtudeId(Long id);

    List<EtudeBO> getAllEtudesByClientId(Long id);

    void createEtude(EtudeBO etudeBO);

    void updateEtude(EtudeBO etudeBO);

    void deleteEtude(Long id);
}
