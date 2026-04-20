package vbm.medrelais.webapp.resource.demande;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vbm.medrelais.service.DemandeDevisService;
import vbm.medrelais.webapp.mapper.DemandeDevisMapper;
import vbm.medrelais.webapp.model.DemandeDevisDTO;

import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(value="/demandeDevis", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
public class DemandeDevisController {

    private final DemandeDevisService demandeDevisService;
    private final DemandeDevisMapper demandeDevisMapper;

    @GetMapping
    @Operation(summary = "Get all demandeDevis")
    @ApiResponse(responseCode = "200", description = "Liste des demandeDevis")
    @ApiResponse(responseCode = "204", description = "Aucun demandeDevis trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    public ResponseEntity<List<DemandeDevisDTO>> getAllDemandeDevis() {
        List<DemandeDevisDTO> response = demandeDevisService.getAllDemandeDevis()
                .stream()
                .map(demandeDevisMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one demandeDevis by ID")
    @ApiResponse(responseCode = "200", description = "Trouve un demandeDevis à partir de son ID")
    @ApiResponse(responseCode = "204", description = "Aucun demandeDevis trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "DemandeDevis introuvable")
    public ResponseEntity<DemandeDevisDTO> getDemandeDevisById(@PathVariable Long id) {
        var bo = demandeDevisService.getDemandeDevisById(id);
        if (bo == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(demandeDevisMapper.toDTO(bo));
    }

    @GetMapping("/client/{id}")
    @Operation(summary = "Get all DemandeDevis by Client ID")
    @ApiResponse(responseCode = "200", description = "Trouve tous les demandeDevis à partir de l'ID du client")
    @ApiResponse(responseCode = "204", description = "Aucun demandeDevis trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ResponseEntity<List<DemandeDevisDTO>> getAllDemandeDevisByClientId(@PathVariable Long id) {
        List<DemandeDevisDTO> response = demandeDevisService.getAllDemandeDevisByClientId(id)
                .stream()
                .map(demandeDevisMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new DemandeDevis")
    @ApiResponse(responseCode = "201", description = "DemandeDevis créée avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et CLIENT")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> createDemandeDevis(@RequestBody DemandeDevisDTO demandeDevisDTO) {
        demandeDevisService.createDemandeDevis(demandeDevisMapper.toBO(demandeDevisDTO));
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update an existing DemandeDevis")
    @ApiResponse(responseCode = "200", description = "DemandeDevis mise à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et CLIENT")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> updateDemandeDevis(@RequestBody DemandeDevisDTO demandeDevisDTO) {
        demandeDevisService.updateDemandeDevis(demandeDevisMapper.toBO(demandeDevisDTO));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a DemandeDevis")
    @ApiResponse(responseCode = "200", description = "DemandeDevis supprimée avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé au rôle ADMIN")
    @ApiResponse(responseCode = "404", description = "DemandeDevis introuvable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDemandeDevis(@PathVariable Long id) {
        demandeDevisService.deleteDemandeDevis(id);
        return ResponseEntity.ok().build();
    }
}