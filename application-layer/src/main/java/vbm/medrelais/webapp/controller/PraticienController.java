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
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.service.PraticienService;
import vbm.medrelais.webapp.mapper.PraticienDtoMapper;
import vbm.medrelais.webapp.model.request.PraticienRequestDTO;
import vbm.medrelais.webapp.model.response.PraticienResponseDTO;

import java.util.List;

@RestController
@RequestMapping(value = "/api/praticiens", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
@AllArgsConstructor
@Tag(name = "Praticiens", description = "Gestion des praticiens")
public class PraticienController {

    private final PraticienService praticienService;
    private final PraticienDtoMapper praticienDtoMapper;

    @GetMapping
    @Operation(summary = "Lister tous les praticiens")
    @ApiResponse(responseCode = "200", description = "Liste des praticiens")
    @ApiResponse(responseCode = "204", description = "Aucun praticien trouvé")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<PraticienResponseDTO>> getAll() {
        List<PraticienResponseDTO> result = praticienService.getAll().stream()
                .map(praticienDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un praticien par son id")
    @ApiResponse(responseCode = "200", description = "Praticien trouvé")
    @ApiResponse(responseCode = "404", description = "Praticien introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<PraticienResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(praticienDtoMapper.toResponseDTO(praticienService.getById(id)));
    }

    @GetMapping("/specialite/{specialite}")
    @Operation(summary = "Rechercher des praticiens par spécialité")
    @ApiResponse(responseCode = "200", description = "Liste des praticiens correspondants")
    @ApiResponse(responseCode = "204", description = "Aucun praticien trouvé")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<PraticienResponseDTO>> getBySpecialite(@PathVariable String specialite) {
        List<PraticienResponseDTO> result = praticienService.getBySpecialite(specialite).stream()
                .map(praticienDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/ville/{ville}")
    @Operation(summary = "Rechercher des praticiens par ville")
    @ApiResponse(responseCode = "200", description = "Liste des praticiens correspondants")
    @ApiResponse(responseCode = "204", description = "Aucun praticien trouvé")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<PraticienResponseDTO>> getByVille(@PathVariable String ville) {
        List<PraticienResponseDTO> result = praticienService.getByVille(ville).stream()
                .map(praticienDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Rechercher des praticiens par proximité géographique")
    @ApiResponse(responseCode = "200", description = "Liste des praticiens dans le rayon")
    @ApiResponse(responseCode = "204", description = "Aucun praticien trouvé dans ce rayon")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<List<PraticienResponseDTO>> getNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "25") double rayonKm) {
        List<PraticienResponseDTO> result = praticienService.getNearby(latitude, longitude, rayonKm).stream()
                .map(praticienDtoMapper::toResponseDTO)
                .toList();
        return result.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau praticien")
    @ApiResponse(responseCode = "201", description = "Praticien créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PraticienResponseDTO> create(@Valid @RequestBody PraticienRequestDTO dto) {
        PraticienBO created = praticienService.create(praticienDtoMapper.toBO(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(praticienDtoMapper.toResponseDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un praticien")
    @ApiResponse(responseCode = "200", description = "Praticien mis à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Praticien introuvable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRATICIEN')")
    public ResponseEntity<PraticienResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PraticienRequestDTO dto) {
        PraticienBO updated = praticienService.update(id, praticienDtoMapper.toBO(dto));
        return ResponseEntity.ok(praticienDtoMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un praticien")
    @ApiResponse(responseCode = "204", description = "Praticien supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Praticien introuvable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        praticienService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

