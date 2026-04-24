package vbm.medrelais.webapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.model.enums.StatutAttribution;
import vbm.medrelais.service.AttributionService;
import vbm.medrelais.webapp.mapper.AttributionDtoMapper;
import vbm.medrelais.webapp.model.request.AttributionRequestDTO;
import vbm.medrelais.webapp.model.response.AttributionResponseDTO;

import java.util.List;

@RestController
@RequestMapping(value = "/api/attributions", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
@Tag(name = "Attributions", description = "Gestion des demandes de remplacement")
public class AttributionController {

    private final AttributionService attributionService;
    private final AttributionDtoMapper attributionDtoMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une attribution par son id")
    @ApiResponse(responseCode = "200", description = "Attribution trouvée")
    @ApiResponse(responseCode = "404", description = "Attribution introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<AttributionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attributionDtoMapper.toResponseDTO(attributionService.getById(id)));
    }

    @GetMapping("/creneau/{creneauId}")
    @Operation(summary = "Lister les demandes pour un créneau donné")
    @ApiResponse(responseCode = "200", description = "Liste des attributions")
    @ApiResponse(responseCode = "204", description = "Aucune attribution trouvée")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<AttributionResponseDTO>> getByCreneauId(@PathVariable Long creneauId) {
        List<AttributionResponseDTO> result = attributionService.getByCreneauId(creneauId).stream()
                .map(attributionDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/remplacant/{remplacantId}")
    @Operation(summary = "Lister les demandes d'un remplaçant")
    @ApiResponse(responseCode = "200", description = "Liste des attributions")
    @ApiResponse(responseCode = "204", description = "Aucune attribution trouvée")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<AttributionResponseDTO>> getByRemplacantId(@PathVariable Long remplacantId) {
        List<AttributionResponseDTO> result = attributionService.getByRemplacantId(remplacantId).stream()
                .map(attributionDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/remplacant/{remplacantId}/statut/{statut}")
    @Operation(summary = "Lister les demandes d'un remplaçant filtrées par statut")
    @ApiResponse(responseCode = "200", description = "Liste des attributions filtrées")
    @ApiResponse(responseCode = "204", description = "Aucune attribution trouvée")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<AttributionResponseDTO>> getByRemplacantIdAndStatut(
            @PathVariable Long remplacantId,
            @PathVariable StatutAttribution statut) {
        List<AttributionResponseDTO> result = attributionService.getByRemplacantIdAndStatut(remplacantId, statut).stream()
                .map(attributionDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Soumettre une demande de remplacement")
    @ApiResponse(responseCode = "201", description = "Demande soumise avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "409", description = "Créneau indisponible ou doublon de demande")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<AttributionResponseDTO> demanderRemplacement(
            @Valid @RequestBody AttributionRequestDTO dto) {
        AttributionBO created = attributionService.demanderRemplacement(attributionDtoMapper.toBO(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(attributionDtoMapper.toResponseDTO(created));
    }

    @PatchMapping("/{id}/accepter")
    @Operation(summary = "Accepter une demande de remplacement",
               description = "Accepte la demande et refuse automatiquement toutes les autres demandes en attente sur ce créneau")
    @ApiResponse(responseCode = "200", description = "Demande acceptée")
    @ApiResponse(responseCode = "404", description = "Attribution introuvable")
    @ApiResponse(responseCode = "409", description = "La demande n'est pas en statut EN_ATTENTE")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<AttributionResponseDTO> accepter(@PathVariable Long id) {
        return ResponseEntity.ok(attributionDtoMapper.toResponseDTO(attributionService.accepter(id)));
    }

    @PatchMapping("/{id}/refuser")
    @Operation(summary = "Refuser une demande de remplacement")
    @ApiResponse(responseCode = "200", description = "Demande refusée")
    @ApiResponse(responseCode = "404", description = "Attribution introuvable")
    @ApiResponse(responseCode = "409", description = "La demande n'est pas en statut EN_ATTENTE")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<AttributionResponseDTO> refuser(@PathVariable Long id) {
        return ResponseEntity.ok(attributionDtoMapper.toResponseDTO(attributionService.refuser(id)));
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler une demande de remplacement (par le remplaçant)")
    @ApiResponse(responseCode = "200", description = "Demande annulée")
    @ApiResponse(responseCode = "404", description = "Attribution introuvable")
    @ApiResponse(responseCode = "409", description = "La demande ne peut pas être annulée")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<AttributionResponseDTO> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(attributionDtoMapper.toResponseDTO(attributionService.annuler(id)));
    }
}

