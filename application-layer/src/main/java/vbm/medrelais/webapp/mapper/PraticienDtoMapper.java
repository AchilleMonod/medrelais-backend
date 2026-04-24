package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.UtilisateurBO;
import vbm.medrelais.webapp.model.request.PraticienRequestDTO;
import vbm.medrelais.webapp.model.response.PraticienResponseDTO;
import vbm.medrelais.webapp.model.response.PraticienSummaryDTO;

@Mapper(componentModel = "spring")
public interface PraticienDtoMapper {

    // =========================================================
    // Request DTO → BO
    // =========================================================

    /** Le password sera encodé par le service avant persistance */
    @Mapping(target = "id",        ignore = true)
    @Mapping(target = "role",      ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PraticienBO toBO(PraticienRequestDTO dto);

    // =========================================================
    // BO → Response DTO
    // =========================================================

    PraticienResponseDTO toResponseDTO(PraticienBO bo);

    @Mapping(source = "adresseVille", target = "adresseVille")
    PraticienSummaryDTO toSummaryDTO(PraticienBO bo);

    /** Conversion générique depuis UtilisateurBO (pour les remplaçants dans AttributionResponseDTO) */
    default PraticienSummaryDTO toSummaryDTO(UtilisateurBO bo) {
        if (bo instanceof PraticienBO praticienBO) {
            return toSummaryDTO(praticienBO);
        }
        return PraticienSummaryDTO.builder()
                .id(bo.getId())
                .nom(bo.getNom())
                .prenom(bo.getPrenom())
                .build();
    }
}

