package vbm.medrelais.service;

import vbm.medrelais.model.PropositionDevisBO;

import java.util.List;

public interface PropositionDevisService {

    List<PropositionDevisBO> getAllPropositionDevis();
    PropositionDevisBO getPropositionDevisById(Long id);
    List<PropositionDevisBO> getAllPropositionDevisByBureauEtudeId(Long id);
    List<PropositionDevisBO> getAllPropositionDevisByDemandeDevisId(Long id);
    void createPropositionDevis(PropositionDevisBO propositionDevisBO);
    void updatePropositionDevis(PropositionDevisBO propositionDevisBO);
    void deletePropositionDevis(Long id);
}
