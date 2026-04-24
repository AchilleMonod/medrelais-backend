package vbm.medrelais.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.Role;
import vbm.medrelais.port.UtilisateurRepository;
import vbm.medrelais.security.JwtService;
import vbm.medrelais.webapp.mapper.PraticienDtoMapper;
import vbm.medrelais.webapp.model.request.AuthRequestDTO;
import vbm.medrelais.webapp.model.request.PraticienRequestDTO;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private PraticienDtoMapper praticienDtoMapper;

    // =========================================================
    // Helpers
    // =========================================================

    private PraticienRequestDTO validRegisterRequest() {
        PraticienRequestDTO req = new PraticienRequestDTO();
        req.setEmail("praticien@test.com");
        req.setPassword("password123");
        req.setNom("Dupont");
        req.setPrenom("Paul");
        req.setSpecialite("Médecine générale");
        req.setAdresseRue("1 rue de la Paix");
        req.setAdresseVille("Paris");
        req.setAdresseCodePostal("75001");
        req.setLatitude(48.85);
        req.setLongitude(2.35);
        return req;
    }

    // =========================================================
    // POST /api/auth/register
    // =========================================================

    @Test
    void register_withValidRequest_shouldReturn200AndToken() throws Exception {
        // GIVEN
        PraticienBO praticien = PraticienBO.builder()
                .id(1L).email("praticien@test.com").role(Role.PRATICIEN).build();

        when(praticienDtoMapper.toBO(any(PraticienRequestDTO.class))).thenReturn(praticien);
        when(utilisateurRepository.save(any())).thenReturn(praticien);
        when(userDetailsService.loadUserByUsername("praticien@test.com"))
                .thenReturn(new User("praticien@test.com", "encoded", Collections.emptyList()));
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
    }

    @Test
    void register_withMissingEmail_shouldReturn400() throws Exception {
        // GIVEN
        PraticienRequestDTO req = validRegisterRequest();
        req.setEmail(null);

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withInvalidEmail_shouldReturn400() throws Exception {
        // GIVEN
        PraticienRequestDTO req = validRegisterRequest();
        req.setEmail("pas-un-email");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withPasswordTooShort_shouldReturn400() throws Exception {
        // GIVEN — password < 8 caractères
        PraticienRequestDTO req = validRegisterRequest();
        req.setPassword("abc");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withMissingSpecialite_shouldReturn400() throws Exception {
        // GIVEN
        PraticienRequestDTO req = validRegisterRequest();
        req.setSpecialite(null);

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================
    // POST /api/auth/login
    // =========================================================

    @Test
    void login_withValidCredentials_shouldReturn200AndToken() throws Exception {
        // GIVEN
        when(userDetailsService.loadUserByUsername("praticien@test.com"))
                .thenReturn(new User("praticien@test.com", "encoded", Collections.emptyList()));
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");

        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("praticien@test.com");
        req.setPassword("password123");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
    }

    @Test
    void login_withBadCredentials_shouldReturn401() throws Exception {
        // GIVEN
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        AuthRequestDTO req = new AuthRequestDTO();
        req.setEmail("praticien@test.com");
        req.setPassword("wrongpassword");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withMissingBody_shouldReturn400() throws Exception {
        // WHEN / THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

