package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientBO {
    private Long id;
    private UtilisateurBO user;
    private String nom;
    private String prenom;
    private AdresseBO adresseFacturation;
    private List<DemandeDevisBO> demandesDevis;
    private List<EtudeBO> etudes;
}