package vbm.medrelais.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proposition_devis")
public class PropositionDevisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bureau_etudes_id", nullable = false)
    private BureauEtudesEntity bureauEtude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_devis_id", nullable = false)
    private DemandeDevisEntity demandeDevis;

    @Column(name = "date_rendu", nullable = false)
    private LocalDate dateRendu;

    @Column(nullable = false)
    private BigDecimal prix;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "devis_pdf_id")
    private DocumentEntity devisPDF;

    @Column
    @Builder.Default
    private boolean refusee = false;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
