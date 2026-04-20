package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.DemandeDevisServiceException;
import vbm.medrelais.model.AdresseBO;
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.model.DemandeDevisBO;
import vbm.medrelais.port.DemandeDevisRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemandeDevisServiceImplTest {

    @Mock
    private DemandeDevisRepository demandeDevisRepository;

    @InjectMocks
    private DemandeDevisServiceImpl demandeDevisService;

    // =========================================================
    // getAllDemandeDevis
    // =========================================================

    @Test
    void getAllDemandeDevis_whenListNotEmpty_shouldReturnList() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(1L);
        when(demandeDevisRepository.getAllDemandeDevis()).thenReturn(List.of(demande));

        // WHEN
        List<DemandeDevisBO> result = demandeDevisService.getAllDemandeDevis();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(demandeDevisRepository).getAllDemandeDevis();
    }

    @Test
    void getAllDemandeDevis_whenListEmpty_shouldReturnEmptyList() {
        // GIVEN
        when(demandeDevisRepository.getAllDemandeDevis()).thenReturn(Collections.emptyList());

        // WHEN
        List<DemandeDevisBO> result = demandeDevisService.getAllDemandeDevis();

        // THEN
        assertThat(result).isEmpty();
        verify(demandeDevisRepository).getAllDemandeDevis();
    }

    // =========================================================
    // getDemandeDevisById
    // =========================================================

    @Test
    void getDemandeDevisById_whenFound_shouldReturnBO() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(1L);
        when(demandeDevisRepository.getDemandeDevisById(1L)).thenReturn(demande);

        // WHEN
        DemandeDevisBO result = demandeDevisService.getDemandeDevisById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(demandeDevisRepository).getDemandeDevisById(1L);
    }

    @Test
    void getDemandeDevisById_whenNotFound_shouldReturnNull() {
        // GIVEN
        when(demandeDevisRepository.getDemandeDevisById(99L)).thenReturn(null);

        // WHEN
        DemandeDevisBO result = demandeDevisService.getDemandeDevisById(99L);

        // THEN
        assertThat(result).isNull();
        verify(demandeDevisRepository).getDemandeDevisById(99L);
    }

    // =========================================================
    // getAllDemandeDevisByClientId
    // =========================================================

    @Test
    void getAllDemandeDevisByClientId_whenFound_shouldReturnList() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(1L);
        when(demandeDevisRepository.getAllDemandeDevisByClientId(1L)).thenReturn(List.of(demande));

        // WHEN
        List<DemandeDevisBO> result = demandeDevisService.getAllDemandeDevisByClientId(1L);

        // THEN
        assertThat(result).hasSize(1);
        verify(demandeDevisRepository).getAllDemandeDevisByClientId(1L);
    }

    @Test
    void getAllDemandeDevisByClientId_whenNoneFound_shouldReturnEmptyList() {
        // GIVEN
        when(demandeDevisRepository.getAllDemandeDevisByClientId(99L)).thenReturn(Collections.emptyList());

        // WHEN
        List<DemandeDevisBO> result = demandeDevisService.getAllDemandeDevisByClientId(99L);

        // THEN
        assertThat(result).isEmpty();
        verify(demandeDevisRepository).getAllDemandeDevisByClientId(99L);
    }

    // =========================================================
    // createDemandeDevis
    // =========================================================

    @Test
    void createDemandeDevis_shouldDelegateToRepository() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(null);
        doNothing().when(demandeDevisRepository).createDemandeDevis(demande);

        // WHEN
        demandeDevisService.createDemandeDevis(demande);

        // THEN
        verify(demandeDevisRepository).createDemandeDevis(demande);
    }

    // =========================================================
    // updateDemandeDevis
    // =========================================================

    @Test
    void updateDemandeDevis_whenExists_shouldDelegateToRepository() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(1L);
        when(demandeDevisRepository.getDemandeDevisById(1L)).thenReturn(demande);
        doNothing().when(demandeDevisRepository).updateDemandeDevis(demande);

        // WHEN
        demandeDevisService.updateDemandeDevis(demande);

        // THEN
        verify(demandeDevisRepository).getDemandeDevisById(1L);
        verify(demandeDevisRepository).updateDemandeDevis(demande);
    }

    @Test
    void updateDemandeDevis_whenNotFound_shouldThrowException() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(99L);
        when(demandeDevisRepository.getDemandeDevisById(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> demandeDevisService.updateDemandeDevis(demande))
                .isInstanceOf(DemandeDevisServiceException.class)
                .hasMessageContaining("99");

        verify(demandeDevisRepository).getDemandeDevisById(99L);
        verify(demandeDevisRepository, never()).updateDemandeDevis(any());
    }

    // =========================================================
    // deleteDemandeDevis
    // =========================================================

    @Test
    void deleteDemandeDevis_whenExists_shouldDelegateToRepository() {
        // GIVEN
        DemandeDevisBO demande = buildDemande(1L);
        when(demandeDevisRepository.getDemandeDevisById(1L)).thenReturn(demande);
        doNothing().when(demandeDevisRepository).deleteDemandeDevis(1L);

        // WHEN
        demandeDevisService.deleteDemandeDevis(1L);

        // THEN
        verify(demandeDevisRepository).getDemandeDevisById(1L);
        verify(demandeDevisRepository).deleteDemandeDevis(1L);
    }

    @Test
    void deleteDemandeDevis_whenNotFound_shouldThrowException() {
        // GIVEN
        when(demandeDevisRepository.getDemandeDevisById(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> demandeDevisService.deleteDemandeDevis(99L))
                .isInstanceOf(DemandeDevisServiceException.class)
                .hasMessageContaining("99");

        verify(demandeDevisRepository).getDemandeDevisById(99L);
        verify(demandeDevisRepository, never()).deleteDemandeDevis(any());
    }

    // =========================================================
    // Helper
    // =========================================================

    private DemandeDevisBO buildDemande(Long id) {
        return DemandeDevisBO.builder()
                .id(id)
                .delaiMax(LocalDate.of(2026, 6, 30))
                .adresseProjet(AdresseBO.builder().rue("10 rue de Lyon").codePostal("69001").ville("Lyon").build())
                .client(ClientBO.builder().id(1L).nom("Dupont").prenom("Jean").build())
                .build();
    }
}

