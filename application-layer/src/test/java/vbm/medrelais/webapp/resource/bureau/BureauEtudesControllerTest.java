package vbm.medrelais.webapp.resource.bureau;

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
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.security.configuration.SecurityConfig;
import vbm.medrelais.security.JwtAuthenticationFilter;
import vbm.medrelais.service.BureauEtudesService;
import vbm.medrelais.webapp.mapper.BureauEtudesMapper;
import vbm.medrelais.webapp.model.BureauEtudesDTO;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = BureauEtudesController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
class BureauEtudesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BureauEtudesService bureauEtudesService;

    @MockitoBean
    private BureauEtudesMapper bureauEtudesMapper;

    // =========================================================
    // GET /bureauEtude
    // =========================================================

    @Test
    void getAllBureauEtude_whenListNotEmpty_shouldReturn200WithList() throws Exception {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .telContact("0600000000")
                .build();
        BureauEtudesDTO bureauDTO = BureauEtudesDTO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .telContact("0600000000")
                .build();
        when(bureauEtudesService.getAllBureauEtude()).thenReturn(List.of(bureau));
        when(bureauEtudesMapper.toDTO(any())).thenReturn(bureauDTO);

        // WHEN / THEN
        mockMvc.perform(get("/bureauEtude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].raisonSociale").value("GeoTest SARL"));
    }

    @Test
    void getAllBureauEtude_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(bureauEtudesService.getAllBureauEtude()).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/bureauEtude"))
                .andExpect(status().isNoContent());
    }
 @Test
    void getBureauEtudeByID_whenFound_shouldReturn200WithBureau() throws Exception {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .build();
        BureauEtudesDTO bureauDTO = BureauEtudesDTO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .build();
        when(bureauEtudesService.getBureauEtudeByID(1L)).thenReturn(bureau);
        when(bureauEtudesMapper.toDTO(any())).thenReturn(bureauDTO);

        // WHEN / THEN
        mockMvc.perform(get("/bureauEtude/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.raisonSociale").value("GeoTest SARL"));
    }

    @Test
    void getBureauEtudeByID_whenNotFound_shouldReturn204() throws Exception {
        // GIVEN
        when(bureauEtudesService.getBureauEtudeByID(99L)).thenReturn(null);

        // WHEN / THEN
        mockMvc.perform(get("/bureauEtude/99"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /bureauEtude
    // =========================================================

    @Test
    void createBureauEtude_shouldReturn200() throws Exception {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .telContact("0600000000")
                .build();
        doNothing().when(bureauEtudesService).createBureauEtude(any());

        // WHEN / THEN
        mockMvc.perform(post("/bureauEtude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bureau)))
                .andExpect(status().isOk());

        verify(bureauEtudesService).createBureauEtude(any());
    }

    @Test
    void updateBureauEtude_shouldReturn200() throws Exception {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .raisonSociale("GeoTest Updated")
                .emailContact("updated@geotest.fr")
                .telContact("0611111111")
                .build();
        doNothing().when(bureauEtudesService).updateBureauEtude(any());

        // WHEN / THEN
        mockMvc.perform(put("/bureauEtude")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bureau)))
                .andExpect(status().isOk());

        verify(bureauEtudesService).updateBureauEtude(any());
    }

    @Test
    void deleteBureauEtude_shouldReturn200() throws Exception {
        // GIVEN
        doNothing().when(bureauEtudesService).deleteBureauEtude(1L);

        // WHEN / THEN
        mockMvc.perform(delete("/bureauEtude/1"))
                .andExpect(status().isOk());

        verify(bureauEtudesService).deleteBureauEtude(1L);
    }
}
