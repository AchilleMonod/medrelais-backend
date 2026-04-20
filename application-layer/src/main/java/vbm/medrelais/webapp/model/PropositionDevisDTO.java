package vbm.medrelais.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PropositionDevisDTO {
    private Long id;
    private Long bureauEtudeId;
    private Long demandeDevisId;
    private LocalDate dateRendu;
    private BigDecimal prix;
    private String cheminDevisPdf;
    private boolean refusee;
}
