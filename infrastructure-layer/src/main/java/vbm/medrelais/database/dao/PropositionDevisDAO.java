package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.PropositionDevisEntity;

import java.util.List;

public interface PropositionDevisDAO extends JpaRepository<PropositionDevisEntity, Long> {

    List<PropositionDevisEntity> findByBureauEtudeId(Long bureauEtudeId);

    List<PropositionDevisEntity> findByDemandeDevisId(Long demandeDevisId);
}
