package vbm.medrelais.webapp.resource.demande;

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
import vbm.medrelais.model.DemandeDevisBO;
import vbm.medrelais.security.JwtAuthenticationFilter;
import vbm.medrelais.security.configuration.SecurityConfig;
import vbm.medrelais.service.DemandeDevisService;
import vbm.medrelais.webapp.mapper.DemandeDevisMapper;
import vbm.medrelais.webapp.model.DemandeDevisDTO;

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
        value = DemandeDevisController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
class DemandeDevisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DemandeDevisService demandeDevisService;

    @MockitoBean
    private DemandeDevisMapper demandeDevisMapper;

    // =========================================================
    // GET /demandeDevis
    // =========================================================

    @Test
    void getAllDemandeDevis_whenListNotEmpty_shouldReturn200WithList() throws Exception {
        // GIVEN
        DemandeDevisBO demandeDevis = DemandeDevisBO.builder()
                .id(1L)
                .delaiMax(LocalDate.of(2026, 6, 30))
                .build();
        DemandeDevisDTO demandeDevisDTO = DemandeDevisDTO.builder()
                .id(1L)
                .delaiMax(LocalDate.of(2026, 6, 30))
                .build();
        when(demandeDevisService.getAllDemandeDevis()).thenReturn(List.of(demandeDevis));
        when(demandeDevisMapper.toDTO(any())).thenReturn(demandeDevisDTO);

        // WHEN / THEN
        mockMvc.perform(get("/demandeDevis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllDemandeDevis_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(demandeDevisService.getAllDemandeDevis()).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/demandeDevis"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getDemandeDevisById_whenFound_shouldReturn200() throws Exception {
        // GIVEN
        DemandeDevisBO demandeDevis = DemandeDevisBO.builder()
                .id(1L)
                .delaiMax(LocalDate.of(2026, 6, 30))
                .build();
        when(demandeDevisService.getDemandeDevisById(1L)).thenReturn(demandeDevis);

        // WHEN / THEN
        mockMvc.perform(get("/demandeDevis/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getDemandeDevisById_whenNotFound_shouldReturn204() throws Exception {
        // GIVEN
        when(demandeDevisService.getDemandeDevisById(99L)).thenReturn(null);

        // WHEN / THEN
        mockMvc.perform(get("/demandeDevis/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllDemandeDevisByClientId_whenListNotEmpty_shouldReturn200() throws Exception {
        // GIVEN
        DemandeDevisBO demandeDevis = DemandeDevisBO.builder()
                .id(1L)
                .build();
        DemandeDevisDTO demandeDevisDTO = DemandeDevisDTO.builder()
                .id(1L)
                .build();
        when(demandeDevisService.getAllDemandeDevisByClientId(1L)).thenReturn(List.of(demandeDevis));
        when(demandeDevisMapper.toDTO(any())).thenReturn(demandeDevisDTO);

        // WHEN / THEN
        mockMvc.perform(get("/demandeDevis/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllDemandeDevisByClientId_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(demandeDevisService.getAllDemandeDevisByClientId(99L)).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/demandeDevis/client/99"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /demandeDevis
    // =========================================================

    @Test
    void createDemandeDevis_shouldReturn200() throws Exception {
        // GIVEN
        DemandeDevisBO demandeDevis = DemandeDevisBO.builder()
                .delaiMax(LocalDate.of(2026, 6, 30))
                .build();
        doNothing().when(demandeDevisService).createDemandeDevis(any());

        // WHEN / THEN
        mockMvc.perform(post("/demandeDevis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandeDevis)))
                .andExpect(status().isOk());

        verify(demandeDevisService).createDemandeDevis(any());
    }

    @Test
    void updateDemandeDevis_shouldReturn200() throws Exception {
        // GIVEN
        DemandeDevisBO demandeDevis = DemandeDevisBO.builder()
                .delaiMax(LocalDate.of(2026, 12, 31))
                .build();
        doNothing().when(demandeDevisService).updateDemandeDevis(any());

        // WHEN / THEN
        mockMvc.perform(put("/demandeDevis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(demandeDevis)))
                .andExpect(status().isOk());

        verify(demandeDevisService).updateDemandeDevis(any());
    }

    @Test
    void deleteDemandeDevis_shouldReturn200() throws Exception {
        // GIVEN
        doNothing().when(demandeDevisService).deleteDemandeDevis(1L);

        // WHEN / THEN
        mockMvc.perform(delete("/demandeDevis/1"))
                .andExpect(status().isOk());

        verify(demandeDevisService).deleteDemandeDevis(1L);
    }
}

