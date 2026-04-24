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
import vbm.medrelais.model.RegleRecurrenceBO;
import vbm.medrelais.service.RegleRecurrenceService;
import vbm.medrelais.webapp.mapper.RegleRecurrenceDtoMapper;
import vbm.medrelais.webapp.model.request.RegleRecurrenceRequestDTO;
import vbm.medrelais.webapp.model.response.RegleRecurrenceResponseDTO;

import java.util.List;

@RestController
@RequestMapping(value = "/api/regles-recurrence", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
@Tag(name = "Règles de récurrence", description = "Gestion des règles de génération de créneaux récurrents")
public class RegleRecurrenceController {

    private final RegleRecurrenceService regleRecurrenceService;
    private final RegleRecurrenceDtoMapper regleRecurrenceDtoMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une règle de récurrence par son id")
    @ApiResponse(responseCode = "200", description = "Règle trouvée")
    @ApiResponse(responseCode = "404", description = "Règle introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<RegleRecurrenceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(regleRecurrenceDtoMapper.toResponseDTO(regleRecurrenceService.getById(id)));
    }

    @GetMapping("/praticien/{praticienId}")
    @Operation(summary = "Lister toutes les règles d'un praticien")
    @ApiResponse(responseCode = "200", description = "Liste des règles")
    @ApiResponse(responseCode = "204", description = "Aucune règle trouvée")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<RegleRecurrenceResponseDTO>> getByPraticienId(@PathVariable Long praticienId) {
        List<RegleRecurrenceResponseDTO> result = regleRecurrenceService.getByPraticienId(praticienId).stream()
                .map(regleRecurrenceDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/praticien/{praticienId}/actives")
    @Operation(summary = "Lister les règles actives d'un praticien")
    @ApiResponse(responseCode = "200", description = "Liste des règles actives")
    @ApiResponse(responseCode = "204", description = "Aucune règle active trouvée")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<RegleRecurrenceResponseDTO>> getActivesByPraticienId(@PathVariable Long praticienId) {
        List<RegleRecurrenceResponseDTO> result = regleRecurrenceService.getActivesByPraticienId(praticienId).stream()
                .map(regleRecurrenceDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Créer une règle de récurrence et générer les créneaux associés")
    @ApiResponse(responseCode = "201", description = "Règle créée et créneaux générés avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<RegleRecurrenceResponseDTO> create(@Valid @RequestBody RegleRecurrenceRequestDTO dto) {
        RegleRecurrenceBO created = regleRecurrenceService.create(regleRecurrenceDtoMapper.toBO(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(regleRecurrenceDtoMapper.toResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une règle et regénérer les créneaux DISPONIBLES")
    @ApiResponse(responseCode = "200", description = "Règle mise à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Règle introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<RegleRecurrenceResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RegleRecurrenceRequestDTO dto) {
        RegleRecurrenceBO updated = regleRecurrenceService.update(id, regleRecurrenceDtoMapper.toBO(dto));
        return ResponseEntity.ok(regleRecurrenceDtoMapper.toResponseDTO(updated));
    }

    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver une règle sans supprimer les créneaux existants")
    @ApiResponse(responseCode = "200", description = "Règle désactivée avec succès")
    @ApiResponse(responseCode = "404", description = "Règle introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<RegleRecurrenceResponseDTO> desactiver(@PathVariable Long id) {
        return ResponseEntity.ok(regleRecurrenceDtoMapper.toResponseDTO(regleRecurrenceService.desactiver(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une règle et ses créneaux encore DISPONIBLES")
    @ApiResponse(responseCode = "204", description = "Règle supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Règle introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        regleRecurrenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

