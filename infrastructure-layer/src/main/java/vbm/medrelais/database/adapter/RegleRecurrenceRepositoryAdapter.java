package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.RegleRecurrenceDao;
import vbm.medrelais.database.mapper.RegleRecurrenceMapper;
import vbm.medrelais.model.RegleRecurrenceBO;
import vbm.medrelais.port.RegleRecurrenceRepository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class RegleRecurrenceRepositoryAdapter implements RegleRecurrenceRepository {

    private final RegleRecurrenceDao regleRecurrenceDao;
    private final RegleRecurrenceMapper regleRecurrenceMapper;

    @Override
    public Optional<RegleRecurrenceBO> findById(Long id) {
        return regleRecurrenceDao.findById(id)
                .map(regleRecurrenceMapper::toBO);
    }

    @Override
    public List<RegleRecurrenceBO> findByPraticienId(Long praticienId) {
        return regleRecurrenceDao.findByPraticienId(praticienId).stream()
                .map(regleRecurrenceMapper::toBO)
                .toList();
    }

    @Override
    public List<RegleRecurrenceBO> findActivesByPraticienId(Long praticienId) {
        return regleRecurrenceDao.findByPraticienIdAndActiveTrue(praticienId).stream()
                .map(regleRecurrenceMapper::toBO)
                .toList();
    }

    @Override
    public RegleRecurrenceBO save(RegleRecurrenceBO regle) {
        return regleRecurrenceMapper.toBO(
                regleRecurrenceDao.save(regleRecurrenceMapper.toEntity(regle))
        );
    }

    @Override
    public void deleteById(Long id) {
        regleRecurrenceDao.deleteById(id);
    }
}

