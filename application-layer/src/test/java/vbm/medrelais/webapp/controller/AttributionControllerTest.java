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
import vbm.medrelais.exception.AttributionNotFoundException;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.StatutAttribution;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.service.AttributionService;
import vbm.medrelais.webapp.mapper.AttributionDtoMapper;
import vbm.medrelais.webapp.model.request.AttributionRequestDTO;
import vbm.medrelais.webapp.model.response.AttributionResponseDTO;
import vbm.medrelais.webapp.model.response.CreneauResponseDTO;
import vbm.medrelais.webapp.model.response.PraticienSummaryDTO;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AttributionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttributionService attributionService;

    @MockitoBean
    private AttributionDtoMapper attributionDtoMapper;

    // =========================================================
    // Helpers
    // =========================================================

    private AttributionBO attributionBO(Long id, StatutAttribution statut) {
        return AttributionBO.builder()
                .id(id)
                .creneau(CreneauBO.builder().id(5L).statut(StatutCreneau.EN_ATTENTE).build())
                .remplacant(PraticienBO.builder().id(20L).build())
                .statut(statut)
                .dateDebutAttribution(LocalDateTime.of(2026, 5, 1, 9, 0))
                .dateFinAttribution(LocalDateTime.of(2026, 5, 1, 12, 0))
                .build();
    }

    private AttributionResponseDTO attributionResponse(Long id, StatutAttribution statut) {
        return AttributionResponseDTO.builder()
                .id(id)
                .creneau(CreneauResponseDTO.builder().id(5L).typeDuree(TypeDureeCreneau.MATIN)
                        .statut(StatutCreneau.EN_ATTENTE).build())
                .remplacant(PraticienSummaryDTO.builder().id(20L).build())
                .statut(statut)
                .build();
    }

    private AttributionRequestDTO validRequest() {
        AttributionRequestDTO req = new AttributionRequestDTO();
        req.setCreneauId(5L);
        req.setRemplacantId(20L);
        req.setDateDebutAttribution(LocalDateTime.of(2026, 5, 1, 9, 0));
        req.setDateFinAttribution(LocalDateTime.of(2026, 5, 1, 12, 0));
        return req;
    }

    // =========================================================
    // GET /api/attributions/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenFound_shouldReturn200() throws Exception {
        when(attributionService.getById(1L)).thenReturn(attributionBO(1L, StatutAttribution.EN_ATTENTE));
        when(attributionDtoMapper.toResponseDTO(any())).thenReturn(attributionResponse(1L, StatutAttribution.EN_ATTENTE));

        mockMvc.perform(get("/api/attributions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getById_whenNotFound_shouldReturn404() throws Exception {
        when(attributionService.getById(99L)).thenThrow(new AttributionNotFoundException(99L));

        mockMvc.perform(get("/api/attributions/99"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // GET /api/attributions/creneau/{creneauId}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByCreneauId_whenFound_shouldReturn200() throws Exception {
        when(attributionService.getByCreneauId(5L))
                .thenReturn(List.of(attributionBO(1L, StatutAttribution.EN_ATTENTE)));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.EN_ATTENTE));

        mockMvc.perform(get("/api/attributions/creneau/5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByCreneauId_whenEmpty_shouldReturn204() throws Exception {
        when(attributionService.getByCreneauId(5L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/attributions/creneau/5"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/attributions/remplacant/{remplacantId}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByRemplacantId_whenFound_shouldReturn200() throws Exception {
        when(attributionService.getByRemplacantId(20L))
                .thenReturn(List.of(attributionBO(1L, StatutAttribution.EN_ATTENTE)));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.EN_ATTENTE));

        mockMvc.perform(get("/api/attributions/remplacant/20"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByRemplacantId_whenEmpty_shouldReturn204() throws Exception {
        when(attributionService.getByRemplacantId(20L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/attributions/remplacant/20"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // GET /api/attributions/remplacant/{remplacantId}/statut/{statut}
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByRemplacantIdAndStatut_whenFound_shouldReturn200() throws Exception {
        when(attributionService.getByRemplacantIdAndStatut(20L, StatutAttribution.EN_ATTENTE))
                .thenReturn(List.of(attributionBO(1L, StatutAttribution.EN_ATTENTE)));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.EN_ATTENTE));

        mockMvc.perform(get("/api/attributions/remplacant/20/statut/EN_ATTENTE"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void getByRemplacantIdAndStatut_whenEmpty_shouldReturn204() throws Exception {
        when(attributionService.getByRemplacantIdAndStatut(20L, StatutAttribution.ACCEPTEE))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/attributions/remplacant/20/statut/ACCEPTEE"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /api/attributions
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void demanderRemplacement_withValidRequest_shouldReturn201() throws Exception {
        when(attributionDtoMapper.toBO(any())).thenReturn(attributionBO(null, StatutAttribution.EN_ATTENTE));
        when(attributionService.demanderRemplacement(any()))
                .thenReturn(attributionBO(1L, StatutAttribution.EN_ATTENTE));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.EN_ATTENTE));

        mockMvc.perform(post("/api/attributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void demanderRemplacement_withMissingCreneauId_shouldReturn400() throws Exception {
        AttributionRequestDTO req = validRequest();
        req.setCreneauId(null);

        mockMvc.perform(post("/api/attributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void demanderRemplacement_whenCreneauIndisponible_shouldReturn409() throws Exception {
        when(attributionDtoMapper.toBO(any())).thenReturn(attributionBO(null, StatutAttribution.EN_ATTENTE));
        when(attributionService.demanderRemplacement(any()))
                .thenThrow(new IllegalStateException("Le créneau n'est plus disponible"));

        mockMvc.perform(post("/api/attributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void demanderRemplacement_whenCreneauInexistant_shouldReturn404() throws Exception {
        when(attributionDtoMapper.toBO(any())).thenReturn(attributionBO(null, StatutAttribution.EN_ATTENTE));
        when(attributionService.demanderRemplacement(any()))
                .thenThrow(new CreneauNotFoundException(99L));

        mockMvc.perform(post("/api/attributions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // PATCH /api/attributions/{id}/accepter
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void accepter_whenFound_shouldReturn200() throws Exception {
        when(attributionService.accepter(1L)).thenReturn(attributionBO(1L, StatutAttribution.ACCEPTEE));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.ACCEPTEE));

        mockMvc.perform(patch("/api/attributions/1/accepter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ACCEPTEE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void accepter_whenPasEnAttente_shouldReturn409() throws Exception {
        when(attributionService.accepter(1L))
                .thenThrow(new IllegalStateException("Seule une attribution EN_ATTENTE peut être acceptée"));

        mockMvc.perform(patch("/api/attributions/1/accepter"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void accepter_whenNotFound_shouldReturn404() throws Exception {
        when(attributionService.accepter(99L)).thenThrow(new AttributionNotFoundException(99L));

        mockMvc.perform(patch("/api/attributions/99/accepter"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // PATCH /api/attributions/{id}/refuser
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void refuser_whenFound_shouldReturn200() throws Exception {
        when(attributionService.refuser(1L)).thenReturn(attributionBO(1L, StatutAttribution.REFUSEE));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.REFUSEE));

        mockMvc.perform(patch("/api/attributions/1/refuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REFUSEE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void refuser_whenNotFound_shouldReturn404() throws Exception {
        when(attributionService.refuser(99L)).thenThrow(new AttributionNotFoundException(99L));

        mockMvc.perform(patch("/api/attributions/99/refuser"))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // PATCH /api/attributions/{id}/annuler
    // =========================================================

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void annuler_whenFound_shouldReturn200() throws Exception {
        when(attributionService.annuler(1L)).thenReturn(attributionBO(1L, StatutAttribution.ANNULEE));
        when(attributionDtoMapper.toResponseDTO(any()))
                .thenReturn(attributionResponse(1L, StatutAttribution.ANNULEE));

        mockMvc.perform(patch("/api/attributions/1/annuler"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ANNULEE"));
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void annuler_whenDejaRefusee_shouldReturn409() throws Exception {
        when(attributionService.annuler(1L))
                .thenThrow(new IllegalStateException("Une attribution déjà refusée ne peut pas être annulée"));

        mockMvc.perform(patch("/api/attributions/1/annuler"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "PRATICIEN")
    void annuler_whenNotFound_shouldReturn404() throws Exception {
        when(attributionService.annuler(99L)).thenThrow(new AttributionNotFoundException(99L));

        mockMvc.perform(patch("/api/attributions/99/annuler"))
                .andExpect(status().isNotFound());
    }
}

