package vbm.medrelais.port;

import vbm.medrelais.model.BureauEtudesBO;

import java.util.List;

public interface BureauEtudeRepository {
    List<BureauEtudesBO> getAllBureauEtude();
    BureauEtudesBO getBureauEtudeByID(Long id);
    void createBureauEtude(BureauEtudesBO bureauEtudesBO);
    void updateBureauEtude(BureauEtudesBO bureauEtudesBO);
    void deleteBureauEtude(Long id);
}
