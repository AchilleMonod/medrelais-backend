package vbm.medrelais.port;

import vbm.medrelais.model.RegleRecurrenceBO;

import java.util.List;
import java.util.Optional;

public interface RegleRecurrenceRepository {

    Optional<RegleRecurrenceBO> findById(Long id);

    List<RegleRecurrenceBO> findByPraticienId(Long praticienId);

    List<RegleRecurrenceBO> findActivesByPraticienId(Long praticienId);

    RegleRecurrenceBO save(RegleRecurrenceBO regle);

    void deleteById(Long id);
}

