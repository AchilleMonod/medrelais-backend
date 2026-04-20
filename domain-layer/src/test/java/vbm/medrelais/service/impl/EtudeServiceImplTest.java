package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.EtudeServiceException;
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.model.EtudeBO;
import vbm.medrelais.model.enums.EtatEtudeEnum;
import vbm.medrelais.port.EtudeRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtudeServiceImplTest {

    @Mock
    private EtudeRepository etudeRepository;

    @InjectMocks
    private EtudeServiceImpl etudeService;

    // =========================================================
    // getAllEtudes
    // =========================================================

    @Test
    void getAllEtudes_whenListNotEmpty_shouldReturnList() {
        // GIVEN
        EtudeBO etude = buildEtude(1L);
        when(etudeRepository.getAllEtudes()).thenReturn(List.of(etude));

        // WHEN
        List<EtudeBO> result = etudeService.getAllEtudes();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(etudeRepository).getAllEtudes();
    }

    @Test
    void getAllEtudes_whenListEmpty_shouldReturnEmptyList() {
        // GIVEN
        when(etudeRepository.getAllEtudes()).thenReturn(Collections.emptyList());

        // WHEN
        List<EtudeBO> result = etudeService.getAllEtudes();

        // THEN
        assertThat(result).isEmpty();
        verify(etudeRepository).getAllEtudes();
    }

    // =========================================================
    // getEtudeById
    // =========================================================

    @Test
    void getEtudeById_whenFound_shouldReturnBO() {
        // GIVEN
        EtudeBO etude = buildEtude(1L);
        when(etudeRepository.getEtudeById(1L)).thenReturn(etude);

        // WHEN
        EtudeBO result = etudeService.getEtudeById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEtat()).isEqualTo(EtatEtudeEnum.DEVIS_VALIDE);
        verify(etudeRepository).getEtudeById(1L);
    }

    @Test
    void getEtudeById_whenNotFound_shouldReturnNull() {
        // GIVEN
        when(etudeRepository.getEtudeById(99L)).thenReturn(null);

        // WHEN
        EtudeBO result = etudeService.getEtudeById(99L);

        // THEN
        assertThat(result).isNull();
        verify(etudeRepository).getEtudeById(99L);
    }

    // =========================================================
    // getAllEtudesByBureauEtudeId
    // =========================================================

    @Test
    void getAllEtudesByBureauEtudeId_whenFound_shouldReturnList() {
        // GIVEN
        EtudeBO etude = buildEtude(1L);
        when(etudeRepository.getAllEtudesByBureauEtudeId(1L)).thenReturn(List.of(etude));

        // WHEN
        List<EtudeBO> result = etudeService.getAllEtudesByBureauEtudeId(1L);

        // THEN
        assertThat(result).hasSize(1);
        verify(etudeRepository).getAllEtudesByBureauEtudeId(1L);
    }

    @Test
    void getAllEtudesByBureauEtudeId_whenNoneFound_shouldReturnEmptyList() {
        // GIVEN
        when(etudeRepository.getAllEtudesByBureauEtudeId(99L)).thenReturn(Collections.emptyList());

        // WHEN
        List<EtudeBO> result = etudeService.getAllEtudesByBureauEtudeId(99L);

        // THEN
        assertThat(result).isEmpty();
        verify(etudeRepository).getAllEtudesByBureauEtudeId(99L);
    }

    // =========================================================
    // getAllEtudesByClientId
    // =========================================================

    @Test
    void getAllEtudesByClientId_whenFound_shouldReturnList() {
        // GIVEN
        EtudeBO etude = buildEtude(1L);
        when(etudeRepository.getAllEtudesByClientId(1L)).thenReturn(List.of(etude));

        // WHEN
        List<EtudeBO> result = etudeService.getAllEtudesByClientId(1L);

        // THEN
        assertThat(result).hasSize(1);
        verify(etudeRepository).getAllEtudesByClientId(1L);
    }

    @Test
    void getAllEtudesByClientId_whenNoneFound_shouldReturnEmptyList() {
        // GIVEN
        when(etudeRepository.getAllEtudesByClientId(99L)).thenReturn(Collections.emptyList());

        // WHEN
        List<EtudeBO> result = etudeService.getAllEtudesByClientId(99L);

        // THEN
        assertThat(result).isEmpty();
        verify(etudeRepository).getAllEtudesByClientId(99L);
    }

    // =========================================================
    // createEtude
    // =========================================================

    @Test
    void createEtude_shouldDelegateToRepository() {
        // GIVEN
        EtudeBO etude = buildEtude(null);
        doNothing().when(etudeRepository).createEtude(etude);

        // WHEN
        etudeService.createEtude(etude);

        // THEN
        verify(etudeRepository).createEtude(etude);
    }

    // =========================================================
    // updateEtude
    // =========================================================

    @Test
    void updateEtude_whenExists_shouldDelegateToRepository() {
        // GIVEN
        EtudeBO etude = buildEtude(1L);
        when(etudeRepository.getEtudeById(1L)).thenReturn(etude);
        doNothing().when(etudeRepository).updateEtude(etude);

        // WHEN
        etudeService.updateEtude(etude);

        // THEN
        verify(etudeRepository).getEtudeById(1L);
        verify(etudeRepository).updateEtude(etude);
    }

    @Test
    void updateEtude_whenNotFound_shouldThrowException() {
        // GIVEN
        EtudeBO etude = buildEtude(99L);
        when(etudeRepository.getEtudeById(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> etudeService.updateEtude(etude))
                .isInstanceOf(EtudeServiceException.class)
                .hasMessageContaining("99");

        verify(etudeRepository).getEtudeById(99L);
        verify(etudeRepository, never()).updateEtude(any());
    }

    // =========================================================
    // deleteEtude
    // =========================================================

    @Test
    void deleteEtude_whenExists_shouldDelegateToRepository() {
        // GIVEN
        EtudeBO etude = buildEtude(1L);
        when(etudeRepository.getEtudeById(1L)).thenReturn(etude);
        doNothing().when(etudeRepository).deleteEtude(1L);

        // WHEN
        etudeService.deleteEtude(1L);

        // THEN
        verify(etudeRepository).getEtudeById(1L);
        verify(etudeRepository).deleteEtude(1L);
    }

    @Test
    void deleteEtude_whenNotFound_shouldThrowException() {
        // GIVEN
        when(etudeRepository.getEtudeById(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> etudeService.deleteEtude(99L))
                .isInstanceOf(EtudeServiceException.class)
                .hasMessageContaining("99");

        verify(etudeRepository).getEtudeById(99L);
        verify(etudeRepository, never()).deleteEtude(any());
    }

    // =========================================================
    // Helper
    // =========================================================

    private EtudeBO buildEtude(Long id) {
        return EtudeBO.builder()
                .id(id)
                .etat(EtatEtudeEnum.DEVIS_VALIDE)
                .bureauEtude(BureauEtudesBO.builder().id(1L).raisonSociale("GeoTest SARL").build())
                .client(ClientBO.builder().id(1L).nom("Dupont").prenom("Jean").build())
                .build();
    }
}

