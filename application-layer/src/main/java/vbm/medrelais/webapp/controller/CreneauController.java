package vbm.medrelais.webapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.service.CreneauService;
import vbm.medrelais.webapp.mapper.CreneauDtoMapper;
import vbm.medrelais.webapp.model.request.CreneauRequestDTO;
import vbm.medrelais.webapp.model.response.CreneauResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/creneaux", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
@Tag(name = "Créneaux", description = "Gestion des créneaux de remplacement")
public class CreneauController {

    private final CreneauService creneauService;
    private final CreneauDtoMapper creneauDtoMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un créneau par son id")
    @ApiResponse(responseCode = "200", description = "Créneau trouvé")
    @ApiResponse(responseCode = "404", description = "Créneau introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<CreneauResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(creneauDtoMapper.toResponseDTO(creneauService.getById(id)));
    }

    @GetMapping("/praticien/{praticienId}")
    @Operation(summary = "Lister les créneaux d'un praticien")
    @ApiResponse(responseCode = "200", description = "Liste des créneaux")
    @ApiResponse(responseCode = "204", description = "Aucun créneau trouvé")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<CreneauResponseDTO>> getByPraticienId(@PathVariable Long praticienId) {
        List<CreneauResponseDTO> result = creneauService.getByPraticienId(praticienId).stream()
                .map(creneauDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/praticien/{praticienId}/statut/{statut}")
    @Operation(summary = "Lister les créneaux d'un praticien filtrés par statut")
    @ApiResponse(responseCode = "200", description = "Liste des créneaux filtrés")
    @ApiResponse(responseCode = "204", description = "Aucun créneau trouvé")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<CreneauResponseDTO>> getByPraticienIdAndStatut(
            @PathVariable Long praticienId,
            @PathVariable StatutCreneau statut) {
        List<CreneauResponseDTO> result = creneauService.getByPraticienIdAndStatut(praticienId, statut).stream()
                .map(creneauDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Rechercher les créneaux disponibles sur une période")
    @ApiResponse(responseCode = "200", description = "Liste des créneaux disponibles")
    @ApiResponse(responseCode = "204", description = "Aucun créneau disponible sur cette période")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<CreneauResponseDTO>> getDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<CreneauResponseDTO> result = creneauService.getDisponiblesByPeriode(debut, fin).stream()
                .map(creneauDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Créer un créneau ponctuel")
    @ApiResponse(responseCode = "201", description = "Créneau créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<CreneauResponseDTO> create(@Valid @RequestBody CreneauRequestDTO dto) {
        CreneauBO created = creneauService.create(creneauDtoMapper.toBO(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(creneauDtoMapper.toResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un créneau")
    @ApiResponse(responseCode = "200", description = "Créneau mis à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Créneau introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<CreneauResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CreneauRequestDTO dto) {
        CreneauBO updated = creneauService.update(id, creneauDtoMapper.toBO(dto));
        return ResponseEntity.ok(creneauDtoMapper.toResponseDTO(updated));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un créneau")
    @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Créneau introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<CreneauResponseDTO> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutCreneau statut) {
        return ResponseEntity.ok(creneauDtoMapper.toResponseDTO(creneauService.updateStatut(id, statut)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un créneau")
    @ApiResponse(responseCode = "204", description = "Créneau supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Créneau introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creneauService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

