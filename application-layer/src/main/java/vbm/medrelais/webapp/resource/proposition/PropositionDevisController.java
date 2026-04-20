package vbm.medrelais.webapp.resource.proposition;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vbm.medrelais.service.PropositionDevisService;
import vbm.medrelais.webapp.mapper.PropositionDevisMapper;
import vbm.medrelais.webapp.model.PropositionDevisDTO;

import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(value="/propositionDevis", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
public class PropositionDevisController {

    private final PropositionDevisService propositionDevisService;
    private final PropositionDevisMapper propositionDevisMapper;

    @GetMapping
    @Operation(summary = "Get all propositionDevis")
    @ApiResponse(responseCode = "200", description = "Liste des propositions de devis")
    @ApiResponse(responseCode = "204", description = "Aucune proposition de devis trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    public ResponseEntity<List<PropositionDevisDTO>> getAllPropositionDevis() {
        List<PropositionDevisDTO> response = propositionDevisService.getAllPropositionDevis()
                .stream()
                .map(propositionDevisMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one propositionDevis by ID")
    @ApiResponse(responseCode = "200", description = "Trouve une proposition de devis à partir de son ID")
    @ApiResponse(responseCode = "204", description = "Aucune proposition de devis trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Proposition de devis introuvable")
    public ResponseEntity<PropositionDevisDTO> getPropositionDevisById(@PathVariable Long id) {
        var bo = propositionDevisService.getPropositionDevisById(id);
        if (bo == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(propositionDevisMapper.toDTO(bo));
    }

    @GetMapping("/bureauEtude/{id}")
    @Operation(summary = "Get all propositionDevis by BureauEtude ID")
    @ApiResponse(responseCode = "200", description = "Trouve toutes les propositions de devis à partir de l'ID du bureau d'étude")
    @ApiResponse(responseCode = "204", description = "Aucune proposition de devis trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Bureau d'étude introuvable")
    public ResponseEntity<List<PropositionDevisDTO>> getAllPropositionDevisByBureauEtudeId(@PathVariable Long id) {
        List<PropositionDevisDTO> response = propositionDevisService.getAllPropositionDevisByBureauEtudeId(id)
                .stream()
                .map(propositionDevisMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/devis/{id}")
    @Operation(summary = "Get all propositionDevis by DemandeDevis ID")
    @ApiResponse(responseCode = "200", description = "Trouve toutes les propositions de devis à partir de l'ID de la demande de devis")
    @ApiResponse(responseCode = "204", description = "Aucune proposition de devis trouvée")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Demande de devis introuvable")
    public ResponseEntity<List<PropositionDevisDTO>> getAllPropositionDevisByDemandeDevisId(@PathVariable Long id) {
        List<PropositionDevisDTO> response = propositionDevisService.getAllPropositionDevisByDemandeDevisId(id)
                .stream()
                .map(propositionDevisMapper::toDTO)
                .toList();
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new propositionDevis")
    @ApiResponse(responseCode = "201", description = "Proposition de devis créée avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et BUREAU_ETUDE")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUREAU_ETUDE')")
    public ResponseEntity<Void> createPropositionDevis(@RequestBody PropositionDevisDTO propositionDevisDTO) {
        propositionDevisService.createPropositionDevis(propositionDevisMapper.toBO(propositionDevisDTO));
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update an existing propositionDevis")
    @ApiResponse(responseCode = "200", description = "Proposition de devis mise à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et BUREAU_ETUDE")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUREAU_ETUDE')")
    public ResponseEntity<Void> updatePropositionDevis(@RequestBody PropositionDevisDTO propositionDevisDTO) {
        propositionDevisService.updatePropositionDevis(propositionDevisMapper.toBO(propositionDevisDTO));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a propositionDevis")
    @ApiResponse(responseCode = "200", description = "Proposition de devis supprimée avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé au rôle ADMIN")
    @ApiResponse(responseCode = "404", description = "Proposition de devis introuvable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePropositionDevis(@PathVariable Long id) {
        propositionDevisService.deletePropositionDevis(id);
        return ResponseEntity.ok().build();
    }
}