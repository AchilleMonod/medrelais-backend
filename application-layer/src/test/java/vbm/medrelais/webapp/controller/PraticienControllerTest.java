package vbm.medrelais.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vbm.medrelais.exception.PraticienNotFoundException;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.Role;
import vbm.medrelais.service.PraticienService;
import vbm.medrelais.webapp.mapper.PraticienDtoMapper;
import vbm.medrelais.webapp.model.request.PraticienRequestDTO;
import vbm.medrelais.webapp.model.response.PraticienResponseDTO;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PraticienControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PraticienService praticienService;

    @MockitoBean
    private PraticienDtoMapper praticienDtoMapper;

    // =========================================================
    // Helpers
    // =========================================================

    private PraticienBO praticienBO(Long id) {
        return PraticienBO.builder().id(id).email("p@test.com").nom("Dupont")
                .prenom("Paul").specialite("MG").role(Role.PRATICIEN).build();
    }

    private PraticienResponseDTO praticienResponse(Long id) {
        return PraticienResponseDTO.builder().id(id).email("p@test.com").nom("Dupont")
                .prenom("Paul").specialite("MG").role(Role.PRATICIEN).build();
    }

    private PraticienRequestDTO validRequest() {
        PraticienRequestDTO req = new PraticienRequestDTO();
        req.setEmail("p@test.com");
        req.setPassword("password123");
        req.setNom("Dupont");
        req.setPrenom("Paul");
        req.setSpecialite("MG");
        req.setAdresseRue("1 rue Test");
        req.setAdresseVille("Paris");
        req.setAdresseCodePostal("75001");
        req.setLatitude(48.85);
        req.setLongitude(2.35);
        return req;
    }

    // =========================================================
    // GET /api/praticiens
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getAll_whenListNotEmpty_shouldReturn200() throws Exception {
        when(praticienService.getAll()).thenReturn(List.of(praticienBO(1L)));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(get("/api/praticiens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getAll_whenEmpty_shouldReturn204() throws Exception {
        when(praticienService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/praticiens"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/praticiens/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenFound_shouldReturn200() throws Exception {
        when(praticienService.getById(1L)).thenReturn(praticienBO(1L));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(get("/api/praticiens/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(praticienService.getById(99L)).thenThrow(new PraticienNotFoundException(99L));

        mockMvc.perform(get("/api/praticiens/99"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // GET /api/praticiens/specialite/{specialite}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getBySpecialite_whenFound_shouldReturn200() throws Exception {
        when(praticienService.getBySpecialite("MG")).thenReturn(List.of(praticienBO(1L)));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(get("/api/praticiens/specialite/MG"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getBySpecialite_whenEmpty_shouldReturn204() throws Exception {
        when(praticienService.getBySpecialite("Inconnu")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/praticiens/specialite/Inconnu"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/praticiens/ville/{ville}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByVille_whenFound_shouldReturn200() throws Exception {
        when(praticienService.getByVille("Paris")).thenReturn(List.of(praticienBO(1L)));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(get("/api/praticiens/ville/Paris"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByVille_whenEmpty_shouldReturn204() throws Exception {
        when(praticienService.getByVille("Inconnu")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/praticiens/ville/Inconnu"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/praticiens/nearby
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getNearby_whenFound_shouldReturn200() throws Exception {
        when(praticienService.getNearby(48.85, 2.35, 25.0)).thenReturn(List.of(praticienBO(1L)));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(get("/api/praticiens/nearby")
                        .param("latitude", "48.85")
                        .param("longitude", "2.35"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getNearby_whenEmpty_shouldReturn204() throws Exception {
        when(praticienService.getNearby(0.0, 0.0, 25.0)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/praticiens/nearby")
                        .param("latitude", "0.0")
                        .param("longitude", "0.0"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /api/praticiens
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withValidRequest_shouldReturn201() throws Exception {
        when(praticienDtoMapper.toBO(any(PraticienRequestDTO.class))).thenReturn(praticienBO(null));
        when(praticienService.create(any())).thenReturn(praticienBO(1L));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(post("/api/praticiens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withMissingEmail_shouldReturn400() throws Exception {
        PraticienRequestDTO req = validRequest();
        req.setEmail(null);

        mockMvc.perform(post("/api/praticiens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withRolePraticien_shouldReturn403() throws Exception {
        mockMvc.perform(post("/api/praticiens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isForbidden());
    }

    // =========================================================
    // PUT /api/praticiens/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_whenFound_shouldReturn200() throws Exception {
        when(praticienDtoMapper.toBO(any(PraticienRequestDTO.class))).thenReturn(praticienBO(null));
        when(praticienService.update(eq(1L), any())).thenReturn(praticienBO(1L));
        when(praticienDtoMapper.toResponseDTO(any())).thenReturn(praticienResponse(1L));

        mockMvc.perform(put("/api/praticiens/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_whenNotFound_shouldReturn404() throws Exception {
        when(praticienDtoMapper.toBO(any())).thenReturn(praticienBO(null));
        when(praticienService.update(eq(99L), any())).thenThrow(new PraticienNotFoundException(99L));

        mockMvc.perform(put("/api/praticiens/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // DELETE /api/praticiens/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenFound_shouldReturn204() throws Exception {
        doNothing().when(praticienService).delete(1L);

        mockMvc.perform(delete("/api/praticiens/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new PraticienNotFoundException(99L)).when(praticienService).delete(99L);

        mockMvc.perform(delete("/api/praticiens/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void delete_withRolePraticien_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/praticiens/1"))
                .andExpect(status().isForbidden());
    }
}

