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
public class BureauEtudesBO {
    private Long id;
    private UtilisateurBO user;
    private String raisonSociale;
    private String emailContact;
    private String telContact;
    private List<EtudeBO> etudes;
    private List<PropositionDevisBO> propositions;
}