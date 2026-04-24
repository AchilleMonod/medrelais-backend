package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.RegleRecurrenceBO;
import vbm.medrelais.webapp.model.request.RegleRecurrenceRequestDTO;
import vbm.medrelais.webapp.model.response.RegleRecurrenceResponseDTO;

@Mapper(componentModel = "spring", uses = {PraticienDtoMapper.class})
public interface RegleRecurrenceDtoMapper {

    // =========================================================
    // Request DTO → BO
    // =========================================================

    @Mapping(source = "praticienId", target = "praticien.id")
    @Mapping(target = "active",    ignore = true)
    @Mapping(target = "creneaux",  ignore = true)
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RegleRecurrenceBO toBO(RegleRecurrenceRequestDTO dto);

    // =========================================================
    // BO → Response DTO
    // =========================================================

    @Mapping(source = "praticien", target = "praticien")
    RegleRecurrenceResponseDTO toResponseDTO(RegleRecurrenceBO bo);
}

