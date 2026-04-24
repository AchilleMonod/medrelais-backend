package vbm.medrelais.webapp.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * Résumé léger d'un praticien, intégré dans les réponses imbriquées
 * (ex: dans CreneauResponseDTO) pour éviter les réponses trop volumineuses.
 */
@Data
@Builder
public class PraticienSummaryDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    private String adresseVille;
    private Double latitude;
    private Double longitude;
}

