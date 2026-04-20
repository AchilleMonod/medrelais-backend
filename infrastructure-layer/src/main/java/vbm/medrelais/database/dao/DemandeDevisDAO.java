package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.DemandeDevisEntity;

import java.util.List;

public interface DemandeDevisDAO extends JpaRepository<DemandeDevisEntity, Long> {

    List<DemandeDevisEntity> findByClientId(Long clientId);
}
