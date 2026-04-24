package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vbm.medrelais.model.enums.StatutAttribution;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttributionBO {

    private Long id;
    private CreneauBO creneau;
    private PraticienBO remplacant;
    private LocalDateTime dateDebutAttribution;
    private LocalDateTime dateFinAttribution;

    @Builder.Default
    private StatutAttribution statut = StatutAttribution.EN_ATTENTE;

    /** Message laissé par le remplaçant lors de la demande */
    private String message;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

