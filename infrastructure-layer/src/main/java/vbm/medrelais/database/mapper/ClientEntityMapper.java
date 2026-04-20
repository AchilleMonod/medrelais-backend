package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.ClientEntity;
import vbm.medrelais.model.ClientBO;

@Mapper(componentModel = "spring", uses = {UtilisateurEntityMapper.class, AdresseEntityMapper.class})
public interface ClientEntityMapper {

    // demandeDevis (entity) != demandesDevis (BO) : ignoré pour éviter le cycle
    @Mapping(target = "demandesDevis", ignore = true)
    @Mapping(target = "etudes", ignore = true)
    ClientBO toBO(ClientEntity entity);

    @Mapping(target = "demandeDevis", ignore = true)
    @Mapping(target = "etudes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ClientEntity toEntity(ClientBO bo);
}

