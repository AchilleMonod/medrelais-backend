package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.webapp.model.request.CreneauRequestDTO;
import vbm.medrelais.webapp.model.response.CreneauResponseDTO;

@Mapper(componentModel = "spring", uses = {PraticienDtoMapper.class})
public interface CreneauDtoMapper {

    // =========================================================
    // Request DTO → BO
    // Le praticien est résolu par le service à partir du praticienId
    // =========================================================

    @Mapping(source = "praticienId", target = "praticien.id")
    @Mapping(target = "statut",           ignore = true)
    @Mapping(target = "regleRecurrence",  ignore = true)
    @Mapping(target = "estException",     ignore = true)
    @Mapping(target = "attributions",     ignore = true)
    @Mapping(target = "id",               ignore = true)
    @Mapping(target = "createdAt",        ignore = true)
    @Mapping(target = "updatedAt",        ignore = true)
    CreneauBO toBO(CreneauRequestDTO dto);

    // =========================================================
    // BO → Response DTO
    // praticien : PraticienBO → PraticienSummaryDTO via PraticienDtoMapper
    // regleRecurrenceId : extrait de l'objet imbriqué
    // =========================================================

    @Mapping(source = "regleRecurrence.id", target = "regleRecurrenceId")
    @Mapping(source = "praticien",          target = "praticien")
    CreneauResponseDTO toResponseDTO(CreneauBO bo);
}


