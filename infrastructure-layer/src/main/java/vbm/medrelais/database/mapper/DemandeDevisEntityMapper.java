package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.DemandeDevisEntity;
import vbm.medrelais.model.DemandeDevisBO;

@Mapper(componentModel = "spring", uses = {AdresseEntityMapper.class, ClientEntityMapper.class, PropositionDevisEntityMapper.class})
public interface DemandeDevisEntityMapper {

    DemandeDevisBO toBO(DemandeDevisEntity entity);

    // docsDevis n'existe pas dans DemandeDevisBO (pas de DocumentBO)
    @Mapping(target = "docsDevis", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DemandeDevisEntity toEntity(DemandeDevisBO bo);
}

