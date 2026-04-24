package vbm.medrelais.webapp.model.response;

import lombok.Builder;
import lombok.Data;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;

import java.time.LocalDateTime;

@Data
@Builder
public class CreneauResponseDTO {

    private Long id;
    private PraticienSummaryDTO praticien;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private TypeDureeCreneau typeDuree;
    private StatutCreneau statut;
    private String titre;
    private String description;
    /** Null si créneau ponctuel */
    private Long regleRecurrenceId;
    private boolean estException;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

