package vbm.medrelais.webapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.Role;
import vbm.medrelais.port.UtilisateurRepository;
import vbm.medrelais.security.JwtService;
import vbm.medrelais.webapp.mapper.PraticienDtoMapper;
import vbm.medrelais.webapp.model.request.PraticienRequestDTO;
import vbm.medrelais.webapp.model.request.AuthRequestDTO;
import vbm.medrelais.webapp.model.response.AuthResponseDTO;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Tag(name = "Authentification", description = "Inscription et connexion")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final PraticienDtoMapper praticienDtoMapper;

    /**
     * Inscription d'un nouveau praticien.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouveau praticien")
    @ApiResponse(responseCode = "200", description = "Inscription réussie, token JWT retourné")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody PraticienRequestDTO request) {
        PraticienBO praticien = praticienDtoMapper.toBO(request);
        praticien.setPassword(passwordEncoder.encode(request.getPassword()));
        praticien.setRole(Role.PRATICIEN);
        utilisateurRepository.save(praticien);

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        return ResponseEntity.ok(new AuthResponseDTO(jwtService.generateToken(userDetails)));
    }

    /**
     * Authentification d'un praticien existant.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion d'un praticien")
    @ApiResponse(responseCode = "200", description = "Authentification réussie, token JWT retourné")
    @ApiResponse(responseCode = "400", description = "Corps de la requête invalide")
    @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        return ResponseEntity.ok(new AuthResponseDTO(jwtService.generateToken(userDetails)));
    }
}

