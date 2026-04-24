package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.port.CreneauRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreneauServiceImplTest {

    @Mock
    private CreneauRepository creneauRepository;

    @InjectMocks
    private CreneauServiceImpl creneauService;

    // =========================================================
    // Helpers
    // =========================================================

    private PraticienBO praticien(Long id) {
        return PraticienBO.builder().id(id).email("p@test.com").build();
    }

    private CreneauBO buildCreneau(Long id, StatutCreneau statut) {
        return CreneauBO.builder()
                .id(id)
                .praticien(praticien(10L))
                .dateDebut(LocalDateTime.of(2026, 5, 1, 9, 0))
                .dateFin(LocalDateTime.of(2026, 5, 1, 12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .statut(statut)
                .build();
    }

    // =========================================================
    // getById
    // =========================================================

    @Test
    void getById_whenFound_shouldReturnBO() {
        // GIVEN
        CreneauBO creneau = buildCreneau(1L, StatutCreneau.DISPONIBLE);
        when(creneauRepository.findById(1L)).thenReturn(Optional.of(creneau));

        // WHEN
        CreneauBO result = creneauService.getById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(creneauRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_shouldThrowCreneauNotFoundException() {
        // GIVEN
        when(creneauRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> creneauService.getById(99L))
                .isInstanceOf(CreneauNotFoundException.class);
    }

    // =========================================================
    // getByPraticienId
    // =========================================================

    @Test
    void getByPraticienId_whenCreneauxExist_shouldReturnList() {
        // GIVEN
        when(creneauRepository.findByPraticienId(10L))
                .thenReturn(List.of(buildCreneau(1L, StatutCreneau.DISPONIBLE)));

        // WHEN
        List<CreneauBO> result = creneauService.getByPraticienId(10L);

        // THEN
        assertThat(result).hasSize(1);
        verify(creneauRepository).findByPraticienId(10L);
    }

    @Test
    void getByPraticienId_whenNoCreneaux_shouldReturnEmptyList() {
        // GIVEN
        when(creneauRepository.findByPraticienId(10L)).thenReturn(Collections.emptyList());

        // WHEN
        List<CreneauBO> result = creneauService.getByPraticienId(10L);

        // THEN
        assertThat(result).isEmpty();
    }

    // =========================================================
    // getByPraticienIdAndStatut
    // =========================================================

    @Test
    void getByPraticienIdAndStatut_shouldDelegateToRepository() {
        // GIVEN
        when(creneauRepository.findByPraticienIdAndStatut(10L, StatutCreneau.DISPONIBLE))
                .thenReturn(List.of(buildCreneau(1L, StatutCreneau.DISPONIBLE)));

        // WHEN
        List<CreneauBO> result = creneauService.getByPraticienIdAndStatut(10L, StatutCreneau.DISPONIBLE);

        // THEN
        assertThat(result).hasSize(1);
        verify(creneauRepository).findByPraticienIdAndStatut(10L, StatutCreneau.DISPONIBLE);
    }

    // =========================================================
    // getDisponiblesByPeriode
    // =========================================================

    @Test
    void getDisponiblesByPeriode_whenCreneauxExist_shouldReturnList() {
        // GIVEN
        LocalDateTime debut = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime fin   = LocalDateTime.of(2026, 5, 31, 23, 59);
        when(creneauRepository.findDisponiblesByPeriode(debut, fin))
                .thenReturn(List.of(buildCreneau(1L, StatutCreneau.DISPONIBLE)));

        // WHEN
        List<CreneauBO> result = creneauService.getDisponiblesByPeriode(debut, fin);

        // THEN
        assertThat(result).hasSize(1);
        verify(creneauRepository).findDisponiblesByPeriode(debut, fin);
    }

    @Test
    void getDisponiblesByPeriode_whenNone_shouldReturnEmptyList() {
        // GIVEN
        LocalDateTime debut = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime fin   = LocalDateTime.of(2026, 5, 31, 23, 59);
        when(creneauRepository.findDisponiblesByPeriode(debut, fin)).thenReturn(Collections.emptyList());

        // WHEN
        List<CreneauBO> result = creneauService.getDisponiblesByPeriode(debut, fin);

        // THEN
        assertThat(result).isEmpty();
    }

    // =========================================================
    // create
    // =========================================================

    @Test
    void create_shouldForceStatutDisponibleAndSave() {
        // GIVEN
        CreneauBO input = buildCreneau(null, StatutCreneau.EN_ATTENTE); // statut incorrect en entrée
        CreneauBO saved = buildCreneau(1L, StatutCreneau.DISPONIBLE);
        when(creneauRepository.save(any())).thenReturn(saved);

        // WHEN
        CreneauBO result = creneauService.create(input);

        // THEN
        verify(creneauRepository).save(argThat(c -> c.getStatut() == StatutCreneau.DISPONIBLE));
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatut()).isEqualTo(StatutCreneau.DISPONIBLE);
    }

    // =========================================================
    // update
    // =========================================================

    @Test
    void update_whenFound_shouldPreserveIdAndCreatedAtAndSave() {
        // GIVEN
        CreneauBO existing = buildCreneau(1L, StatutCreneau.DISPONIBLE);
        existing.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));

        CreneauBO input = buildCreneau(null, StatutCreneau.DISPONIBLE);
        CreneauBO saved = buildCreneau(1L, StatutCreneau.DISPONIBLE);

        when(creneauRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(creneauRepository.save(any())).thenReturn(saved);

        // WHEN
        CreneauBO result = creneauService.update(1L, input);

        // THEN
        verify(creneauRepository).save(argThat(c ->
                c.getId().equals(1L)
                && c.getCreatedAt().equals(LocalDateTime.of(2026, 1, 1, 0, 0))
        ));
        assertThat(result).isNotNull();
    }

    @Test
    void update_whenNotFound_shouldThrowCreneauNotFoundException() {
        // GIVEN
        when(creneauRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> creneauService.update(99L, buildCreneau(null, StatutCreneau.DISPONIBLE)))
                .isInstanceOf(CreneauNotFoundException.class);
        verify(creneauRepository, never()).save(any());
    }

    // =========================================================
    // updateStatut
    // =========================================================

    @Test
    void updateStatut_whenFound_shouldChangeStatutAndSave() {
        // GIVEN
        CreneauBO existing = buildCreneau(1L, StatutCreneau.DISPONIBLE);
        CreneauBO saved    = buildCreneau(1L, StatutCreneau.EN_ATTENTE);
        when(creneauRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(creneauRepository.save(any())).thenReturn(saved);

        // WHEN
        CreneauBO result = creneauService.updateStatut(1L, StatutCreneau.EN_ATTENTE);

        // THEN
        verify(creneauRepository).save(argThat(c -> c.getStatut() == StatutCreneau.EN_ATTENTE));
        assertThat(result.getStatut()).isEqualTo(StatutCreneau.EN_ATTENTE);
    }

    @Test
    void updateStatut_whenNotFound_shouldThrowCreneauNotFoundException() {
        // GIVEN
        when(creneauRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> creneauService.updateStatut(99L, StatutCreneau.ATTRIBUE))
                .isInstanceOf(CreneauNotFoundException.class);
    }

    // =========================================================
    // delete
    // =========================================================

    @Test
    void delete_whenFound_shouldCallDeleteById() {
        // GIVEN
        when(creneauRepository.findById(1L)).thenReturn(Optional.of(buildCreneau(1L, StatutCreneau.DISPONIBLE)));

        // WHEN
        creneauService.delete(1L);

        // THEN
        verify(creneauRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowCreneauNotFoundException() {
        // GIVEN
        when(creneauRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> creneauService.delete(99L))
                .isInstanceOf(CreneauNotFoundException.class);
        verify(creneauRepository, never()).deleteById(any());
    }
}

