package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.DemandeDevisBO;
import vbm.medrelais.webapp.model.DemandeDevisDTO;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface DemandeDevisMapper {

    @Mapping(source = "client.id", target = "clientId")
    DemandeDevisDTO toDTO(DemandeDevisBO demandeDevisBO);

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "propositionsDevis", ignore = true)
    DemandeDevisBO toBO(DemandeDevisDTO demandeDevisDTO);
}

