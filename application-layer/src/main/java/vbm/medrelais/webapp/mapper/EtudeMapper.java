package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.EtudeBO;
import vbm.medrelais.webapp.model.EtudeDTO;

@Mapper(componentModel = "spring")
public interface EtudeMapper {

    @Mapping(source = "bureauEtude.id", target = "bureauEtudeId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "propositionDevis.id", target = "propositionDevisId")
    EtudeDTO toDTO(EtudeBO etudeBO);

    @Mapping(target = "bureauEtude", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "propositionDevis", ignore = true)
    EtudeBO toBO(EtudeDTO etudeDTO);
}

