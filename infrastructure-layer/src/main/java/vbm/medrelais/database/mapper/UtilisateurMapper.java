package vbm.medrelais.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vbm.medrelais.database.entities.AdminEntity;
import vbm.medrelais.database.entities.PraticienEntity;
import vbm.medrelais.database.entities.UtilisateurEntity;
import vbm.medrelais.model.AdminBO;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.UtilisateurBO;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    // =========================================================
    // Entity → BO
    // =========================================================

    /** Dispatch selon le type concret de l'entité */
    default UtilisateurBO toBO(UtilisateurEntity entity) {
        if (entity instanceof PraticienEntity p) return toPraticienBO(p);
        if (entity instanceof AdminEntity a)     return toAdminBO(a);
        throw new IllegalArgumentException("Type entity non supporté : " + entity.getClass().getSimpleName());
    }

    @Mapping(source = "motDePasse", target = "password")
    PraticienBO toPraticienBO(PraticienEntity entity);

    @Mapping(source = "motDePasse", target = "password")
    AdminBO toAdminBO(AdminEntity entity);

    // =========================================================
    // BO → Entity
    // =========================================================

    @Mapping(source = "password", target = "motDePasse")
    @Mapping(target = "role",      ignore = true)  // géré par @DiscriminatorValue
    @Mapping(target = "createdAt", ignore = true)  // géré par @CreationTimestamp
    @Mapping(target = "updatedAt", ignore = true)  // géré par @UpdateTimestamp
    PraticienEntity toPraticienEntity(PraticienBO bo);

    @Mapping(source = "password", target = "motDePasse")
    @Mapping(target = "role",      ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AdminEntity toAdminEntity(AdminBO bo);

    default UtilisateurEntity toEntity(UtilisateurBO bo) {
        if (bo instanceof PraticienBO praticienBO) return toPraticienEntity(praticienBO);
        if (bo instanceof AdminBO adminBO)         return toAdminEntity(adminBO);
        throw new IllegalArgumentException("Type BO non supporté : " + bo.getClass().getSimpleName());
    }
}
