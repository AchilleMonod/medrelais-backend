package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.webapp.model.request.AttributionRequestDTO;
import vbm.medrelais.webapp.model.response.AttributionResponseDTO;

@Mapper(componentModel = "spring", uses = {CreneauDtoMapper.class, PraticienDtoMapper.class})
public interface AttributionDtoMapper {

    // =========================================================
    // Request DTO → BO
    // creneauId et remplacantId sont des stubs résolus par le service
    // =========================================================

    @Mapping(source = "creneauId",    target = "creneau.id")
    @Mapping(source = "remplacantId", target = "remplacant.id")
    @Mapping(target = "statut",    ignore = true)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AttributionBO toBO(AttributionRequestDTO dto);

    // =========================================================
    // BO → Response DTO
    // =========================================================

    @Mapping(source = "creneau",    target = "creneau")
    @Mapping(source = "remplacant", target = "remplacant")
    AttributionResponseDTO toResponseDTO(AttributionBO bo);
}

