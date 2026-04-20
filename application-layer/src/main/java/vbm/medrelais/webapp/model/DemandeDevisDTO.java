package vbm.medrelais.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DemandeDevisDTO {
    private Long id;
    private LocalDate delaiMax;
    private AdresseDTO adresseProjet;
    private Long clientId;
}

