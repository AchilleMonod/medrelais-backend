package vbm.medrelais.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.model.enums.TypeRecurrence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegleRecurrenceBO {

    private Long id;
    private PraticienBO praticien;
    private TypeRecurrence typeRecurrence;
    private int intervalle;

    /** Jours de la semaine CSV. Ex: "1,3,5" pour lundi, mercredi, vendredi */
    private String joursSemaine;

    /** Jours du mois CSV. Ex: "1,15" */
    private String joursMois;

    /** Mois de l'année CSV. Ex: "6,7,8" */
    private String moisAnnee;

    private LocalDate dateDebut;

    /** Fin de série par date (exclusif avec nombreOccurrences) */
    private LocalDate dateFin;

    /** Fin de série par nombre d'occurrences (exclusif avec dateFin) */
    private Integer nombreOccurrences;

    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeDureeCreneau typeDuree;
    private boolean active;

    @Builder.Default
    private List<CreneauBO> creneaux = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


