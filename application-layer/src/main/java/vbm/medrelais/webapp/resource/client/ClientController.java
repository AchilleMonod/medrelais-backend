package vbm.medrelais.webapp.resource.client;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vbm.medrelais.service.ClientService;
import vbm.medrelais.webapp.mapper.ClientMapper;
import vbm.medrelais.webapp.model.ClientDTO;

import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping(value="/client", produces = "application/json")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {
    
    private final ClientService clientService;
    
    private final ClientMapper clientMapper;

    @GetMapping
    @Operation(summary = "Get all clients")
    @ApiResponse(responseCode = "200", description = "Liste des clients")
    @ApiResponse(responseCode = "204", description = "Aucun client trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    public ResponseEntity<List<ClientDTO>> getAllClients(){

        List<ClientDTO> response = clientService.getAllClients()
                .stream()
                .map(clientMapper::toDTO)
                .toList();
        if(response.isEmpty()){
            return ResponseEntity.noContent().build(); // Retourne 204 au lieu de lever une exception
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one  client by ID")
    @ApiResponse(responseCode = "200", description = "Trouve un client à partir de son ID")
    @ApiResponse(responseCode = "204", description = "Aucun client trouvé")
    @ApiResponse(responseCode = "400", description = "Bad Request") 
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Droits insuffisants")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id){

        var bo = clientService.getClientById(id);
        if(bo ==  null){
            return ResponseEntity.noContent().build(); // Retourne 204 au lieu de lever une exception
        }
        return ResponseEntity.ok(clientMapper.toDTO(bo));
    }

    

    @PostMapping
    @Operation(summary = "Create a new Client")
    @ApiResponse(responseCode = "201", description = "Client créé avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et CLIENT")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> createClient(@RequestBody ClientDTO clientDTO){
        clientService.createClient(clientMapper.toBO(clientDTO));
        return ResponseEntity.ok().build();
    }     

    @PutMapping
    @Operation(summary = "Update an existing Client")
    @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé aux rôles ADMIN et CLIENT")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<Void> updateClient(@RequestBody ClientDTO clientDTO){
        clientService.updateClient(clientMapper.toBO(clientDTO));
        return ResponseEntity.ok().build();
    }     

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Client")
    @ApiResponse(responseCode = "200", description = "Client supprimé avec succès")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Token JWT absent, expiré ou invalide")
    @ApiResponse(responseCode = "403", description = "Réservé au rôle ADMIN")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id){
        clientService.deleteClient(id);
        return ResponseEntity.ok().build();
    }     
}