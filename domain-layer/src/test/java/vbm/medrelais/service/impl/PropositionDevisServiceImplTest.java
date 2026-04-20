package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.PropositionDevisServiceException;
import vbm.medrelais.model.BureauEtudesBO;
import vbm.medrelais.model.PropositionDevisBO;
import vbm.medrelais.port.PropositionDevisRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropositionDevisServiceImplTest {

    @Mock
    private PropositionDevisRepository propositionDevisRepository;

    @InjectMocks
    private PropositionDevisServiceImpl propositionDevisService;

    // =========================================================
    // getAllPropositionDevis
    // =========================================================

    @Test
    void getAllPropositionDevis_whenListNotEmpty_shouldReturnList() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(1L);
        when(propositionDevisRepository.getAllPropositionDevis()).thenReturn(List.of(proposition));

        // WHEN
        List<PropositionDevisBO> result = propositionDevisService.getAllPropositionDevis();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(propositionDevisRepository).getAllPropositionDevis();
    }

    @Test
    void getAllPropositionDevis_whenListEmpty_shouldReturnEmptyList() {
        // GIVEN
        when(propositionDevisRepository.getAllPropositionDevis()).thenReturn(Collections.emptyList());

        // WHEN
        List<PropositionDevisBO> result = propositionDevisService.getAllPropositionDevis();

        // THEN
        assertThat(result).isEmpty();
        verify(propositionDevisRepository).getAllPropositionDevis();
    }

    // =========================================================
    // getPropositionDevisById
    // =========================================================

    @Test
    void getPropositionDevisById_whenFound_shouldReturnBO() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(1L);
        when(propositionDevisRepository.getPropositionDevisById(1L)).thenReturn(proposition);

        // WHEN
        PropositionDevisBO result = propositionDevisService.getPropositionDevisById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrix()).isEqualByComparingTo("1500.00");
        verify(propositionDevisRepository).getPropositionDevisById(1L);
    }

    @Test
    void getPropositionDevisById_whenNotFound_shouldReturnNull() {
        // GIVEN
        when(propositionDevisRepository.getPropositionDevisById(99L)).thenReturn(null);

        // WHEN
        PropositionDevisBO result = propositionDevisService.getPropositionDevisById(99L);

        // THEN
        assertThat(result).isNull();
        verify(propositionDevisRepository).getPropositionDevisById(99L);
    }

    // =========================================================
    // getAllPropositionDevisByBureauEtudeId
    // =========================================================

    @Test
    void getAllPropositionDevisByBureauEtudeId_whenFound_shouldReturnList() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(1L);
        when(propositionDevisRepository.getAllPropositionDevisByBureauEtudeId(1L)).thenReturn(List.of(proposition));

        // WHEN
        List<PropositionDevisBO> result = propositionDevisService.getAllPropositionDevisByBureauEtudeId(1L);

        // THEN
        assertThat(result).hasSize(1);
        verify(propositionDevisRepository).getAllPropositionDevisByBureauEtudeId(1L);
    }

    @Test
    void getAllPropositionDevisByBureauEtudeId_whenNoneFound_shouldReturnEmptyList() {
        // GIVEN
        when(propositionDevisRepository.getAllPropositionDevisByBureauEtudeId(99L)).thenReturn(Collections.emptyList());

        // WHEN
        List<PropositionDevisBO> result = propositionDevisService.getAllPropositionDevisByBureauEtudeId(99L);

        // THEN
        assertThat(result).isEmpty();
        verify(propositionDevisRepository).getAllPropositionDevisByBureauEtudeId(99L);
    }

    // =========================================================
    // getAllPropositionDevisByDemandeDevisId
    // =========================================================

    @Test
    void getAllPropositionDevisByDemandeDevisId_whenFound_shouldReturnList() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(1L);
        when(propositionDevisRepository.getAllPropositionDevisByDemandeDevisId(1L)).thenReturn(List.of(proposition));

        // WHEN
        List<PropositionDevisBO> result = propositionDevisService.getAllPropositionDevisByDemandeDevisId(1L);

        // THEN
        assertThat(result).hasSize(1);
        verify(propositionDevisRepository).getAllPropositionDevisByDemandeDevisId(1L);
    }

    @Test
    void getAllPropositionDevisByDemandeDevisId_whenNoneFound_shouldReturnEmptyList() {
        // GIVEN
        when(propositionDevisRepository.getAllPropositionDevisByDemandeDevisId(99L)).thenReturn(Collections.emptyList());

        // WHEN
        List<PropositionDevisBO> result = propositionDevisService.getAllPropositionDevisByDemandeDevisId(99L);

        // THEN
        assertThat(result).isEmpty();
        verify(propositionDevisRepository).getAllPropositionDevisByDemandeDevisId(99L);
    }

    // =========================================================
    // createPropositionDevis
    // =========================================================

    @Test
    void createPropositionDevis_shouldDelegateToRepository() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(null);
        doNothing().when(propositionDevisRepository).createPropositionDevis(proposition);

        // WHEN
        propositionDevisService.createPropositionDevis(proposition);

        // THEN
        verify(propositionDevisRepository).createPropositionDevis(proposition);
    }

    // =========================================================
    // updatePropositionDevis
    // =========================================================

    @Test
    void updatePropositionDevis_whenExists_shouldDelegateToRepository() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(1L);
        when(propositionDevisRepository.getPropositionDevisById(1L)).thenReturn(proposition);
        doNothing().when(propositionDevisRepository).updatePropositionDevis(proposition);

        // WHEN
        propositionDevisService.updatePropositionDevis(proposition);

        // THEN
        verify(propositionDevisRepository).getPropositionDevisById(1L);
        verify(propositionDevisRepository).updatePropositionDevis(proposition);
    }

    @Test
    void updatePropositionDevis_whenNotFound_shouldThrowException() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(99L);
        when(propositionDevisRepository.getPropositionDevisById(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> propositionDevisService.updatePropositionDevis(proposition))
                .isInstanceOf(PropositionDevisServiceException.class)
                .hasMessageContaining("99");

        verify(propositionDevisRepository).getPropositionDevisById(99L);
        verify(propositionDevisRepository, never()).updatePropositionDevis(any());
    }

    // =========================================================
    // deletePropositionDevis
    // =========================================================

    @Test
    void deletePropositionDevis_whenExists_shouldDelegateToRepository() {
        // GIVEN
        PropositionDevisBO proposition = buildProposition(1L);
        when(propositionDevisRepository.getPropositionDevisById(1L)).thenReturn(proposition);
        doNothing().when(propositionDevisRepository).deletePropositionDevis(1L);

        // WHEN
        propositionDevisService.deletePropositionDevis(1L);

        // THEN
        verify(propositionDevisRepository).getPropositionDevisById(1L);
        verify(propositionDevisRepository).deletePropositionDevis(1L);
    }

    @Test
    void deletePropositionDevis_whenNotFound_shouldThrowException() {
        // GIVEN
        when(propositionDevisRepository.getPropositionDevisById(99L)).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> propositionDevisService.deletePropositionDevis(99L))
                .isInstanceOf(PropositionDevisServiceException.class)
                .hasMessageContaining("99");

        verify(propositionDevisRepository).getPropositionDevisById(99L);
        verify(propositionDevisRepository, never()).deletePropositionDevis(any());
    }

    // =========================================================
    // Helper
    // =========================================================

    private PropositionDevisBO buildProposition(Long id) {
        return PropositionDevisBO.builder()
                .id(id)
                .prix(new BigDecimal("1500.00"))
                .dateRendu(LocalDate.of(2026, 7, 15))
                .demandeDevisId(1L)
                .bureauEtude(BureauEtudesBO.builder().id(1L).raisonSociale("GeoTest SARL").build())
                .build();
    }
}

