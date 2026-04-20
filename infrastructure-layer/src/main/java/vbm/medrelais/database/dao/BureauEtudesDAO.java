package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.BureauEtudesEntity;


public interface BureauEtudesDAO extends JpaRepository<BureauEtudesEntity, Long> {

}
