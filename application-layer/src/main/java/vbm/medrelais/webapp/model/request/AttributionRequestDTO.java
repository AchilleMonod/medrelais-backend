package vbm.medrelais.webapp.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttributionRequestDTO {

    @NotNull(message = "L'identifiant du créneau est obligatoire")
    private Long creneauId;

    @NotNull(message = "L'identifiant du remplaçant est obligatoire")
    private Long remplacantId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime dateDebutAttribution;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFinAttribution;

    /** Message optionnel laissé par le remplaçant lors de la demande */
    private String message;
}

