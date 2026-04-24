package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.RegleRecurrenceNotFoundException;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.RegleRecurrenceBO;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.model.enums.TypeRecurrence;
import vbm.medrelais.port.CreneauRepository;
import vbm.medrelais.port.RegleRecurrenceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegleRecurrenceServiceImplTest {

    @Mock
    private RegleRecurrenceRepository regleRecurrenceRepository;

    @Mock
    private CreneauRepository creneauRepository;

    @InjectMocks
    private RegleRecurrenceServiceImpl regleRecurrenceService;

    // =========================================================
    // Helpers
    // =========================================================

    private PraticienBO praticien() {
        return PraticienBO.builder().id(10L).email("p@test.com").build();
    }

    /**
     * Règle de base : tous les lundis (joursSemaine="1") sur 4 semaines.
     * dateDebut = lundi 2026-05-04 → lundis : 04/05, 11/05, 18/05, 25/05
     */
    private RegleRecurrenceBO.RegleRecurrenceBOBuilder regleBase() {
        return RegleRecurrenceBO.builder()
                .id(1L)
                .praticien(praticien())
                .typeRecurrence(TypeRecurrence.HEBDOMADAIRE)
                .intervalle(1)
                .joursSemaine("1") // lundi = 1
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .dateDebut(LocalDate.of(2026, 5, 4))  // lundi
                .dateFin(LocalDate.of(2026, 5, 31));
    }

    private CreneauBO disponible(Long id) {
        return CreneauBO.builder().id(id).statut(StatutCreneau.DISPONIBLE).build();
    }

    private CreneauBO enAttente(Long id) {
        return CreneauBO.builder().id(id).statut(StatutCreneau.EN_ATTENTE).build();
    }

    // =========================================================
    // getById
    // =========================================================

    @Test
    void getById_whenFound_shouldReturnBO() {
        // GIVEN
        RegleRecurrenceBO regle = regleBase().build();
        when(regleRecurrenceRepository.findById(1L)).thenReturn(Optional.of(regle));

        // WHEN
        RegleRecurrenceBO result = regleRecurrenceService.getById(1L);

        // THEN
        assertThat(result.getId()).isEqualTo(1L);
        verify(regleRecurrenceRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_shouldThrowRegleRecurrenceNotFoundException() {
        // GIVEN
        when(regleRecurrenceRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> regleRecurrenceService.getById(99L))
                .isInstanceOf(RegleRecurrenceNotFoundException.class);
    }

    // =========================================================
    // getByPraticienId
    // =========================================================

    @Test
    void getByPraticienId_shouldDelegateToRepository() {
        // GIVEN
        when(regleRecurrenceRepository.findByPraticienId(10L))
                .thenReturn(List.of(regleBase().build()));

        // WHEN
        List<RegleRecurrenceBO> result = regleRecurrenceService.getByPraticienId(10L);

        // THEN
        assertThat(result).hasSize(1);
        verify(regleRecurrenceRepository).findByPraticienId(10L);
    }

    // =========================================================
    // getActivesByPraticienId
    // =========================================================

    @Test
    void getActivesByPraticienId_shouldDelegateToRepository() {
        // GIVEN
        when(regleRecurrenceRepository.findActivesByPraticienId(10L))
                .thenReturn(List.of(regleBase().active(true).build()));

        // WHEN
        List<RegleRecurrenceBO> result = regleRecurrenceService.getActivesByPraticienId(10L);

        // THEN
        assertThat(result).hasSize(1);
        verify(regleRecurrenceRepository).findActivesByPraticienId(10L);
    }

    // =========================================================
    // create — génération de créneaux
    // =========================================================

    @Test
    void create_shouldForceActiveTrueEtGenererCreneaux() {
        // GIVEN
        RegleRecurrenceBO input = regleBase().active(false).id(null).build();
        RegleRecurrenceBO saved = regleBase().active(true).build(); // retourné par le repository

        when(regleRecurrenceRepository.save(any())).thenReturn(saved);
        when(creneauRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        RegleRecurrenceBO result = regleRecurrenceService.create(input);

        // THEN
        verify(regleRecurrenceRepository).save(argThat(r -> r.isActive()));
        assertThat(result.isActive()).isTrue();

        // 4 lundis en mai 2026 (04, 11, 18, 25)
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CreneauBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(creneauRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(4);
        captor.getValue().forEach(c -> {
            assertThat(c.getStatut()).isEqualTo(StatutCreneau.DISPONIBLE);
            assertThat(c.isEstException()).isFalse();
            assertThat(c.getPraticien().getId()).isEqualTo(10L);
        });
    }

    @Test
    void create_avecNombreOccurrences_shouldLimiterLesCreneaux() {
        // GIVEN — 2 occurrences maximum
        RegleRecurrenceBO input = regleBase().dateFin(null).nombreOccurrences(2).id(null).build();
        RegleRecurrenceBO saved = regleBase().dateFin(null).nombreOccurrences(2).build();

        when(regleRecurrenceRepository.save(any())).thenReturn(saved);
        when(creneauRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        regleRecurrenceService.create(input);

        // THEN
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CreneauBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(creneauRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void create_sansDateFinNiOccurrences_shouldGenererSur1An() {
        // GIVEN — aucun filtre de fin → génération sur 1 an (52 lundis à partir de 2026-05-04)
        RegleRecurrenceBO input = regleBase().dateFin(null).nombreOccurrences(null).id(null).build();
        RegleRecurrenceBO saved = regleBase().dateFin(null).nombreOccurrences(null).build();

        when(regleRecurrenceRepository.save(any())).thenReturn(saved);
        when(creneauRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        regleRecurrenceService.create(input);

        // THEN
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CreneauBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(creneauRepository).saveAll(captor.capture());
        // 52 ou 53 lundis selon l'année — on vérifie juste que c'est > 4
        assertThat(captor.getValue().size()).isGreaterThan(4);
    }

    @Test
    void create_avecJoursMois_shouldFiltrerParJourDuMois() {
        // GIVEN — tous les jours du mois = "4,11" → uniquement le 4 et 11 mai
        RegleRecurrenceBO input = RegleRecurrenceBO.builder()
                .praticien(praticien())
                .typeRecurrence(TypeRecurrence.MENSUEL)
                .intervalle(1)
                .joursMois("4,11")     // 4 et 11 mai
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .dateDebut(LocalDate.of(2026, 5, 1))
                .dateFin(LocalDate.of(2026, 5, 31))
                .build();
        RegleRecurrenceBO saved = RegleRecurrenceBO.builder()
                .id(1L).active(true)
                .praticien(praticien())
                .typeRecurrence(TypeRecurrence.MENSUEL)
                .intervalle(1)
                .joursMois("4,11")
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .dateDebut(LocalDate.of(2026, 5, 1))
                .dateFin(LocalDate.of(2026, 5, 31))
                .build();

        when(regleRecurrenceRepository.save(any())).thenReturn(saved);
        when(creneauRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        regleRecurrenceService.create(input);

        // THEN
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CreneauBO>> captor = ArgumentCaptor.forClass(List.class);
        verify(creneauRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void create_avecMoisAnnee_shouldFiltrerParMois() {
        // GIVEN — moisAnnee="6" → uniquement le mois de juin ; dateDebut en mai → 0 créneau
        RegleRecurrenceBO input = RegleRecurrenceBO.builder()
                .praticien(praticien())
                .typeRecurrence(TypeRecurrence.HEBDOMADAIRE)
                .intervalle(1)
                .moisAnnee("6") // uniquement juin
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .dateDebut(LocalDate.of(2026, 5, 1))
                .dateFin(LocalDate.of(2026, 5, 31)) // uniquement mai → 0 créneau
                .build();
        RegleRecurrenceBO saved = RegleRecurrenceBO.builder()
                .id(1L).active(true)
                .praticien(praticien())
                .typeRecurrence(TypeRecurrence.HEBDOMADAIRE)
                .intervalle(1)
                .moisAnnee("6")
                .heureDebut(LocalTime.of(9, 0))
                .heureFin(LocalTime.of(12, 0))
                .typeDuree(TypeDureeCreneau.MATIN)
                .dateDebut(LocalDate.of(2026, 5, 1))
                .dateFin(LocalDate.of(2026, 5, 31))
                .build();

        when(regleRecurrenceRepository.save(any())).thenReturn(saved);

        // WHEN
        regleRecurrenceService.create(input);

        // THEN — aucun créneau généré, saveAll ne doit pas être appelé
        verify(creneauRepository, never()).saveAll(any());
    }

    // =========================================================
    // update
    // =========================================================

    @Test
    void update_whenFound_shouldSupprimerDisponiblesEtRegenerer() {
        // GIVEN
        RegleRecurrenceBO existing = regleBase().build();
        existing.setCreatedAt(java.time.LocalDateTime.of(2026, 1, 1, 0, 0));

        CreneauBO disp    = disponible(1L);
        CreneauBO attente = enAttente(2L); // ne doit pas être supprimé

        when(regleRecurrenceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(creneauRepository.findByRegleRecurrenceId(1L)).thenReturn(List.of(disp, attente));
        when(regleRecurrenceRepository.save(any())).thenReturn(regleBase().build());
        when(creneauRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        RegleRecurrenceBO input = regleBase().build();

        // WHEN
        regleRecurrenceService.update(1L, input);

        // THEN
        verify(creneauRepository).deleteById(1L);  // seul le DISPONIBLE supprimé
        verify(creneauRepository, never()).deleteById(2L); // EN_ATTENTE préservé
        verify(creneauRepository).saveAll(any());
    }

    @Test
    void update_whenNotFound_shouldThrowRegleRecurrenceNotFoundException() {
        // GIVEN
        when(regleRecurrenceRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> regleRecurrenceService.update(99L, regleBase().build()))
                .isInstanceOf(RegleRecurrenceNotFoundException.class);
        verify(regleRecurrenceRepository, never()).save(any());
    }

    // =========================================================
    // desactiver
    // =========================================================

    @Test
    void desactiver_whenFound_shouldSetActiveFalseAndSave() {
        // GIVEN
        RegleRecurrenceBO regle = regleBase().active(true).build();
        RegleRecurrenceBO saved = regleBase().active(false).build();
        when(regleRecurrenceRepository.findById(1L)).thenReturn(Optional.of(regle));
        when(regleRecurrenceRepository.save(any())).thenReturn(saved);

        // WHEN
        RegleRecurrenceBO result = regleRecurrenceService.desactiver(1L);

        // THEN
        verify(regleRecurrenceRepository).save(argThat(r -> !r.isActive()));
        assertThat(result.isActive()).isFalse();
    }

    @Test
    void desactiver_whenNotFound_shouldThrowRegleRecurrenceNotFoundException() {
        // GIVEN
        when(regleRecurrenceRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> regleRecurrenceService.desactiver(99L))
                .isInstanceOf(RegleRecurrenceNotFoundException.class);
    }

    // =========================================================
    // delete
    // =========================================================

    @Test
    void delete_whenFound_shouldSupprimerSeulementDisponiblesEtSupprimerRegle() {
        // GIVEN
        RegleRecurrenceBO regle = regleBase().build();
        CreneauBO disp    = disponible(1L);
        CreneauBO attente = enAttente(2L); // ne doit pas être supprimé

        when(regleRecurrenceRepository.findById(1L)).thenReturn(Optional.of(regle));
        when(creneauRepository.findByRegleRecurrenceId(1L)).thenReturn(List.of(disp, attente));

        // WHEN
        regleRecurrenceService.delete(1L);

        // THEN
        verify(creneauRepository).deleteById(1L);
        verify(creneauRepository, never()).deleteById(2L);
        verify(regleRecurrenceRepository).deleteById(1L);
    }

    @Test
    void delete_whenNotFound_shouldThrowRegleRecurrenceNotFoundException() {
        // GIVEN
        when(regleRecurrenceRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> regleRecurrenceService.delete(99L))
                .isInstanceOf(RegleRecurrenceNotFoundException.class);
        verify(regleRecurrenceRepository, never()).deleteById(any());
    }

    @Test
    void delete_whenAucunCreneau_shouldSupprimerRegleSansErreur() {
        // GIVEN
        RegleRecurrenceBO regle = regleBase().build();
        when(regleRecurrenceRepository.findById(1L)).thenReturn(Optional.of(regle));
        when(creneauRepository.findByRegleRecurrenceId(1L)).thenReturn(Collections.emptyList());

        // WHEN
        regleRecurrenceService.delete(1L);

        // THEN
        verify(creneauRepository, never()).deleteById(any());
        verify(regleRecurrenceRepository).deleteById(1L);
    }
}



