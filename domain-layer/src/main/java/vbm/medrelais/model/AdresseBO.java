package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdresseBO {
    private Long id;
    private String rue;
    private String codePostal;
    private String ville;
}