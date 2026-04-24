package vbm.medrelais.database.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import vbm.medrelais.database.entities.enums.Role;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@DiscriminatorValue("ADMIN")
public class AdminEntity extends UtilisateurEntity {
    public AdminEntity() {
        setRole(Role.ADMIN);
    }
}