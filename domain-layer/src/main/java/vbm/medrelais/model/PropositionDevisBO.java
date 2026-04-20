package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PropositionDevisBO {
    private Long id;
    private BureauEtudesBO bureauEtude;
    private Long demandeDevisId;
    private LocalDate dateRendu;
    private BigDecimal prix;
    private String cheminDevisPdf;
    private boolean refusee;
}