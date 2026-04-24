package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.UtilisateurDao;
import vbm.medrelais.database.mapper.UtilisateurMapper;
import vbm.medrelais.model.UtilisateurBO;
import vbm.medrelais.port.UtilisateurRepository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UtilisateurRepositoryAdapter implements UtilisateurRepository {

    private final UtilisateurDao utilisateurDao;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    public Optional<UtilisateurBO> findById(Long id) {
        return utilisateurDao.findById(id)
                .map(utilisateurMapper::toBO);
    }

    @Override
    public Optional<UtilisateurBO> findByEmail(String email) {
        return utilisateurDao.findByEmail(email)
                .map(utilisateurMapper::toBO);
    }

    @Override
    public boolean existsByEmail(String email) {
        return utilisateurDao.existsByEmail(email);
    }

    @Override
    public UtilisateurBO save(UtilisateurBO utilisateur) {
        return utilisateurMapper.toBO(
                utilisateurDao.save(utilisateurMapper.toEntity(utilisateur))
        );
    }

    @Override
    public void deleteById(Long id) {
        utilisateurDao.deleteById(id);
    }
}

