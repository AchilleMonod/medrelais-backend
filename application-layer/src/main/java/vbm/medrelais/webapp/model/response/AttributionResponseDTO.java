package vbm.medrelais.webapp.model.response;

import lombok.Builder;
import lombok.Data;
import vbm.medrelais.model.enums.StatutAttribution;

import java.time.LocalDateTime;

@Data
@Builder
public class AttributionResponseDTO {

    private Long id;
    /** Résumé du créneau concerné */
    private CreneauResponseDTO creneau;
    /** Résumé du remplaçant candidat */
    private PraticienSummaryDTO remplacant;
    private LocalDateTime dateDebutAttribution;
    private LocalDateTime dateFinAttribution;
    private StatutAttribution statut;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

