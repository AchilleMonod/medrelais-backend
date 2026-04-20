package vbm.medrelais.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BureauEtudesDTO {
    private Long id;
    private String raisonSociale;
    private String emailContact;
    private String telContact;
    private Long utilisateurId;
}
