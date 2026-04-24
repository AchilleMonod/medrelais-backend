package vbm.medrelais.webapp.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vbm.medrelais.model.enums.TypeDureeCreneau;

import java.time.LocalDateTime;

@Data
public class CreneauRequestDTO {

    @NotNull(message = "L'identifiant du praticien est obligatoire")
    private Long praticienId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;

    @NotNull(message = "Le type de durée est obligatoire")
    private TypeDureeCreneau typeDuree;

    private String titre;
    private String description;
}

