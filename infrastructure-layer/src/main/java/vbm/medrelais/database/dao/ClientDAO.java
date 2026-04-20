package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.ClientEntity;


public interface ClientDAO extends JpaRepository<ClientEntity, Long> {

}
