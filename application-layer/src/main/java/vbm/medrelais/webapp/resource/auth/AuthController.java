package vbm.medrelais.webapp.resource.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vbm.medrelais.model.UtilisateurBO;
import vbm.medrelais.model.enums.RoleEnum;
import vbm.medrelais.port.UtilisateurRepository;
import vbm.medrelais.security.JwtService;
import vbm.medrelais.webapp.model.AuthRequestDTO;
import vbm.medrelais.webapp.model.AuthResponseDTO;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscription d'un nouvel utilisateur.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    @ApiResponse(responseCode = "200", description = "Inscription réussie, token JWT retourné")
    @ApiResponse(responseCode = "400", description = "Corps de la requête invalide (email ou mot de passe manquant)")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody AuthRequestDTO request) {
        UtilisateurBO user = UtilisateurBO.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.CLIENT)
                .build();
        utilisateurRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    /**
     * Authentification d'un utilisateur existant.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    @Operation(summary = "Authentification d'un utilisateur existant")
    @ApiResponse(responseCode = "200", description = "Authentification réussie, token JWT retourné")
    @ApiResponse(responseCode = "400", description = "Corps de la requête invalide (email ou mot de passe manquant)")
    @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
