package vbm.medrelais.webapp.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.model.enums.TypeRecurrence;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RegleRecurrenceRequestDTO {

    @NotNull(message = "L'identifiant du praticien est obligatoire")
    private Long praticienId;

    @NotNull(message = "Le type de récurrence est obligatoire")
    private TypeRecurrence typeRecurrence;

    @Min(value = 1, message = "L'intervalle doit être d'au moins 1")
    private int intervalle = 1;

    /**
     * Jours de la semaine en CSV (1=lundi … 7=dimanche). Ex: "1,3,5"
     * Obligatoire pour HEBDOMADAIRE.
     */
    private String joursSemaine;

    /** Jours du mois en CSV. Ex: "1,15". Utilisé pour MENSUEL. */
    private String joursMois;

    /** Mois de l'année en CSV. Ex: "6,7,8". Utilisé pour ANNUEL/CUSTOM. */
    private String moisAnnee;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    /** Fin de série par date — exclusif avec nombreOccurrences */
    private LocalDate dateFin;

    /** Fin de série par nombre d'occurrences — exclusif avec dateFin */
    private Integer nombreOccurrences;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotNull(message = "Le type de durée est obligatoire")
    private TypeDureeCreneau typeDuree;
}

