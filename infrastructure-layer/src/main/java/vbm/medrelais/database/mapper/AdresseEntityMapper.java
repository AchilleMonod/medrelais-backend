package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.AdresseEntity;
import vbm.medrelais.model.AdresseBO;

@Mapper(componentModel = "spring")
public interface AdresseEntityMapper {

    AdresseBO toBO(AdresseEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    AdresseEntity toEntity(AdresseBO bo);
}


