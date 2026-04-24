package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.PraticienNotFoundException;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.Role;
import vbm.medrelais.port.PraticienRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PraticienServiceImplTest {

    @Mock
    private PraticienRepository praticienRepository;

    @InjectMocks
    private PraticienServiceImpl praticienService;

    // =========================================================
    // Helpers
    // =========================================================

    private PraticienBO buildPraticien(Long id) {
        return PraticienBO.builder()
                .id(id)
                .email("praticien" + id + "@test.com")
                .nom("Dupont")
                .prenom("Paul")
                .specialite("Médecine générale")
                .adresseVille("Paris")
                .build();
    }

    // =========================================================
    // getById
    // =========================================================

    @Test
    void getById_whenFound_shouldReturnBO() {
        // GIVEN
        PraticienBO praticien = buildPraticien(1L);
        when(praticienRepository.findById(1L)).thenReturn(Optional.of(praticien));

        // WHEN
        PraticienBO result = praticienService.getById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(praticienRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_shouldThrowPraticienNotFoundException() {
        // GIVEN
        when(praticienRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> praticienService.getById(99L))
                .isInstanceOf(PraticienNotFoundException.class);
        verify(praticienRepository).findById(99L);
    }

    // =========================================================
    // getAll
    // =========================================================

    @Test
    void getAll_whenListNotEmpty_shouldReturnList() {
        // GIVEN
        when(praticienRepository.findAll()).thenReturn(List.of(buildPraticien(1L), buildPraticien(2L)));

        // WHEN
        List<PraticienBO> result = praticienService.getAll();

        // THEN
        assertThat(result).hasSize(2);
        verify(praticienRepository).findAll();
    }

    @Test
    void getAll_whenListEmpty_shouldReturnEmptyList() {
        // GIVEN
        when(praticienRepository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<PraticienBO> result = praticienService.getAll();

        // THEN
        assertThat(result).isEmpty();
        verify(praticienRepository).findAll();
    }

    // =========================================================
    // getBySpecialite
    // =========================================================

    @Test
    void getBySpecialite_whenMatches_shouldReturnList() {
        // GIVEN
        when(praticienRepository.findBySpecialite("Cardiologie"))
                .thenReturn(List.of(buildPraticien(1L)));

        // WHEN
        List<PraticienBO> result = praticienService.getBySpecialite("Cardiologie");

        // THEN
        assertThat(result).hasSize(1);
        verify(praticienRepository).findBySpecialite("Cardiologie");
    }

    @Test
    void getBySpecialite_whenNoMatch_shouldReturnEmptyList() {
        // GIVEN
        when(praticienRepository.findBySpecialite("Neurologie")).thenReturn(Collections.emptyList());

        // WHEN
        List<PraticienBO> result = praticienService.getBySpecialite("Neurologie");

        // THEN
        assertThat(result).isEmpty();
    }

    // =========================================================
    // getByVille
    // =========================================================

    @Test
    void getByVille_whenMatches_shouldReturnList() {
        // GIVEN
        when(praticienRepository.findByVille("Lyon")).thenReturn(List.of(buildPraticien(1L)));

        // WHEN
        List<PraticienBO> result = praticienService.getByVille("Lyon");

        // THEN
        assertThat(result).hasSize(1);
        verify(praticienRepository).findByVille("Lyon");
    }

    @Test
    void getByVille_whenNoMatch_shouldReturnEmptyList() {
        // GIVEN
        when(praticienRepository.findByVille("Bordeaux")).thenReturn(Collections.emptyList());

        // WHEN
        List<PraticienBO> result = praticienService.getByVille("Bordeaux");

        // THEN
        assertThat(result).isEmpty();
    }

    // =========================================================
    // getNearby
    // =========================================================

    @Test
    void getNearby_whenMatches_shouldReturnList() {
        // GIVEN
        when(praticienRepository.findNearby(48.85, 2.35, 10.0))
                .thenReturn(List.of(buildPraticien(1L)));

        // WHEN
        List<PraticienBO> result = praticienService.getNearby(48.85, 2.35, 10.0);

        // THEN
        assertThat(result).hasSize(1);
        verify(praticienRepository).findNearby(48.85, 2.35, 10.0);
    }

    @Test
    void getNearby_whenNoMatch_shouldReturnEmptyList() {
        // GIVEN
        when(praticienRepository.findNearby(0.0, 0.0, 1.0)).thenReturn(Collections.emptyList());

        // WHEN
        List<PraticienBO> result = praticienService.getNearby(0.0, 0.0, 1.0);

        // THEN
        assertThat(result).isEmpty();
    }

    // =========================================================
    // create
    // =========================================================

    @Test
    void create_shouldForceRolePraticienAndSave() {
        // GIVEN
        PraticienBO input = buildPraticien(null);
        PraticienBO saved = buildPraticien(1L);
        saved.setRole(Role.PRATICIEN);
        when(praticienRepository.save(any())).thenReturn(saved);

        // WHEN
        PraticienBO result = praticienService.create(input);

        // THEN
        // Le rôle doit avoir été forcé avant l'appel au repository
        verify(praticienRepository).save(argThat(p -> p.getRole() == Role.PRATICIEN));
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRole()).isEqualTo(Role.PRATICIEN);
    }

    // =========================================================
    // update
    // =========================================================

    @Test
    void update_whenFound_shouldPreserveEmailRoleCreatedAtAndSave() {
        // GIVEN
        PraticienBO existing = buildPraticien(1L);
        existing.setRole(Role.PRATICIEN);
        existing.setEmail("original@test.com");

        PraticienBO input = PraticienBO.builder()
                .nom("Martin")
                .prenom("Jean")
                .specialite("Pédiatrie")
                .email("new@test.com")  // doit être ignoré
                .build();

        PraticienBO saved = buildPraticien(1L);
        saved.setNom("Martin");

        when(praticienRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(praticienRepository.save(any())).thenReturn(saved);

        // WHEN
        PraticienBO result = praticienService.update(1L, input);

        // THEN
        verify(praticienRepository).save(argThat(p ->
                p.getId().equals(1L)
                && p.getEmail().equals("original@test.com")  // email préservé
                && p.getRole() == Role.PRATICIEN              // rôle préservé
        ));
        assertThat(result).isNotNull();
    }

    @Test
    void update_whenNotFound_shouldThrowPraticienNotFoundException() {
        // GIVEN
        when(praticienRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> praticienService.update(99L, buildPraticien(null)))
                .isInstanceOf(PraticienNotFoundException.class);
        verify(praticienRepository, never()).save(any());
    }

    // =========================================================
    // delete
    // =========================================================

    @Test
    void delete_whenFound_shouldCallDeleteById() {
        // GIVEN
        when(praticienRepository.findById(1L)).thenReturn(Optional.of(buildPraticien(1L)));

        // WHEN
        praticienService.delete(1L);

        // THEN
        verify(praticienRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowPraticienNotFoundException() {
        // GIVEN
        when(praticienRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> praticienService.delete(99L))
                .isInstanceOf(PraticienNotFoundException.class);
        verify(praticienRepository, never()).deleteById(any());
    }
}

