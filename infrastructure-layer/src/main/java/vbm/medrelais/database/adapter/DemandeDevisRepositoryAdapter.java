package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.DemandeDevisDAO;
import vbm.medrelais.database.entities.DemandeDevisEntity;
import vbm.medrelais.database.mapper.DemandeDevisEntityMapper;
import vbm.medrelais.model.DemandeDevisBO;
import vbm.medrelais.port.DemandeDevisRepository;

import java.util.List;

@Repository
@AllArgsConstructor
public class DemandeDevisRepositoryAdapter implements DemandeDevisRepository {

    private final DemandeDevisDAO demandeDevisDAO;
    private final DemandeDevisEntityMapper demandeDevisEntityMapper;

    @Override
    public List<DemandeDevisBO> getAllDemandeDevis() {
        return demandeDevisDAO.findAll().stream()
                .map(demandeDevisEntityMapper::toBO)
                .toList();
    }

    @Override
    public DemandeDevisBO getDemandeDevisById(Long id) {
        return demandeDevisDAO.findById(id)
                .map(demandeDevisEntityMapper::toBO)
                .orElse(null);
    }

    @Override
    public List<DemandeDevisBO> getAllDemandeDevisByClientId(Long id) {
        return demandeDevisDAO.findByClientId(id).stream()
                .map(demandeDevisEntityMapper::toBO)
                .toList();
    }

    @Override
    public void createDemandeDevis(DemandeDevisBO demandeDevisBO) {
        persist(demandeDevisBO);
    }

    @Override
    public void updateDemandeDevis(DemandeDevisBO demandeDevisBO) {
        persist(demandeDevisBO);
    }

    @Override
    public void deleteDemandeDevis(Long id) {
        demandeDevisDAO.deleteById(id);
    }

    private void persist(DemandeDevisBO demandeDevisBO) {
        DemandeDevisEntity entity = demandeDevisEntityMapper.toEntity(demandeDevisBO);
        demandeDevisDAO.save(entity);
    }
}
