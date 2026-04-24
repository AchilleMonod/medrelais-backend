package vbm.medrelais.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vbm.medrelais.database.entities.enums.StatutAttribution;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attribution")
public class AttributionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creneau_id", nullable = false)
    private CreneauEntity creneau;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "remplacant_id", nullable = false)
    private UtilisateurEntity remplacant; // Qui demande le créneau

    @Column(name = "date_debut_attribution", nullable = false)
    private LocalDateTime dateDebutAttribution;

    @Column(name = "date_fin_attribution", nullable = false)
    private LocalDateTime dateFinAttribution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAttribution statut = StatutAttribution.EN_ATTENTE;

    @Column
    private String message; // Message du remplaçant

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
