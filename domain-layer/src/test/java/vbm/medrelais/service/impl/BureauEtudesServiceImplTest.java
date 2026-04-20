package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.BureauEtudesServiceException;
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.port.BureauEtudeRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BureauEtudesServiceImplTest {

    @Mock
    private BureauEtudeRepository bureauEtudeRepository;

    @InjectMocks
    private BureauEtudesServiceImpl bureauEtudesService;

    // =========================================================
    // getAllBureauEtude
    // =========================================================

    @Test
    void getAllBureauEtude_whenListNotEmpty_shouldReturnList() {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .telContact("0600000000")
                .build();
        when(bureauEtudeRepository.getAllBureauEtude()).thenReturn(List.of(bureau));

        // WHEN
        List<BureauEtudesBO> result = bureauEtudesService.getAllBureauEtude();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRaisonSociale()).isEqualTo("GeoTest SARL");
        verify(bureauEtudeRepository).getAllBureauEtude();
    }

    @Test
    void getAllBureauEtude_whenListEmpty_shouldReturnEmptyList() {
        // GIVEN
        when(bureauEtudeRepository.getAllBureauEtude()).thenReturn(Collections.emptyList());

        // WHEN
        List<BureauEtudesBO> result = bureauEtudesService.getAllBureauEtude();

        // THEN
        assertThat(result).isEmpty();
        verify(bureauEtudeRepository).getAllBureauEtude();
    }

    // =========================================================
    // getBureauEtudeByID
    // =========================================================

    @Test
    void getBureauEtudeByID_whenFound_shouldReturnBO() {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .build();
        when(bureauEtudeRepository.getBureauEtudeByID(1L)).thenReturn(bureau);

        // WHEN
        BureauEtudesBO result = bureauEtudesService.getBureauEtudeByID(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRaisonSociale()).isEqualTo("GeoTest SARL");
        verify(bureauEtudeRepository).getBureauEtudeByID(1L);
    }

    @Test
    void getBureauEtudeByID_whenNotFound_shouldReturnNull() {
        // GIVEN
        when(bureauEtudeRepository.getBureauEtudeByID(99L)).thenReturn(null);

        // WHEN
        BureauEtudesBO result = bureauEtudesService.getBureauEtudeByID(99L);

        // THEN
        assertThat(result).isNull();
        verify(bureauEtudeRepository).getBureauEtudeByID(99L);
    }

    // =========================================================
    // createBureauEtude
    // =========================================================

    @Test
    void createBureauEtude_shouldDelegateToRepository() {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .raisonSociale("GeoTest SARL")
                .emailContact("contact@geotest.fr")
                .telContact("0600000000")
                .build();
        doNothing().when(bureauEtudeRepository).createBureauEtude(bureau);

        // WHEN
        bureauEtudesService.createBureauEtude(bureau);

        // THEN
        verify(bureauEtudeRepository).createBureauEtude(bureau);
    }

    // =========================================================
    // updateBureauEtude
    // =========================================================

    @Test
    void updateBureauEtude_whenExists_shouldDelegateToRepository() {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(1L)
                .raisonSociale("GeoTest Updated")
                .emailContact("updated@geotest.fr")
                .telContact("0611111111")
                .build();
        when(bureauEtudeRepository.getBureauEtudeByID(1L)).thenReturn(bureau);
        doNothing().when(bureauEtudeRepository).updateBureauEtude(bureau);

        // WHEN
        bureauEtudesService.updateBureauEtude(bureau);

        // THEN
        verify(bureauEtudeRepository).getBureauEtudeByID(1L);
        verify(bureauEtudeRepository).updateBureauEtude(bureau);
    }

    @Test
    void updateBureauEtude_whenNotFound_shouldThrowException() {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(99L)
                .raisonSociale("Inexistant")
                .build();
        when(bureauEtudeRepository.getBureauEtudeByID(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> bureauEtudesService.updateBureauEtude(bureau))
                .isInstanceOf(BureauEtudesServiceException.class)
                .hasMessageContaining("99");

        verify(bureauEtudeRepository).getBureauEtudeByID(99L);
        verify(bureauEtudeRepository, never()).updateBureauEtude(any());
    }

    // =========================================================
    // deleteBureauEtude
    // =========================================================

    @Test
    void deleteBureauEtude_whenExists_shouldDelegateToRepository() {
        // GIVEN
        BureauEtudesBO bureau = BureauEtudesBO.builder()
                .id(1L)
                .raisonSociale("GeoTest SARL")
                .build();
        when(bureauEtudeRepository.getBureauEtudeByID(1L)).thenReturn(bureau);
        doNothing().when(bureauEtudeRepository).deleteBureauEtude(1L);

        // WHEN
        bureauEtudesService.deleteBureauEtude(1L);

        // THEN
        verify(bureauEtudeRepository).getBureauEtudeByID(1L);
        verify(bureauEtudeRepository).deleteBureauEtude(1L);
    }

    @Test
    void deleteBureauEtude_whenNotFound_shouldThrowException() {
        // GIVEN
        when(bureauEtudeRepository.getBureauEtudeByID(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> bureauEtudesService.deleteBureauEtude(99L))
                .isInstanceOf(BureauEtudesServiceException.class)
                .hasMessageContaining("99");

        verify(bureauEtudeRepository).getBureauEtudeByID(99L);
        verify(bureauEtudeRepository, never()).deleteBureauEtude(any());
    }
}

