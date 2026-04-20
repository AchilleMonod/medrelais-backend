package vbm.medrelais.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private Long id;
    private String nom;
    private String prenom;
    private AdresseDTO adresseFacturation;
    private Long utilisateurId;
}

