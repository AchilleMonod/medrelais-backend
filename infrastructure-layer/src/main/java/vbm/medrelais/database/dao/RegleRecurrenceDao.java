package vbm.medrelais.database.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vbm.medrelais.database.entities.RegleRecurrenceEntity;

import java.util.List;

public interface RegleRecurrenceDao extends JpaRepository<RegleRecurrenceEntity, Long> {

    List<RegleRecurrenceEntity> findByPraticienId(Long praticienId);

    List<RegleRecurrenceEntity> findByPraticienIdAndActiveTrue(Long praticienId);
}

