package vbm.medrelais.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AdresseDTO {
    private Long id;
    private String rue;
    private String codePostal;
    private String ville;
}

