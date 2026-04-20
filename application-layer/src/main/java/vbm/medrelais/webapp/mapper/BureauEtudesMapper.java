package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.webapp.model.BureauEtudesDTO;


@Mapper(componentModel = "spring")
public interface BureauEtudesMapper {

    @Mapping(source = "user.id", target = "utilisateurId")
    BureauEtudesDTO toDTO(BureauEtudesBO bureauEtudesBO);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "etudes", ignore = true)
    @Mapping(target = "propositions", ignore = true)
    BureauEtudesBO toBO(BureauEtudesDTO bureauEtudesDTO);
}
