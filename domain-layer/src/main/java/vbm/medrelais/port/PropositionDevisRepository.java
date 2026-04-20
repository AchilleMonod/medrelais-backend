package vbm.medrelais.port;

import vbm.medrelais.model.PropositionDevisBO;

import java.util.List;

public interface PropositionDevisRepository {

    PropositionDevisBO getPropositionDevisById(Long id);

    List<PropositionDevisBO> getAllPropositionDevis();

    List<PropositionDevisBO> getAllPropositionDevisByBureauEtudeId(Long id);

    List<PropositionDevisBO> getAllPropositionDevisByDemandeDevisId(Long id);

    void createPropositionDevis(PropositionDevisBO propositionDevisBO);

    void updatePropositionDevis(PropositionDevisBO propositionDevisBO);

    void deletePropositionDevis(Long id);
}
