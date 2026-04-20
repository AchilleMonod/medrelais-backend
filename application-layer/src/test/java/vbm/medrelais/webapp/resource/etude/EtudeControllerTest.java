package vbm.medrelais.webapp.resource.etude;

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
import vbm.medrelais.model.EtudeBO;
import vbm.medrelais.model.enums.EtatEtudeEnum;
import vbm.medrelais.security.JwtAuthenticationFilter;
import vbm.medrelais.security.configuration.SecurityConfig;
import vbm.medrelais.service.EtudeService;
import vbm.medrelais.webapp.mapper.EtudeMapper;
import vbm.medrelais.webapp.model.EtudeDTO;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = EtudeController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
class EtudeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EtudeService etudeService;

    @MockitoBean
    private EtudeMapper etudeMapper;

    // =========================================================
    // GET /etude
    // =========================================================

    @Test
    void getAllEtudes_whenListNotEmpty_shouldReturn200WithList() throws Exception {
        // GIVEN
        EtudeBO etude = EtudeBO.builder()
                .id(1L)
                .etat(EtatEtudeEnum.DEVIS_VALIDE)
                .build();
        EtudeDTO etudeDTO = EtudeDTO.builder()
                .id(1L)
                .etat(EtatEtudeEnum.DEVIS_VALIDE)
                .build();
        when(etudeService.getAllEtudes()).thenReturn(List.of(etude));
        when(etudeMapper.toDTO(any())).thenReturn(etudeDTO);

        // WHEN / THEN
        mockMvc.perform(get("/etude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].etat").value("DEVIS_VALIDE"));
    }

    @Test
    void getAllEtudes_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(etudeService.getAllEtudes()).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/etude"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getEtudeById_whenFound_shouldReturn200WithEtude() throws Exception {
        // GIVEN
        EtudeBO etude = EtudeBO.builder()
                .id(1L)
                .etat(EtatEtudeEnum.DEVIS_VALIDE)
                .build();
        EtudeDTO etudeDTO = EtudeDTO.builder()
                .id(1L)
                .etat(EtatEtudeEnum.DEVIS_VALIDE)
                .build();
        when(etudeService.getEtudeById(1L)).thenReturn(etude);
        when(etudeMapper.toDTO(any())).thenReturn(etudeDTO);

        // WHEN / THEN
        mockMvc.perform(get("/etude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getEtudeById_whenNotFound_shouldReturn204() throws Exception {
        // GIVEN
        when(etudeService.getEtudeById(99L)).thenReturn(null);

        // WHEN / THEN
        mockMvc.perform(get("/etude/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllEtudesByBureauEtudeId_whenListNotEmpty_shouldReturn200() throws Exception {
        // GIVEN
        EtudeBO etude = EtudeBO.builder().id(1L).build();
        EtudeDTO etudeDTO = EtudeDTO.builder().id(1L).build();
        when(etudeService.getAllEtudesByBureauEtudeId(1L)).thenReturn(List.of(etude));
        when(etudeMapper.toDTO(any())).thenReturn(etudeDTO);

        // WHEN / THEN
        mockMvc.perform(get("/etude/bureauEtude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAllEtudesByBureauEtudeId_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(etudeService.getAllEtudesByBureauEtudeId(99L)).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/etude/bureauEtude/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllEtudesByClientId_whenListNotEmpty_shouldReturn200() throws Exception {
        // GIVEN
        EtudeBO etude = EtudeBO.builder().id(2L).build();
        EtudeDTO etudeDTO = EtudeDTO.builder().id(2L).build();
        when(etudeService.getAllEtudesByClientId(1L)).thenReturn(List.of(etude));
        when(etudeMapper.toDTO(any())).thenReturn(etudeDTO);

        // WHEN / THEN
        mockMvc.perform(get("/etude/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L));
    }

    @Test
    void getAllEtudesByClientId_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(etudeService.getAllEtudesByClientId(99L)).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/etude/client/99"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /etude
    // =========================================================

    @Test
    void createEtude_shouldReturn200() throws Exception {
        // GIVEN
        EtudeBO etude = EtudeBO.builder()
                .etat(EtatEtudeEnum.DEVIS_VALIDE)
                .build();
        doNothing().when(etudeService).createEtude(any());

        // WHEN / THEN
        mockMvc.perform(post("/etude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etude)))
                .andExpect(status().isOk());

        verify(etudeService).createEtude(any());
    }

    @Test
    void updateEtude_shouldReturn200() throws Exception {
        // GIVEN
        EtudeBO etude = EtudeBO.builder()
                .etat(EtatEtudeEnum.PAIEMENT_EFFECTUE)
                .build();
        doNothing().when(etudeService).updateEtude(any());

        // WHEN / THEN
        mockMvc.perform(put("/etude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(etude)))
                .andExpect(status().isOk());

        verify(etudeService).updateEtude(any());
    }

    @Test
    void deleteEtude_shouldReturn200() throws Exception {
        // GIVEN
        doNothing().when(etudeService).deleteEtude(1L);

        // WHEN / THEN
        mockMvc.perform(delete("/etude/1"))
                .andExpect(status().isOk());

        verify(etudeService).deleteEtude(1L);
    }
}

