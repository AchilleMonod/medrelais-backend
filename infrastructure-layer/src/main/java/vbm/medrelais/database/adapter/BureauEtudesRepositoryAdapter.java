package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.BureauEtudesDAO;
import vbm.medrelais.database.entities.BureauEtudesEntity;
import vbm.medrelais.database.mapper.BureauEtudeEntityMapper;
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.port.BureauEtudeRepository;

import java.util.List;

@Repository
@AllArgsConstructor
public class BureauEtudesRepositoryAdapter implements BureauEtudeRepository {

    private final BureauEtudesDAO bureauEtudesDAO;

    private final BureauEtudeEntityMapper bureauEtudeEntityMapper;

    @Override
    public List<BureauEtudesBO> getAllBureauEtude() {
        return bureauEtudesDAO.findAll().stream()
                .map(bureauEtudeEntityMapper::toBO)
                .toList();
    }

    @Override
    public BureauEtudesBO getBureauEtudeByID(Long id) {
        return bureauEtudesDAO.findById(id)
                .map(bureauEtudeEntityMapper::toBO)
                .orElse(null);
    }

    @Override
    public void createBureauEtude(BureauEtudesBO bureauEtudesBO) {
        persist(bureauEtudesBO);
    }

    @Override
    public void updateBureauEtude(BureauEtudesBO bureauEtudesBO) {
        persist(bureauEtudesBO);
    }

    @Override
    public void deleteBureauEtude(Long id) {
        bureauEtudesDAO.deleteById(id);
    }

    private void persist(BureauEtudesBO bureauEtudesBO) {
        BureauEtudesEntity entity = bureauEtudeEntityMapper.toEntity(bureauEtudesBO);
        bureauEtudesDAO.save(entity);
    }
}

