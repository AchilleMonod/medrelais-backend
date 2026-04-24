package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.AttributionDao;
import vbm.medrelais.database.entities.enums.StatutAttribution;
import vbm.medrelais.database.mapper.AttributionMapper;
import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.port.AttributionRepository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class AttributionRepositoryAdapter implements AttributionRepository {

    private final AttributionDao attributionDao;
    private final AttributionMapper attributionMapper;

    @Override
    public Optional<AttributionBO> findById(Long id) {
        return attributionDao.findById(id)
                .map(attributionMapper::toBO);
    }

    @Override
    public List<AttributionBO> findByCreneauId(Long creneauId) {
        return attributionDao.findByCreneauId(creneauId).stream()
                .map(attributionMapper::toBO)
                .toList();
    }

    @Override
    public List<AttributionBO> findByRemplacantId(Long remplacantId) {
        return attributionDao.findByRemplacantId(remplacantId).stream()
                .map(attributionMapper::toBO)
                .toList();
    }

    @Override
    public List<AttributionBO> findByRemplacantIdAndStatut(Long remplacantId, vbm.medrelais.model.enums.StatutAttribution statut) {
        return attributionDao.findByRemplacantIdAndStatut(remplacantId, StatutAttribution.valueOf(statut.name())).stream()
                .map(attributionMapper::toBO)
                .toList();
    }

    @Override
    public boolean existsByCreneauIdAndRemplacantIdAndStatutIn(
            Long creneauId,
            Long remplacantId,
            List<vbm.medrelais.model.enums.StatutAttribution> statuts) {
        List<StatutAttribution> entityStatuts = statuts.stream()
                .map(s -> StatutAttribution.valueOf(s.name()))
                .toList();
        return attributionDao.existsByCreneauIdAndRemplacantIdAndStatutIn(creneauId, remplacantId, entityStatuts);
    }

    @Override
    public AttributionBO save(AttributionBO attribution) {
        return attributionMapper.toBO(
                attributionDao.save(attributionMapper.toEntity(attribution))
        );
    }

    @Override
    public void deleteById(Long id) {
        attributionDao.deleteById(id);
    }
}

