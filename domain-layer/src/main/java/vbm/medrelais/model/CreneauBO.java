package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreneauBO {

    private Long id;
    private PraticienBO praticien;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private TypeDureeCreneau typeDuree;

    @Builder.Default
    private StatutCreneau statut = StatutCreneau.DISPONIBLE;

    private String titre;
    private String description;

    /** NULL si créneau ponctuel */
    private RegleRecurrenceBO regleRecurrence;

    private boolean estException;

    @Builder.Default
    private List<AttributionBO> attributions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


