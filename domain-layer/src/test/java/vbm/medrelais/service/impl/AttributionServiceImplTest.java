package vbm.medrelais.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vbm.medrelais.exception.AttributionNotFoundException;
import vbm.medrelais.exception.CreneauNotFoundException;
import vbm.medrelais.model.AttributionBO;
import vbm.medrelais.model.CreneauBO;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.StatutAttribution;
import vbm.medrelais.model.enums.StatutCreneau;
import vbm.medrelais.model.enums.TypeDureeCreneau;
import vbm.medrelais.port.AttributionRepository;
import vbm.medrelais.port.CreneauRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributionServiceImplTest {

    @Mock
    private AttributionRepository attributionRepository;

    @Mock
    private CreneauRepository creneauRepository;

    @InjectMocks
    private AttributionServiceImpl attributionService;

    // =========================================================
    // Helpers
    // =========================================================

    private PraticienBO praticien(Long id) {
        return PraticienBO.builder().id(id).email("p" + id + "@test.com").build();
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

    private AttributionBO buildAttribution(Long id, StatutAttribution statut) {
        return AttributionBO.builder()
                .id(id)
                .creneau(buildCreneau(5L, StatutCreneau.EN_ATTENTE))
                .remplacant(praticien(20L))
                .statut(statut)
                .build();
    }

    // =========================================================
    // getById
    // =========================================================

    @Test
    void getById_whenFound_shouldReturnBO() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.EN_ATTENTE);
        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));

        // WHEN
        AttributionBO result = attributionService.getById(1L);

        // THEN
        assertThat(result.getId()).isEqualTo(1L);
        verify(attributionRepository).findById(1L);
    }

    @Test
    void getById_whenNotFound_shouldThrowAttributionNotFoundException() {
        // GIVEN
        when(attributionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.getById(99L))
                .isInstanceOf(AttributionNotFoundException.class);
    }

    // =========================================================
    // getByCreneauId
    // =========================================================

    @Test
    void getByCreneauId_shouldDelegateToRepository() {
        // GIVEN
        when(attributionRepository.findByCreneauId(5L))
                .thenReturn(List.of(buildAttribution(1L, StatutAttribution.EN_ATTENTE)));

        // WHEN
        List<AttributionBO> result = attributionService.getByCreneauId(5L);

        // THEN
        assertThat(result).hasSize(1);
        verify(attributionRepository).findByCreneauId(5L);
    }

    // =========================================================
    // getByRemplacantId
    // =========================================================

    @Test
    void getByRemplacantId_shouldDelegateToRepository() {
        // GIVEN
        when(attributionRepository.findByRemplacantId(20L))
                .thenReturn(List.of(buildAttribution(1L, StatutAttribution.EN_ATTENTE)));

        // WHEN
        List<AttributionBO> result = attributionService.getByRemplacantId(20L);

        // THEN
        assertThat(result).hasSize(1);
    }

    // =========================================================
    // getByRemplacantIdAndStatut
    // =========================================================

    @Test
    void getByRemplacantIdAndStatut_shouldDelegateToRepository() {
        // GIVEN
        when(attributionRepository.findByRemplacantIdAndStatut(20L, StatutAttribution.EN_ATTENTE))
                .thenReturn(List.of(buildAttribution(1L, StatutAttribution.EN_ATTENTE)));

        // WHEN
        List<AttributionBO> result = attributionService.getByRemplacantIdAndStatut(20L, StatutAttribution.EN_ATTENTE);

        // THEN
        assertThat(result).hasSize(1);
    }

    // =========================================================
    // demanderRemplacement
    // =========================================================

    @Test
    void demanderRemplacement_whenCreneauDisponibleEtPasDoublon_shouldSaveAndPasserCreneauEnAttente() {
        // GIVEN
        CreneauBO creneau = buildCreneau(5L, StatutCreneau.DISPONIBLE);
        AttributionBO input = AttributionBO.builder()
                .creneau(buildCreneau(5L, StatutCreneau.DISPONIBLE))
                .remplacant(praticien(20L))
                .build();
        AttributionBO saved = buildAttribution(1L, StatutAttribution.EN_ATTENTE);

        when(creneauRepository.findById(5L)).thenReturn(Optional.of(creneau));
        when(attributionRepository.existsByCreneauIdAndRemplacantIdAndStatutIn(eq(5L), eq(20L), anyList()))
                .thenReturn(false);
        when(attributionRepository.save(any())).thenReturn(saved);
        when(creneauRepository.save(any())).thenReturn(creneau);

        // WHEN
        AttributionBO result = attributionService.demanderRemplacement(input);

        // THEN
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatut()).isEqualTo(StatutAttribution.EN_ATTENTE);
        verify(creneauRepository).save(argThat(c -> c.getStatut() == StatutCreneau.EN_ATTENTE));
    }

    @Test
    void demanderRemplacement_whenCreneauNonDisponible_shouldThrowIllegalStateException() {
        // GIVEN
        CreneauBO creneau = buildCreneau(5L, StatutCreneau.EN_ATTENTE); // pas DISPONIBLE
        AttributionBO input = AttributionBO.builder()
                .creneau(buildCreneau(5L, StatutCreneau.EN_ATTENTE))
                .remplacant(praticien(20L))
                .build();

        when(creneauRepository.findById(5L)).thenReturn(Optional.of(creneau));

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.demanderRemplacement(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("n'est plus disponible");
        verify(attributionRepository, never()).save(any());
    }

    @Test
    void demanderRemplacement_whenCreneauInexistant_shouldThrowCreneauNotFoundException() {
        // GIVEN
        AttributionBO input = AttributionBO.builder()
                .creneau(buildCreneau(99L, StatutCreneau.DISPONIBLE))
                .remplacant(praticien(20L))
                .build();

        when(creneauRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.demanderRemplacement(input))
                .isInstanceOf(CreneauNotFoundException.class);
    }

    @Test
    void demanderRemplacement_whenDoublon_shouldThrowIllegalStateException() {
        // GIVEN
        CreneauBO creneau = buildCreneau(5L, StatutCreneau.DISPONIBLE);
        AttributionBO input = AttributionBO.builder()
                .creneau(buildCreneau(5L, StatutCreneau.DISPONIBLE))
                .remplacant(praticien(20L))
                .build();

        when(creneauRepository.findById(5L)).thenReturn(Optional.of(creneau));
        when(attributionRepository.existsByCreneauIdAndRemplacantIdAndStatutIn(eq(5L), eq(20L), anyList()))
                .thenReturn(true); // doublon

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.demanderRemplacement(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("déjà une demande active");
        verify(attributionRepository, never()).save(any());
    }

    // =========================================================
    // accepter
    // =========================================================

    @Test
    void accepter_whenEnAttente_shouldAccepterEtRefuserAutresEtMarquerCreneauAttribue() {
        // GIVEN
        AttributionBO toAccept = buildAttribution(1L, StatutAttribution.EN_ATTENTE);
        toAccept.getCreneau().setId(5L);

        // Deux autres demandes sur le même créneau
        AttributionBO other1 = buildAttribution(2L, StatutAttribution.EN_ATTENTE);
        AttributionBO other2 = buildAttribution(3L, StatutAttribution.REFUSEE); // déjà refusée → ne doit pas être re-refusée

        AttributionBO accepted = buildAttribution(1L, StatutAttribution.ACCEPTEE);
        CreneauBO creneau = buildCreneau(5L, StatutCreneau.EN_ATTENTE);

        when(attributionRepository.findById(1L)).thenReturn(Optional.of(toAccept));
        when(attributionRepository.findByCreneauId(5L)).thenReturn(List.of(toAccept, other1, other2));
        when(attributionRepository.save(any())).thenReturn(accepted);
        when(creneauRepository.findById(5L)).thenReturn(Optional.of(creneau));
        when(creneauRepository.save(any())).thenReturn(creneau);

        // WHEN
        AttributionBO result = attributionService.accepter(1L);

        // THEN
        assertThat(result.getStatut()).isEqualTo(StatutAttribution.ACCEPTEE);
        // other1 (EN_ATTENTE) doit être sauvegardée en REFUSEE
        verify(attributionRepository).save(argThat(a ->
                a.getId().equals(2L) && a.getStatut() == StatutAttribution.REFUSEE));
        // other2 (déjà REFUSEE) ne doit pas être re-sauvegardée
        verify(attributionRepository, never()).save(argThat(a -> a.getId().equals(3L)));
        // Le créneau doit être passé ATTRIBUE
        verify(creneauRepository).save(argThat(c -> c.getStatut() == StatutCreneau.ATTRIBUE));
    }

    @Test
    void accepter_whenPasEnAttente_shouldThrowIllegalStateException() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.ACCEPTEE); // déjà acceptée
        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.accepter(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EN_ATTENTE");
    }

    @Test
    void accepter_whenNotFound_shouldThrowAttributionNotFoundException() {
        // GIVEN
        when(attributionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.accepter(99L))
                .isInstanceOf(AttributionNotFoundException.class);
    }

    // =========================================================
    // refuser
    // =========================================================

    @Test
    void refuser_whenSeuleDemande_shouldRefuserEtRemettreCrenauDisponible() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.EN_ATTENTE);
        attribution.getCreneau().setId(5L);
        AttributionBO refused = buildAttribution(1L, StatutAttribution.REFUSEE);
        CreneauBO creneau = buildCreneau(5L, StatutCreneau.EN_ATTENTE);

        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));
        when(attributionRepository.save(any())).thenReturn(refused);
        // Plus aucune demande en attente sur ce créneau (en dehors de la demande refusée)
        when(attributionRepository.findByCreneauId(5L)).thenReturn(List.of(refused));
        when(creneauRepository.findById(5L)).thenReturn(Optional.of(creneau));
        when(creneauRepository.save(any())).thenReturn(creneau);

        // WHEN
        AttributionBO result = attributionService.refuser(1L);

        // THEN
        assertThat(result.getStatut()).isEqualTo(StatutAttribution.REFUSEE);
        verify(creneauRepository).save(argThat(c -> c.getStatut() == StatutCreneau.DISPONIBLE));
    }

    @Test
    void refuser_quandAutresDemandesToujours_shouldNePasRemettreCrenauDisponible() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.EN_ATTENTE);
        attribution.getCreneau().setId(5L);
        AttributionBO refused = buildAttribution(1L, StatutAttribution.REFUSEE);
        AttributionBO other   = buildAttribution(2L, StatutAttribution.EN_ATTENTE); // autre demande encore active

        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));
        when(attributionRepository.save(any())).thenReturn(refused);
        when(attributionRepository.findByCreneauId(5L)).thenReturn(List.of(refused, other));

        // WHEN
        attributionService.refuser(1L);

        // THEN
        verify(creneauRepository, never()).findById(any());
        verify(creneauRepository, never()).save(any());
    }

    @Test
    void refuser_whenPasEnAttente_shouldThrowIllegalStateException() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.ACCEPTEE);
        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.refuser(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EN_ATTENTE");
    }

    @Test
    void refuser_whenNotFound_shouldThrowAttributionNotFoundException() {
        // GIVEN
        when(attributionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.refuser(99L))
                .isInstanceOf(AttributionNotFoundException.class);
    }

    // =========================================================
    // annuler
    // =========================================================

    @Test
    void annuler_whenEnAttente_shouldAnnulerSansModifierCreneau() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.EN_ATTENTE);
        AttributionBO cancelled   = buildAttribution(1L, StatutAttribution.ANNULEE);
        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));
        when(attributionRepository.save(any())).thenReturn(cancelled);

        // WHEN
        AttributionBO result = attributionService.annuler(1L);

        // THEN
        assertThat(result.getStatut()).isEqualTo(StatutAttribution.ANNULEE);
        verify(creneauRepository, never()).save(any());
    }

    @Test
    void annuler_whenAcceptee_shouldAnnulerEtRemettreCrenauDisponible() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.ACCEPTEE);
        attribution.getCreneau().setId(5L);
        attribution.getCreneau().setStatut(StatutCreneau.ATTRIBUE);
        AttributionBO cancelled = buildAttribution(1L, StatutAttribution.ANNULEE);
        CreneauBO creneau = buildCreneau(5L, StatutCreneau.ATTRIBUE);

        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));
        when(attributionRepository.save(any())).thenReturn(cancelled);
        when(creneauRepository.findById(5L)).thenReturn(Optional.of(creneau));
        when(creneauRepository.save(any())).thenReturn(creneau);

        // WHEN
        AttributionBO result = attributionService.annuler(1L);

        // THEN
        assertThat(result.getStatut()).isEqualTo(StatutAttribution.ANNULEE);
        verify(creneauRepository).save(argThat(c -> c.getStatut() == StatutCreneau.DISPONIBLE));
    }

    @Test
    void annuler_whenDejaRefusee_shouldThrowIllegalStateException() {
        // GIVEN
        AttributionBO attribution = buildAttribution(1L, StatutAttribution.REFUSEE);
        when(attributionRepository.findById(1L)).thenReturn(Optional.of(attribution));

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.annuler(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("refusée");
    }

    @Test
    void annuler_whenNotFound_shouldThrowAttributionNotFoundException() {
        // GIVEN
        when(attributionRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> attributionService.annuler(99L))
                .isInstanceOf(AttributionNotFoundException.class);
    }
}

