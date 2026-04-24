package vbm.medrelais.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vbm.medrelais.database.entities.enums.TypeDureeCreneau;
import vbm.medrelais.database.entities.enums.TypeRecurrence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "regle_recurrence")
public class RegleRecurrenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "praticien_id", nullable = false)
    private UtilisateurEntity praticien; // Qui propose la règle

    @Enumerated(EnumType.STRING)
    @Column(name = "type_recurrence", nullable = false)
    private TypeRecurrence typeRecurrence;

    @Column(nullable = false)
    private int intervalle = 1; // Ex: tous les 2 semaines

    /**
     * Jours de la semaine concernés, stockés en chaîne séparée par virgules.
     * Ex: "1,3,5" pour lundi, mercredi, vendredi (1=lundi, 7=dimanche)
     */
    @Column(name = "jours_semaine")
    private String joursSemaine;

    /**
     * Jours du mois concernés. Ex: "1,15" pour le 1er et le 15.
     */
    @Column(name = "jours_mois")
    private String joursMois;

    /**
     * Mois de l'année concernés. Ex: "6,7,8" pour l'été.
     */
    @Column(name = "mois_annee")
    private String moisAnnee;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    /** Fin de série par date (exclusif avec nombreOccurrences) */
    @Column(name = "date_fin")
    private LocalDate dateFin;

    /** Fin de série par nombre d'occurrences (exclusif avec dateFin) */
    @Column(name = "nombre_occurrences")
    private Integer nombreOccurrences;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    /** Type de durée pour les créneaux générés (cohérence avec CreneauEntity) */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_duree", nullable = false)
    private TypeDureeCreneau typeDuree;

    @Column(nullable = false)
    private boolean active = true;

    /** Créneaux générés par cette règle */
    @OneToMany(mappedBy = "regleRecurrence", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CreneauEntity> creneaux = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
