package vbm.medrelais.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vbm.medrelais.model.enums.EtatEtudeEnum;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EtudeDTO {
    private Long id;
    private Long bureauEtudeId;
    private Long clientId;
    private Long propositionDevisId;
    private EtatEtudeEnum etat;
}

