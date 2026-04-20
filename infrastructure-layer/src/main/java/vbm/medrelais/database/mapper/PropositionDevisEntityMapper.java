package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.PropositionDevisEntity;
import vbm.medrelais.model.PropositionDevisBO;

@Mapper(componentModel = "spring", uses = {BureauEtudeEntityMapper.class})
public interface PropositionDevisEntityMapper {

    @Mapping(target = "demandeDevisId",  source = "demandeDevis.id")
    @Mapping(target = "cheminDevisPdf", source = "devisPDF.cheminDocument")
    PropositionDevisBO toBO(PropositionDevisEntity entity);

    @Mapping(target = "demandeDevis.id",          source = "demandeDevisId")
    @Mapping(target = "devisPDF.cheminDocument",  source = "cheminDevisPdf")
    @Mapping(target = "createdAt",                ignore = true)
    PropositionDevisEntity toEntity(PropositionDevisBO bo);
}
