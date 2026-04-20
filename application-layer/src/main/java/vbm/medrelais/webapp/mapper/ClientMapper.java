package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.webapp.model.ClientDTO;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface ClientMapper {

    @Mapping(source = "user.id", target = "utilisateurId")
    ClientDTO toDTO(ClientBO clientBO);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "demandesDevis", ignore = true)
    @Mapping(target = "etudes", ignore = true)
    ClientBO toBO(ClientDTO clientDTO);
}

