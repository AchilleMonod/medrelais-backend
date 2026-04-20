// A changer en PropositionDevisController
package vbm.medrelais.webapp.resource.etude;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vbm.medrelais.service.EtudeService;
import vbm.medrelais.webapp.mapper.EtudeMapper;
import vbm.medrelais.webapp.model.EtudeDTO;

import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(value = "/etude", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
public class EtudeController {

    private final EtudeService etudeService;
    private final EtudeMapper etudeMapper;

    @GetMapping
    @Operation(summary = "Get all etudes")
    @ApiResponse(responseCode = "200", description = "Liste des etudes")
    @ApiResponse(responseCode = "204", description = "Aucune etude trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    public ResponseEntity<List<EtudeDTO>> getAllEtudes() {
        List<EtudeDTO> response = etudeService.getAllEtudes()
                .stream()
                .map(etudeMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one etude by ID")
    @ApiResponse(responseCode = "200", description = "Trouve une etude à partir de son ID")
    @ApiResponse(responseCode = "204", description = "Aucune etude trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Etude introuvable")
    public ResponseEntity<EtudeDTO> getEtudeById(@PathVariable Long id) {
        var bo = etudeService.getEtudeById(id);
        if (bo == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(etudeMapper.toDTO(bo));
    }

    @GetMapping("/bureauEtude/{id}")
    @Operation(summary = "Get all etudes by Bureau d'Étude ID")
    @ApiResponse(responseCode = "200", description = "Trouve toutes les etudes à partir de l'ID du bureau d'étude")
    @ApiResponse(responseCode = "204", description = "Aucune etude trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Bureau d'étude introuvable")
    public ResponseEntity<List<EtudeDTO>> getAllEtudesByBureauEtudeId(@PathVariable Long id) {
        List<EtudeDTO> response = etudeService.getAllEtudesByBureauEtudeId(id)
                .stream()
                .map(etudeMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{id}")
    @Operation(summary = "Get all etudes by client ID")
    @ApiResponse(responseCode = "200", description = "Trouve toutes les etudes à partir de l'ID du client")
    @ApiResponse(responseCode = "204", description = "Aucune etude trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ResponseEntity<List<EtudeDTO>> getAllEtudesByClientId(@PathVariable Long id) {
        List<EtudeDTO> response = etudeService.getAllEtudesByClientId(id)
                .stream()
                .map(etudeMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new Etude")
    @ApiResponse(responseCode = "201", description = "Etude créée avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et BUREAU_ETUDE")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> createEtude(@RequestBody EtudeDTO etudeDTO) {
        etudeService.createEtude(etudeMapper.toBO(etudeDTO));
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update an existing Etude")
    @ApiResponse(responseCode = "200", description = "Etude mise à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et BUREAU_ETUDE")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> updateEtude(@RequestBody EtudeDTO etudeDTO) {
        etudeService.updateEtude(etudeMapper.toBO(etudeDTO));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Etude")
    @ApiResponse(responseCode = "200", description = "Etude supprimée avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé au rôle ADMIN")
    @ApiResponse(responseCode = "404", description = "Etude introuvable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEtude(@PathVariable Long id) {
        etudeService.deleteEtude(id);
        return ResponseEntity.ok().build();
    }
}