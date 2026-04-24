package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.AttributionEntity;
import vbm.medrelais.model.AttributionBO;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class, CreneauMapper.class})
public interface AttributionMapper {

    // =========================================================
    // Entity → BO
    // On brise le cycle Attribution → Creneau → [attributions, regleRecurrence.creneaux]
    // en ignorant ces listes dans le creneau imbriqué.
    // =========================================================

    @Mapping(target = "creneau.attributions",              ignore = true)
    @Mapping(target = "creneau.regleRecurrence.creneaux",  ignore = true)
    AttributionBO toBO(AttributionEntity entity);

    // =========================================================
    // BO → Entity
    // =========================================================

    @Mapping(target = "creneau.attributions",              ignore = true)
    @Mapping(target = "creneau.regleRecurrence.creneaux",  ignore = true)
    AttributionEntity toEntity(AttributionBO bo);
}

