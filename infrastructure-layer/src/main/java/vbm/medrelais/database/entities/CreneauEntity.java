package vbm.medrelais.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vbm.medrelais.database.entities.enums.StatutCreneau;
import vbm.medrelais.database.entities.enums.TypeDureeCreneau;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "creneau")
public class CreneauEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "praticien_id", nullable = false)
    private UtilisateurEntity praticien;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_duree", nullable = false)
    private TypeDureeCreneau typeDuree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCreneau statut = StatutCreneau.DISPONIBLE;

    @Column
    private String titre;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regle_recurrence_id")
    private RegleRecurrenceEntity regleRecurrence; // NULL si créneau ponctuel

    @Column(name = "est_exception", nullable = false)
    private boolean estException = false;

    @OneToMany(mappedBy = "creneau", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributionEntity> attributions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
