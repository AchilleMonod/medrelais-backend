package vbm.medrelais.webapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.service.CreneauService;
import vbm.medrelais.webapp.mapper.CreneauDtoMapper;
import vbm.medrelais.webapp.model.request.CreneauRequestDTO;
import vbm.medrelais.webapp.model.response.CreneauResponseDTO;
import vbm.medrelais.webapp.model.response.PraticienSummaryDTO;

import java.time.LocalDateTime;
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
class CreneauControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreneauService creneauService;

    @MockitoBean
    private CreneauDtoMapper creneauDtoMapper;

    // =========================================================
    // Helpers
    // =========================================================

    private CreneauBO creneauBO(Long id, StatutCreneau statut) {
        return CreneauBO.builder()
                .id(id)
                .praticien(PraticienBO.builder().id(10L).build())
                .dateDebut(LocalDateTime.of(2026, 5, 1, 9, 0))
                .dateFin(LocalDateTime.of(2026, 5, 1, 12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .statut(statut)
                .build();
    }

    private CreneauResponseDTO creneauResponse(Long id, StatutCreneau statut) {
        return CreneauResponseDTO.builder()
                .id(id)
                .praticien(PraticienSummaryDTO.builder().id(10L).build())
                .dateDebut(LocalDateTime.of(2026, 5, 1, 9, 0))
                .dateFin(LocalDateTime.of(2026, 5, 1, 12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .statut(statut)
                .build();
    }

    private CreneauRequestDTO validRequest() {
        CreneauRequestDTO req = new CreneauRequestDTO();
        req.setPraticienId(10L);
        req.setDateDebut(LocalDateTime.of(2026, 5, 1, 9, 0));
        req.setDateFin(LocalDateTime.of(2026, 5, 1, 12, 0));
        req.setTypeDuree(TypeDureeCreneau.MATIN);
        return req;
    }

    // =========================================================
    // GET /api/creneaux/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenFound_shouldReturn200() throws Exception {
        when(creneauService.getById(1L)).thenReturn(creneauBO(1L, StatutCreneau.DISPONIBLE));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.DISPONIBLE));

        mockMvc.perform(get("/api/creneaux/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("DISPONIBLE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(creneauService.getById(99L)).thenThrow(new CreneauNotFoundException(99L));

        mockMvc.perform(get("/api/creneaux/99"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // GET /api/creneaux/praticien/{praticienId}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByPraticienId_whenFound_shouldReturn200() throws Exception {
        when(creneauService.getByPraticienId(10L)).thenReturn(List.of(creneauBO(1L, StatutCreneau.DISPONIBLE)));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.DISPONIBLE));

        mockMvc.perform(get("/api/creneaux/praticien/10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByPraticienId_whenEmpty_shouldReturn204() throws Exception {
        when(creneauService.getByPraticienId(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/creneaux/praticien/10"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/creneaux/praticien/{praticienId}/statut/{statut}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByPraticienIdAndStatut_whenFound_shouldReturn200() throws Exception {
        when(creneauService.getByPraticienIdAndStatut(10L, StatutCreneau.DISPONIBLE))
                .thenReturn(List.of(creneauBO(1L, StatutCreneau.DISPONIBLE)));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.DISPONIBLE));

        mockMvc.perform(get("/api/creneaux/praticien/10/statut/DISPONIBLE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByPraticienIdAndStatut_whenEmpty_shouldReturn204() throws Exception {
        when(creneauService.getByPraticienIdAndStatut(10L, StatutCreneau.ATTRIBUE))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/creneaux/praticien/10/statut/ATTRIBUE"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/creneaux/disponibles
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getDisponibles_whenFound_shouldReturn200() throws Exception {
        when(creneauService.getDisponiblesByPeriode(any(), any()))
                .thenReturn(List.of(creneauBO(1L, StatutCreneau.DISPONIBLE)));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.DISPONIBLE));

        mockMvc.perform(get("/api/creneaux/disponibles")
                        .param("debut", "2026-05-01T00:00:00")
                        .param("fin", "2026-05-31T23:59:59"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getDisponibles_whenEmpty_shouldReturn204() throws Exception {
        when(creneauService.getDisponiblesByPeriode(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/creneaux/disponibles")
                        .param("debut", "2026-05-01T00:00:00")
                        .param("fin", "2026-05-31T23:59:59"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /api/creneaux
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withValidRequest_shouldReturn201() throws Exception {
        when(creneauDtoMapper.toBO(any())).thenReturn(creneauBO(null, StatutCreneau.DISPONIBLE));
        when(creneauService.create(any())).thenReturn(creneauBO(1L, StatutCreneau.DISPONIBLE));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.DISPONIBLE));

        mockMvc.perform(post("/api/creneaux")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withMissingPraticienId_shouldReturn400() throws Exception {
        CreneauRequestDTO req = validRequest();
        req.setPraticienId(null);

        mockMvc.perform(post("/api/creneaux")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withMissingDateDebut_shouldReturn400() throws Exception {
        CreneauRequestDTO req = validRequest();
        req.setDateDebut(null);

        mockMvc.perform(post("/api/creneaux")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================
    // PUT /api/creneaux/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void update_whenFound_shouldReturn200() throws Exception {
        when(creneauDtoMapper.toBO(any())).thenReturn(creneauBO(null, StatutCreneau.DISPONIBLE));
        when(creneauService.update(eq(1L), any())).thenReturn(creneauBO(1L, StatutCreneau.DISPONIBLE));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.DISPONIBLE));

        mockMvc.perform(put("/api/creneaux/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void update_whenNotFound_shouldReturn404() throws Exception {
        when(creneauDtoMapper.toBO(any())).thenReturn(creneauBO(null, StatutCreneau.DISPONIBLE));
        when(creneauService.update(eq(99L), any())).thenThrow(new CreneauNotFoundException(99L));

        mockMvc.perform(put("/api/creneaux/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // PATCH /api/creneaux/{id}/statut
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void updateStatut_whenFound_shouldReturn200() throws Exception {
        when(creneauService.updateStatut(1L, StatutCreneau.EN_ATTENTE))
                .thenReturn(creneauBO(1L, StatutCreneau.EN_ATTENTE));
        when(creneauDtoMapper.toResponseDTO(any())).thenReturn(creneauResponse(1L, StatutCreneau.EN_ATTENTE));

        mockMvc.perform(patch("/api/creneaux/1/statut")
                        .param("statut", "EN_ATTENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void updateStatut_whenNotFound_shouldReturn404() throws Exception {
        when(creneauService.updateStatut(eq(99L), any())).thenThrow(new CreneauNotFoundException(99L));

        mockMvc.perform(patch("/api/creneaux/99/statut")
                        .param("statut", "EN_ATTENTE"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // DELETE /api/creneaux/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void delete_whenFound_shouldReturn204() throws Exception {
        doNothing().when(creneauService).delete(1L);

        mockMvc.perform(delete("/api/creneaux/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void delete_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new CreneauNotFoundException(99L)).when(creneauService).delete(99L);

        mockMvc.perform(delete("/api/creneaux/99"))
                .andExpect(status().isNotFound());
    }
}

