package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.EtudeEntity;

import java.util.List;

public interface EtudeDAO extends JpaRepository<EtudeEntity, Long> {

    List<EtudeEntity> findByBureauEtudeId(Long bureauEtudeId);

    List<EtudeEntity> findByClientId(Long clientId);
}
