package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.UserDAO;
import vbm.medrelais.database.mapper.UtilisateurEntityMapper;
import vbm.medrelais.model.UtilisateurBO;
import vbm.medrelais.port.UtilisateurRepository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UtilisateurRepositoryAdapter implements UtilisateurRepository {

    private final UserDAO userDAO;
    private final UtilisateurEntityMapper utilisateurEntityMapper;

    @Override
    public Optional<UtilisateurBO> findByEmail(String email) {
        return userDAO.findByEmail(email)
                .map(utilisateurEntityMapper::toBO);
    }

    @Override
    public UtilisateurBO save(UtilisateurBO user) {
        return utilisateurEntityMapper.toBO(
                userDAO.save(utilisateurEntityMapper.toEntity(user))
        );
    }
}

