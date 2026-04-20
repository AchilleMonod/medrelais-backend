package vbm.medrelais.webapp.resource.client;

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
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.security.JwtAuthenticationFilter;
import vbm.medrelais.security.configuration.SecurityConfig;
import vbm.medrelais.service.ClientService;
import vbm.medrelais.webapp.mapper.ClientMapper;
import vbm.medrelais.webapp.model.ClientDTO;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ClientController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
                )
        }
)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private ClientMapper clientMapper;

    // =========================================================
    // GET /client
    // =========================================================

    @Test
    void getAllClients_whenListNotEmpty_shouldReturn200WithList() throws Exception {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .build();
        ClientDTO clientDTO = ClientDTO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .build();
        when(clientService.getAllClients()).thenReturn(List.of(client));
        when(clientMapper.toDTO(any())).thenReturn(clientDTO);

        // WHEN / THEN
        mockMvc.perform(get("/client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nom").value("Dupont"));
    }

    @Test
    void getAllClients_whenListEmpty_shouldReturn204() throws Exception {
        // GIVEN
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());

        // WHEN / THEN
        mockMvc.perform(get("/client"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getClientById_whenFound_shouldReturn200WithClient() throws Exception {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .build();
        ClientDTO clientDTO = ClientDTO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .build();
        when(clientService.getClientById(1L)).thenReturn(client);
        when(clientMapper.toDTO(any())).thenReturn(clientDTO);

        // WHEN / THEN
        mockMvc.perform(get("/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Dupont"));
    }

    @Test
    void getClientById_whenNotFound_shouldReturn204() throws Exception {
        // GIVEN
        when(clientService.getClientById(99L)).thenReturn(null);

        // WHEN / THEN
        mockMvc.perform(get("/client/99"))
                .andExpect(status().isNoContent());
    }

    // =========================================================
    // POST /client
    // =========================================================

    @Test
    void createClient_shouldReturn200() throws Exception {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .nom("Dupont")
                .prenom("Jean")
                .build();
        doNothing().when(clientService).createClient(any());

        // WHEN / THEN
        mockMvc.perform(post("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk());

        verify(clientService).createClient(any());
    }

    @Test
    void updateClient_shouldReturn200() throws Exception {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .nom("Martin")
                .prenom("Paul")
                .build();
        doNothing().when(clientService).updateClient(any());

        // WHEN / THEN
        mockMvc.perform(put("/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk());

        verify(clientService).updateClient(any());
    }

    @Test
    void deleteClient_shouldReturn200() throws Exception {
        // GIVEN
        doNothing().when(clientService).deleteClient(1L);

        // WHEN / THEN
        mockMvc.perform(delete("/client/1"))
                .andExpect(status().isOk());

        verify(clientService).deleteClient(1L);
    }
}

