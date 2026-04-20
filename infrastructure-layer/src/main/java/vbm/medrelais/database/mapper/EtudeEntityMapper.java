package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.EtudeEntity;
import vbm.medrelais.model.EtudeBO;

@Mapper(componentModel = "spring", uses = {BureauEtudeEntityMapper.class, ClientEntityMapper.class, PropositionDevisEntityMapper.class})
public interface EtudeEntityMapper {

    // rapport (DocumentEntity) n'a pas de correspondance dans EtudeBO
    EtudeBO toBO(EtudeEntity entity);

    @Mapping(target = "rapport", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EtudeEntity toEntity(EtudeBO bo);
}

