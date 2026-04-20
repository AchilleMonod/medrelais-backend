package vbm.medrelais.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bureau_etudes")
public class BureauEtudesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UtilisateurEntity user;

    @Column(name = "raison_sociale", nullable = false)
    private String raisonSociale;

    @Column(name = "email", nullable = false)
    private String emailContact;

    @Column(name = "tel", nullable = false)
    private String telContact;

    @OneToMany(mappedBy = "bureauEtude" ,fetch = FetchType.LAZY)
    private List<EtudeEntity> etudes;

    @OneToMany(mappedBy = "bureauEtude" ,fetch = FetchType.LAZY)
    private List<PropositionDevisEntity> propositions;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
