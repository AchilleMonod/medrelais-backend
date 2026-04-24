package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.CreneauEntity;
import vbm.medrelais.model.CreneauBO;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class, RegleRecurrenceMapper.class})
public interface CreneauMapper {

    // =========================================================
    // Entity → BO
    // La liste attributions est ignorée pour éviter la référence circulaire
    // (Attribution → Creneau → Attribution).
    // Les attributions sont chargées indépendamment via AttributionMapper.
    // =========================================================

    @Mapping(target = "attributions", ignore = true)
    CreneauBO toBO(CreneauEntity entity);

    // =========================================================
    // BO → Entity
    // =========================================================

    @Mapping(target = "attributions", ignore = true)
    CreneauEntity toEntity(CreneauBO bo);
}

