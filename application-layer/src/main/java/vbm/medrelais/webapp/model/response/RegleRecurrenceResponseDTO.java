package vbm.medrelais.webapp.model.response;

import lombok.Builder;
import lombok.Data;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.model.enums.TypeRecurrence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class RegleRecurrenceResponseDTO {

    private Long id;
    private PraticienSummaryDTO praticien;
    private TypeRecurrence typeRecurrence;
    private int intervalle;
    private String joursSemaine;
    private String joursMois;
    private String moisAnnee;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer nombreOccurrences;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private TypeDureeCreneau typeDuree;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

