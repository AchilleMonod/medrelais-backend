package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.PraticienDao;
import vbm.medrelais.database.mapper.UtilisateurMapper;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.port.PraticienRepository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PraticienRepositoryAdapter implements PraticienRepository {

    private final PraticienDao praticienDao;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    public Optional<PraticienBO> findById(Long id) {
        return praticienDao.findById(id)
                .map(utilisateurMapper::toPraticienBO);
    }

    @Override
    public List<PraticienBO> findAll() {
        return praticienDao.findAll().stream()
                .map(utilisateurMapper::toPraticienBO)
                .toList();
    }

    @Override
    public List<PraticienBO> findBySpecialite(String specialite) {
        return praticienDao.findBySpecialiteIgnoreCase(specialite).stream()
                .map(utilisateurMapper::toPraticienBO)
                .toList();
    }

    @Override
    public List<PraticienBO> findByVille(String ville) {
        return praticienDao.findByAdresseVilleIgnoreCase(ville).stream()
                .map(utilisateurMapper::toPraticienBO)
                .toList();
    }

    @Override
    public List<PraticienBO> findNearby(double latitude, double longitude, double rayonKm) {
        return praticienDao.findNearby(latitude, longitude, rayonKm).stream()
                .map(utilisateurMapper::toPraticienBO)
                .toList();
    }

    @Override
    public PraticienBO save(PraticienBO praticien) {
        return utilisateurMapper.toPraticienBO(
                praticienDao.save(utilisateurMapper.toPraticienEntity(praticien))
        );
    }

    @Override
    public void deleteById(Long id) {
        praticienDao.deleteById(id);
    }
}

