package vbm.medrelais.webapp.resource.bureau;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vbm.medrelais.service.BureauEtudesService;
import vbm.medrelais.webapp.mapper.BureauEtudesMapper;
import vbm.medrelais.webapp.model.BureauEtudesDTO;

import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(value="/bureauEtude", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
public class BureauEtudesController {

    private final BureauEtudesService bureauEtudesService;

    private final BureauEtudesMapper bureauEtudesMapper;

    @GetMapping
    @Operation(summary = "Get all bureau d'étude")
    @ApiResponse(responseCode = "200", description = "Liste des bureaux d'étude")
    @ApiResponse(responseCode = "204", description = "Aucun bureau d'étude trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    public ResponseEntity<List<BureauEtudesDTO>> getAllBureauEtude(){

        List<BureauEtudesDTO> response = bureauEtudesService.getAllBureauEtude()
                .stream()
                .map(bureauEtudesMapper::toDTO)
                .toList();
        if(response.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one  bureau d'étude by ID")
    @ApiResponse(responseCode = "200", description = "Trouve un bureau d'étude à partir de son ID")
    @ApiResponse(responseCode = "204", description = "Aucun bureau d'étude trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Bureau d'étude introuvable")
    public ResponseEntity<BureauEtudesDTO> getBureauEtudeByID(@PathVariable Long id){

        var bo = bureauEtudesService.getBureauEtudeByID(id);
        if(bo == null){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bureauEtudesMapper.toDTO(bo));
    }

    @PostMapping
    @Operation(summary = "Create a new Bureau d'Etude")
    @ApiResponse(responseCode = "201", description = "Bureau d'étude créé avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et BUREAU_ETUDE")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUREAU_ETUDE')")
    public ResponseEntity<Void> createBureauEtude(@RequestBody BureauEtudesDTO bureauEtudesDTO){
        bureauEtudesService.createBureauEtude(bureauEtudesMapper.toBO(bureauEtudesDTO));
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update an existing Bureau d'Etude")
    @ApiResponse(responseCode = "200", description = "Bureau d'étude mis à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et BUREAU_ETUDE")
    @PreAuthorize("hasAnyRole('ADMIN', 'BUREAU_ETUDE')")
    public ResponseEntity<Void> updateBureauEtude(@RequestBody BureauEtudesDTO bureauEtudesDTO){
       bureauEtudesService.updateBureauEtude(bureauEtudesMapper.toBO(bureauEtudesDTO));
       return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Bureau d'Etude")
    @ApiResponse(responseCode = "200", description = "Bureau d'étude supprimé avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé au rôle ADMIN")
    @ApiResponse(responseCode = "404", description = "Bureau d'étude introuvable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBureauEtude(@PathVariable Long id){
        bureauEtudesService.deleteBureauEtude(id);
        return ResponseEntity.ok().build();
    }
}