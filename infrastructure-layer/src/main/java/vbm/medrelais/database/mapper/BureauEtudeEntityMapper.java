package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.BureauEtudesEntity;
import vbm.medrelais.model.BureauEtudesBO;

@Mapper(componentModel = "spring", uses = {UtilisateurEntityMapper.class})
public interface BureauEtudeEntityMapper {

    @Mapping(target = "etudes", ignore = true)
    @Mapping(target = "propositions", ignore = true)
    BureauEtudesBO toBO(BureauEtudesEntity bureauEtudesEntity);

    @Mapping(target = "etudes", ignore = true)
    @Mapping(target = "propositions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BureauEtudesEntity toEntity(BureauEtudesBO bureauEtudesBO);
}
