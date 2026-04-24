package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vbm.medrelais.model.enums.Role;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class UtilisateurBO {

    private Long id;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String telephone;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

