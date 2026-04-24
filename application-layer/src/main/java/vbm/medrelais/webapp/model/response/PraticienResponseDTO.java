package vbm.medrelais.webapp.model.response;

import lombok.Builder;
import lombok.Data;
import vbm.medrelais.model.enums.Role;

import java.time.LocalDateTime;

@Data
@Builder
public class PraticienResponseDTO {

    private Long id;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private Role role;
    private String specialite;
    private String adresseRue;
    private String adresseVille;
    private String adresseCodePostal;
    private Double latitude;
    private Double longitude;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

