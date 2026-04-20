package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vbm.medrelais.model.enums.EtatEtudeEnum;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EtudeBO {
    private Long id;
    private BureauEtudesBO bureauEtude;
    private ClientBO client;
    private PropositionDevisBO propositionDevis;
    private EtatEtudeEnum etat;
}