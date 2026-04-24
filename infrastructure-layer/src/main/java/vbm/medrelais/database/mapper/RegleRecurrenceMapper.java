package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.RegleRecurrenceEntity;
import vbm.medrelais.model.RegleRecurrenceBO;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class})
public interface RegleRecurrenceMapper {

    // =========================================================
    // Entity → BO
    // La liste creneaux est ignorée pour éviter la référence circulaire
    // (Creneau → RegleRecurrence → Creneau).
    // Les créneaux sont chargés indépendamment via CreneauMapper.
    // =========================================================

    @Mapping(target = "creneaux", ignore = true)
    RegleRecurrenceBO toBO(RegleRecurrenceEntity entity);

    // =========================================================
    // BO → Entity
    // =========================================================

    @Mapping(target = "creneaux", ignore = true)
    RegleRecurrenceEntity toEntity(RegleRecurrenceBO bo);
}

