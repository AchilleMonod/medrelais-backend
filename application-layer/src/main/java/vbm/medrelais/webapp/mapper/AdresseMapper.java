package vbm.medrelais.webapp.mapper;

import org.mapstruct.Mapper;
import vbm.medrelais.model.AdresseBO;
import vbm.medrelais.webapp.model.AdresseDTO;

@Mapper(componentModel = "spring")
public interface AdresseMapper {

    AdresseDTO toDTO(AdresseBO adresseBO);

    AdresseBO toBO(AdresseDTO adresseDTO);
}

