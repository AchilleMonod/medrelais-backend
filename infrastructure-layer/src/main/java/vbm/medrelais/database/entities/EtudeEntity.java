package vbm.medrelais.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import vbm.medrelais.model.enums.EtatEtudeEnum;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "etude")
public class EtudeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bureau_etudes_id", nullable = false)
    private BureauEtudesEntity bureauEtude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposition_devis_id", nullable = false)
    private PropositionDevisEntity propositionDevis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatEtudeEnum etat;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rapport_id")
    private DocumentEntity rapport;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
