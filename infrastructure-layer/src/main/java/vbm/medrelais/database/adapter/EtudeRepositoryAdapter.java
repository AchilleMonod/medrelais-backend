package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.EtudeDAO;
import vbm.medrelais.database.entities.EtudeEntity;
import vbm.medrelais.database.mapper.EtudeEntityMapper;
import vbm.medrelais.model.EtudeBO;
import vbm.medrelais.port.EtudeRepository;

import java.util.List;

@Repository
@AllArgsConstructor
public class EtudeRepositoryAdapter implements EtudeRepository {

    private final EtudeDAO etudeDAO;
    private final EtudeEntityMapper etudeEntityMapper;

    @Override
    public List<EtudeBO> getAllEtudes() {
        return etudeDAO.findAll().stream()
                .map(etudeEntityMapper::toBO)
                .toList();
    }

    @Override
    public EtudeBO getEtudeById(Long id) {
        return etudeDAO.findById(id)
                .map(etudeEntityMapper::toBO)
                .orElse(null);
    }

    @Override
    public List<EtudeBO> getAllEtudesByBureauEtudeId(Long id) {
        return etudeDAO.findByBureauEtudeId(id).stream()
                .map(etudeEntityMapper::toBO)
                .toList();
    }

    @Override
    public List<EtudeBO> getAllEtudesByClientId(Long id) {
        return etudeDAO.findByClientId(id).stream()
                .map(etudeEntityMapper::toBO)
                .toList();
    }

    @Override
    public void createEtude(EtudeBO etudeBO) {
        persist(etudeBO);
    }

    @Override
    public void updateEtude(EtudeBO etudeBO) {
        persist(etudeBO);
    }

    @Override
    public void deleteEtude(Long id) {
        etudeDAO.deleteById(id);
    }

    private void persist(EtudeBO etudeBO) {
        EtudeEntity entity = etudeEntityMapper.toEntity(etudeBO);
        etudeDAO.save(entity);
    }
}
