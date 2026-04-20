package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.ClientServiceException;
import vbm.medrelais.model.AdresseBO;
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.port.ClientRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    // =========================================================
    // getAllClients
    // =========================================================

    @Test
    void getAllClients_whenListNotEmpty_shouldReturnList() {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .build();
        when(clientRepository.getAllClients()).thenReturn(List.of(client));

        // WHEN
        List<ClientBO> result = clientService.getAllClients();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Dupont");
        verify(clientRepository).getAllClients();
    }

    @Test
    void getAllClients_whenListEmpty_shouldReturnEmptyList() {
        // GIVEN
        when(clientRepository.getAllClients()).thenReturn(Collections.emptyList());

        // WHEN
        List<ClientBO> result = clientService.getAllClients();

        // THEN
        assertThat(result).isEmpty();
        verify(clientRepository).getAllClients();
    }

    // =========================================================
    // getClientById
    // =========================================================

    @Test
    void getClientById_whenFound_shouldReturnBO() {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .id(1L)
                .nom("Dupont")
                .prenom("Jean")
                .build();
        when(clientRepository.getClientByID(1L)).thenReturn(client);

        // WHEN
        ClientBO result = clientService.getClientById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNom()).isEqualTo("Dupont");
        verify(clientRepository).getClientByID(1L);
    }

    @Test
    void getClientById_whenNotFound_shouldReturnNull() {
        // GIVEN
        when(clientRepository.getClientByID(99L)).thenReturn(null);

        // WHEN
        ClientBO result = clientService.getClientById(99L);

        // THEN
        assertThat(result).isNull();
        verify(clientRepository).getClientByID(99L);
    }

    // =========================================================
    // createClient
    // =========================================================

    @Test
    void createClient_shouldDelegateToRepository() {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .nom("Dupont")
                .prenom("Jean")
                .adresseFacturation(AdresseBO.builder().rue("1 rue de la Paix").codePostal("75001").ville("Paris").build())
                .build();
        doNothing().when(clientRepository).createClient(client);

        // WHEN
        clientService.createClient(client);

        // THEN
        verify(clientRepository).createClient(client);
    }

    // =========================================================
    // updateClient
    // =========================================================

    @Test
    void updateClient_whenExists_shouldDelegateToRepository() {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .id(1L)
                .nom("Dupont Updated")
                .prenom("Jean")
                .build();
        when(clientRepository.getClientByID(1L)).thenReturn(client);
        doNothing().when(clientRepository).updateClient(client);

        // WHEN
        clientService.updateClient(client);

        // THEN
        verify(clientRepository).getClientByID(1L);
        verify(clientRepository).updateClient(client);
    }

    @Test
    void updateClient_whenNotFound_shouldThrowException() {
        // GIVEN
        ClientBO client = ClientBO.builder()
                .id(99L)
                .nom("Inexistant")
                .build();
        when(clientRepository.getClientByID(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> clientService.updateClient(client))
                .isInstanceOf(ClientServiceException.class)
                .hasMessageContaining("99");

        verify(clientRepository).getClientByID(99L);
        verify(clientRepository, never()).updateClient(any());
    }

    // =========================================================
    // deleteClient
    // =========================================================

    @Test
    void deleteClient_whenExists_shouldDelegateToRepository() {
        // GIVEN
        ClientBO client = ClientBO.builder().id(1L).nom("Dupont").build();
        when(clientRepository.getClientByID(1L)).thenReturn(client);
        doNothing().when(clientRepository).deleteClient(1L);

        // WHEN
        clientService.deleteClient(1L);

        // THEN
        verify(clientRepository).getClientByID(1L);
        verify(clientRepository).deleteClient(1L);
    }

    @Test
    void deleteClient_whenNotFound_shouldThrowException() {
        // GIVEN
        when(clientRepository.getClientByID(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> clientService.deleteClient(99L))
                .isInstanceOf(ClientServiceException.class)
                .hasMessageContaining("99");

        verify(clientRepository).getClientByID(99L);
        verify(clientRepository, never()).deleteClient(any());
    }
}
