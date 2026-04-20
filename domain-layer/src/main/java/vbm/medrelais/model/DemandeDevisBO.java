package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DemandeDevisBO {
    private Long id;
    private LocalDate delaiMax;
    private AdresseBO adresseProjet;
    private ClientBO client;
    private List<PropositionDevisBO> propositionsDevis;
}
