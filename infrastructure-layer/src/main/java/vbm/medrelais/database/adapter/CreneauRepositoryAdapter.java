package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vbm.medrelais.database.dao.CreneauDao;
import vbm.medrelais.database.entities.enums.StatutCreneau;
import vbm.medrelais.database.mapper.CreneauMapper;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.port.CreneauRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CreneauRepositoryAdapter implements CreneauRepository {

    private final CreneauDao creneauDao;
    private final CreneauMapper creneauMapper;

    @Override
    public Optional<CreneauBO> findById(Long id) {
        return creneauDao.findById(id)
                .map(creneauMapper::toBO);
    }

    @Override
    public List<CreneauBO> findByPraticienId(Long praticienId) {
        return creneauDao.findByPraticienId(praticienId).stream()
                .map(creneauMapper::toBO)
                .toList();
    }

    @Override
    public List<CreneauBO> findByPraticienIdAndStatut(Long praticienId, vbm.medrelais.model.enums.StatutCreneau statut) {
        return creneauDao.findByPraticienIdAndStatut(praticienId, StatutCreneau.valueOf(statut.name())).stream()
                .map(creneauMapper::toBO)
                .toList();
    }

    @Override
    public List<CreneauBO> findDisponiblesByPeriode(LocalDateTime debut, LocalDateTime fin) {
        return creneauDao.findDisponiblesByPeriode(debut, fin).stream()
                .map(creneauMapper::toBO)
                .toList();
    }

    @Override
    public List<CreneauBO> findByRegleRecurrenceId(Long regleRecurrenceId) {
        return creneauDao.findByRegleRecurrenceId(regleRecurrenceId).stream()
                .map(creneauMapper::toBO)
                .toList();
    }

    @Override
    public CreneauBO save(CreneauBO creneau) {
        return creneauMapper.toBO(
                creneauDao.save(creneauMapper.toEntity(creneau))
        );
    }

    @Override
    public List<CreneauBO> saveAll(List<CreneauBO> creneaux) {
        return creneauDao.saveAll(
                creneaux.stream().map(creneauMapper::toEntity).toList()
        ).stream().map(creneauMapper::toBO).toList();
    }

    @Override
    public void deleteById(Long id) {
        creneauDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByRegleRecurrenceId(Long regleRecurrenceId) {
        creneauDao.deleteByRegleRecurrenceId(regleRecurrenceId);
    }
}

