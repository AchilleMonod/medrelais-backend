package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.UtilisateurEntity;
import vbm.medrelais.model.UtilisateurBO;

@Mapper(componentModel = "spring")
public interface UtilisateurEntityMapper {

    UtilisateurBO toBO(UtilisateurEntity utilisateurEntity);

    @Mapping(target = "createdAt", ignore = true)
    UtilisateurEntity toEntity(UtilisateurBO utilisateurBO);
}

