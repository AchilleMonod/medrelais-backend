package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.PropositionDevisBO;
import vbm.medrelais.webapp.model.PropositionDevisDTO;

@Mapper(componentModel = "spring")
public interface PropositionDevisMapper {

    @Mapping(source = "bureauEtude.id", target = "bureauEtudeId")
    PropositionDevisDTO toDTO(PropositionDevisBO propositionDevisBO);

    @Mapping(target = "bureauEtude", ignore = true)
    PropositionDevisBO toBO(PropositionDevisDTO propositionDevisDTO);
}
