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
import vbm.medrelais.exception.RegleRecurrenceNotFoundException;
import vbm.medrelais.model.RegleRecurrenceBO;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.model.enums.TypeRecurrence;
import vbm.medrelais.service.RegleRecurrenceService;
import vbm.medrelais.webapp.mapper.RegleRecurrenceDtoMapper;
import vbm.medrelais.webapp.model.request.RegleRecurrenceRequestDTO;
import vbm.medrelais.webapp.model.response.PraticienSummaryDTO;
import vbm.medrelais.webapp.model.response.RegleRecurrenceResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
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
class RegleRecurrenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegleRecurrenceService regleRecurrenceService;

    @MockitoBean
    private RegleRecurrenceDtoMapper regleRecurrenceDtoMapper;

    // =========================================================
    // Helpers
    // =========================================================

    private RegleRecurrenceBO regleBO(Long id) {
        return RegleRecurrenceBO.builder()
                .id(id)
                .typeRecurrence(TypeRecurrence.HEBDOMADAIRE)
                .intervalle(1)
                .joursSemaine("1")
                .dateDebut(LocalDate.of(2026, 5, 4))
                .dateFin(LocalDate.of(2026, 5, 31))
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .active(true)
                .build();
    }

    private RegleRecurrenceResponseDTO regleResponse(Long id) {
        return RegleRecurrenceResponseDTO.builder()
                .id(id)
                .praticien(PraticienSummaryDTO.builder().id(10L).build())
                .typeRecurrence(TypeRecurrence.HEBDOMADAIRE)
                .intervalle(1)
                .joursSemaine("1")
                .dateDebut(LocalDate.of(2026, 5, 4))
                .dateFin(LocalDate.of(2026, 5, 31))
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .active(true)
                .build();
    }

    private RegleRecurrenceRequestDTO validRequest() {
        RegleRecurrenceRequestDTO req = new RegleRecurrenceRequestDTO();
        req.setPraticienId(10L);
        req.setTypeRecurrence(TypeRecurrence.HEBDOMADAIRE);
        req.setIntervalle(1);
        req.setJoursSemaine("1");
        req.setDateDebut(LocalDate.of(2026, 5, 4));
        req.setDateFin(LocalDate.of(2026, 5, 31));
        req.setHeureDebut(LocalTime.of(9, 0));
        req.setHeureFin(LocalTime.of(12, 0));
        req.setTypeDuree(TypeDureeCreneau.MATIN);
        return req;
    }

    // =========================================================
    // GET /api/regles-recurrence/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenFound_shouldReturn200() throws Exception {
        when(regleRecurrenceService.getById(1L)).thenReturn(regleBO(1L));
        when(regleRecurrenceDtoMapper.toResponseDTO(any())).thenReturn(regleResponse(1L));

        mockMvc.perform(get("/api/regles-recurrence/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(regleRecurrenceService.getById(99L)).thenThrow(new RegleRecurrenceNotFoundException(99L));

        mockMvc.perform(get("/api/regles-recurrence/99"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // GET /api/regles-recurrence/praticien/{praticienId}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByPraticienId_whenFound_shouldReturn200() throws Exception {
        when(regleRecurrenceService.getByPraticienId(10L)).thenReturn(List.of(regleBO(1L)));
        when(regleRecurrenceDtoMapper.toResponseDTO(any())).thenReturn(regleResponse(1L));

        mockMvc.perform(get("/api/regles-recurrence/praticien/10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByPraticienId_whenEmpty_shouldReturn204() throws Exception {
        when(regleRecurrenceService.getByPraticienId(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/regles-recurrence/praticien/10"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/regles-recurrence/praticien/{praticienId}/actives
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getActivesByPraticienId_whenFound_shouldReturn200() throws Exception {
        when(regleRecurrenceService.getActivesByPraticienId(10L)).thenReturn(List.of(regleBO(1L)));
        when(regleRecurrenceDtoMapper.toResponseDTO(any())).thenReturn(regleResponse(1L));

        mockMvc.perform(get("/api/regles-recurrence/praticien/10/actives"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getActivesByPraticienId_whenEmpty_shouldReturn204() throws Exception {
        when(regleRecurrenceService.getActivesByPraticienId(10L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/regles-recurrence/praticien/10/actives"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /api/regles-recurrence
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withValidRequest_shouldReturn201() throws Exception {
        when(regleRecurrenceDtoMapper.toBO(any())).thenReturn(regleBO(null));
        when(regleRecurrenceService.create(any())).thenReturn(regleBO(1L));
        when(regleRecurrenceDtoMapper.toResponseDTO(any())).thenReturn(regleResponse(1L));

        mockMvc.perform(post("/api/regles-recurrence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withMissingPraticienId_shouldReturn400() throws Exception {
        RegleRecurrenceRequestDTO req = validRequest();
        req.setPraticienId(null);

        mockMvc.perform(post("/api/regles-recurrence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void create_withMissingTypeRecurrence_shouldReturn400() throws Exception {
        RegleRecurrenceRequestDTO req = validRequest();
        req.setTypeRecurrence(null);

        mockMvc.perform(post("/api/regles-recurrence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================
    // PUT /api/regles-recurrence/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void update_whenFound_shouldReturn200() throws Exception {
        when(regleRecurrenceDtoMapper.toBO(any())).thenReturn(regleBO(null));
        when(regleRecurrenceService.update(eq(1L), any())).thenReturn(regleBO(1L));
        when(regleRecurrenceDtoMapper.toResponseDTO(any())).thenReturn(regleResponse(1L));

        mockMvc.perform(put("/api/regles-recurrence/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void update_whenNotFound_shouldReturn404() throws Exception {
        when(regleRecurrenceDtoMapper.toBO(any())).thenReturn(regleBO(null));
        when(regleRecurrenceService.update(eq(99L), any()))
                .thenThrow(new RegleRecurrenceNotFoundException(99L));

        mockMvc.perform(put("/api/regles-recurrence/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // PATCH /api/regles-recurrence/{id}/desactiver
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void desactiver_whenFound_shouldReturn200() throws Exception {
        RegleRecurrenceBO desactivee = regleBO(1L);
        desactivee.setActive(false);
        RegleRecurrenceResponseDTO desactiveeResponse = RegleRecurrenceResponseDTO.builder()
                .id(1L)
                .praticien(PraticienSummaryDTO.builder().id(10L).build())
                .typeRecurrence(TypeRecurrence.HEBDOMADAIRE)
                .intervalle(1)
                .joursSemaine("1")
                .dateDebut(LocalDate.of(2026, 5, 4))
                .dateFin(LocalDate.of(2026, 5, 31))
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .active(false)
                .build();

        when(regleRecurrenceService.desactiver(1L)).thenReturn(desactivee);
        when(regleRecurrenceDtoMapper.toResponseDTO(any())).thenReturn(desactiveeResponse);

        mockMvc.perform(patch("/api/regles-recurrence/1/desactiver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void desactiver_whenNotFound_shouldReturn404() throws Exception {
        when(regleRecurrenceService.desactiver(99L))
                .thenThrow(new RegleRecurrenceNotFoundException(99L));

        mockMvc.perform(patch("/api/regles-recurrence/99/desactiver"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // DELETE /api/regles-recurrence/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void delete_whenFound_shouldReturn204() throws Exception {
        doNothing().when(regleRecurrenceService).delete(1L);

        mockMvc.perform(delete("/api/regles-recurrence/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void delete_whenNotFound_shouldReturn404() throws Exception {
        doThrow(new RegleRecurrenceNotFoundException(99L)).when(regleRecurrenceService).delete(99L);

        mockMvc.perform(delete("/api/regles-recurrence/99"))
                .andExpect(status().isNotFound());
    }
}


