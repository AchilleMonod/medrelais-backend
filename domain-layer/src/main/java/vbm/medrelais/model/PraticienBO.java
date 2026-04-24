package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PraticienBO extends UtilisateurBO {

    private String specialite;
    private String adresseRue;
    private String adresseVille;
    private String adresseCodePostal;
    private Double latitude;
    private Double longitude;
    private String bio;
}

