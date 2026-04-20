package vbm.medrelais.webapp.resource.proposition;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vbm.medrelais.model.PropositionDevisBO;
import vbm.medrelais.security.JwtAuthenticationFilter;
import vbm.medrelais.security.configuration.SecurityConfig;
import vbm.medrelais.service.PropositionDevisService;
import vbm.medrelais.webapp.mapper.PropositionDevisMapper;
import vbm.medrelais.webapp.model.PropositionDevisDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = PropositionDevisController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
class PropositionDevisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropositionDevisService propositionDevisService;

    @MockitoBean
    private PropositionDevisMapper propositionDevisMapper;

    // =========================================================
    // GET /propositionDevis
    // =========================================================

    @Test
    void getAllPropositionDevis_whenListNotEmpty_shouldReturn200WithList() throws Exception {
        // GIVEN
        PropositionDevisBO proposition = PropositionDevisBO.builder()
                .id(1L)
                .prix(new BigDecimal("1500.00"))
                .dateRendu(LocalDate.of(2026, 6, 30))
                .build();
        PropositionDevisDTO propositionDTO = PropositionDevisDTO.builder()
                .id(1L)
                .prix(new BigDecimal("1500.00"))
                .dateRendu(LocalDate.of(2026, 6, 30))
                .build();
        when(propositionDevisService.getAllPropositionDevis()).thenReturn(List.of(proposition));
        when(propositionDevisMapper.toDTO(any())).thenReturn(propositionDTO);

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].prix").value(1500.00));
    }

    @Test
    void getAllPropositionDevis_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(propositionDevisService.getAllPropositionDevis()).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPropositionDevisById_whenFound_shouldReturn200WithProposition() throws Exception {
        // GIVEN
        PropositionDevisBO proposition = PropositionDevisBO.builder()
                .id(1L)
                .prix(new BigDecimal("1500.00"))
                .build();
        PropositionDevisDTO propositionDTO = PropositionDevisDTO.builder()
                .id(1L)
                .prix(new BigDecimal("1500.00"))
                .build();
        when(propositionDevisService.getPropositionDevisById(1L)).thenReturn(proposition);
        when(propositionDevisMapper.toDTO(any())).thenReturn(propositionDTO);

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getPropositionDevisById_whenNotFound_shouldReturn204() throws Exception {
        // GIVEN
        when(propositionDevisService.getPropositionDevisById(99L)).thenReturn(null);

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllPropositionDevisByBureauEtudeId_whenListNotEmpty_shouldReturn200() throws Exception {
        // GIVEN
        PropositionDevisBO proposition = PropositionDevisBO.builder().id(1L).build();
        PropositionDevisDTO propositionDTO = PropositionDevisDTO.builder().id(1L).build();
        when(propositionDevisService.getAllPropositionDevisByBureauEtudeId(1L)).thenReturn(List.of(proposition));
        when(propositionDevisMapper.toDTO(any())).thenReturn(propositionDTO);

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis/bureauEtude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllPropositionDevisByBureauEtudeId_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(propositionDevisService.getAllPropositionDevisByBureauEtudeId(99L)).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis/bureauEtude/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllPropositionDevisByDemandeDevisId_whenListNotEmpty_shouldReturn200() throws Exception {
        // GIVEN
        PropositionDevisBO proposition = PropositionDevisBO.builder().id(2L).build();
        PropositionDevisDTO propositionDTO = PropositionDevisDTO.builder().id(2L).build();
        when(propositionDevisService.getAllPropositionDevisByDemandeDevisId(1L)).thenReturn(List.of(proposition));
        when(propositionDevisMapper.toDTO(any())).thenReturn(propositionDTO);

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis/devis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void getAllPropositionDevisByDemandeDevisId_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(propositionDevisService.getAllPropositionDevisByDemandeDevisId(99L)).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/propositionDevis/devis/99"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /propositionDevis
    // =========================================================

    @Test
    void createPropositionDevis_shouldReturn200() throws Exception {
        // GIVEN
        PropositionDevisBO proposition = PropositionDevisBO.builder()
                .prix(new BigDecimal("2000.00"))
                .dateRendu(LocalDate.of(2026, 9, 1))
                .build();
        doNothing().when(propositionDevisService).createPropositionDevis(any());

        // WHEN / THEN
        mockMvc.perform(post("/propositionDevis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proposition)))
                .andExpect(status().isOk());

        verify(propositionDevisService).createPropositionDevis(any());
    }

    @Test
    void updatePropositionDevis_shouldReturn200() throws Exception {
        // GIVEN
        PropositionDevisBO proposition = PropositionDevisBO.builder()
                .prix(new BigDecimal("2500.00"))
                .refusee(true)
                .build();
        doNothing().when(propositionDevisService).updatePropositionDevis(any());

        // WHEN / THEN
        mockMvc.perform(put("/propositionDevis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proposition)))
                .andExpect(status().isOk());

        verify(propositionDevisService).updatePropositionDevis(any());
    }

    @Test
    void deletePropositionDevis_shouldReturn200() throws Exception {
        // GIVEN
        doNothing().when(propositionDevisService).deletePropositionDevis(1L);

        // WHEN / THEN
        mockMvc.perform(delete("/propositionDevis/1"))
                .andExpect(status().isOk());

        verify(propositionDevisService).deletePropositionDevis(1L);
    }
}

