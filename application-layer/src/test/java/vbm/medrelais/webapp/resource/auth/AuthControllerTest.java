package vbm.medrelais.webapp.resource.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vbm.medrelais.model.UtilisateurBO;
import vbm.medrelais.model.enums.RoleEnum;
import vbm.medrelais.port.UtilisateurRepository;
import vbm.medrelais.security.JwtService;
import vbm.medrelais.webapp.model.AuthRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @Test
    void register_withValidRequest_shouldReturn200AndToken() throws Exception {
        // GIVEN
        UtilisateurBO savedUser = UtilisateurBO.builder()
                .id(1L).email("user@test.com").role(RoleEnum.CLIENT).build();

        when(utilisateurRepository.save(any())).thenReturn(savedUser);
        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenReturn(new User("user@test.com", "encoded", Collections.emptyList()));
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");

        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
    }

    @Test
    void register_withMissingEmail_shouldReturn400() throws Exception {
        // GIVEN : email manquant → violation @Email @NotBlank
        AuthRequestDTO request = new AuthRequestDTO();
        request.setPassword("password123");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_withMissingPassword_shouldReturn400() throws Exception {
        // GIVEN : password manquant → violation @NotBlank
        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@test.com");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withValidCredentials_shouldReturn200AndToken() throws Exception {
        // GIVEN
        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenReturn(new User("user@test.com", "encoded", Collections.emptyList()));
        when(jwtService.generateToken(any())).thenReturn("mocked.jwt.token");

        AuthRequestDTO request = new AuthRequestDTO();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        // WHEN / THEN
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
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



