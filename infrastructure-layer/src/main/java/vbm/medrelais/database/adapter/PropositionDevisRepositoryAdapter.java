package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.PropositionDevisDAO;
import vbm.medrelais.database.entities.PropositionDevisEntity;
import vbm.medrelais.database.mapper.PropositionDevisEntityMapper;
import vbm.medrelais.model.PropositionDevisBO;
import vbm.medrelais.port.PropositionDevisRepository;

import java.util.List;

@Repository
@AllArgsConstructor
public class PropositionDevisRepositoryAdapter implements PropositionDevisRepository {

    private final PropositionDevisDAO propositionDevisDAO;
    private final PropositionDevisEntityMapper propositionDevisEntityMapper;

    @Override
    public List<PropositionDevisBO> getAllPropositionDevis() {
        return propositionDevisDAO.findAll().stream()
                .map(propositionDevisEntityMapper::toBO)
                .toList();
    }

    @Override
    public PropositionDevisBO getPropositionDevisById(Long id) {
        return propositionDevisDAO.findById(id)
                .map(propositionDevisEntityMapper::toBO)
                .orElse(null);
    }

    @Override
    public List<PropositionDevisBO> getAllPropositionDevisByBureauEtudeId(Long id) {
        return propositionDevisDAO.findByBureauEtudeId(id).stream()
                .map(propositionDevisEntityMapper::toBO)
                .toList();
    }

    @Override
    public List<PropositionDevisBO> getAllPropositionDevisByDemandeDevisId(Long id) {
        return propositionDevisDAO.findByDemandeDevisId(id).stream()
                .map(propositionDevisEntityMapper::toBO)
                .toList();
    }

    @Override
    public void createPropositionDevis(PropositionDevisBO propositionDevisBO) {
        persist(propositionDevisBO);
    }

    @Override
    public void updatePropositionDevis(PropositionDevisBO propositionDevisBO) {
        persist(propositionDevisBO);
    }

    @Override
    public void deletePropositionDevis(Long id) {
        propositionDevisDAO.deleteById(id);
    }

    private void persist(PropositionDevisBO propositionDevisBO) {
        PropositionDevisEntity entity = propositionDevisEntityMapper.toEntity(propositionDevisBO);
        propositionDevisDAO.save(entity);
    }
}
