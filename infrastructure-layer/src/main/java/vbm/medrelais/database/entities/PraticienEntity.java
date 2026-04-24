package vbm.medrelais.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import vbm.medrelais.database.entities.enums.Role;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@Entity
@DiscriminatorValue("PRATICIEN")
public class PraticienEntity extends UtilisateurEntity {
    @Column
    @NotNull(message = "La spécialité est obligatoire pour un praticien")
    private String specialite;

    @Column(name = "adresse_rue")
    @NotNull(message = "La rue est obligatoire")
    private String adresseRue;

    @Column(name = "adresse_ville")
    @NotNull(message = "La ville est obligatoire")
    private String adresseVille;

    @Column(name = "adresse_code_postal")
    @NotNull(message = "Le code postal est obligatoire")
    private String adresseCodePostal;

    @Column
    @NotNull(message = "La latitude est obligatoire")
    private Double latitude;

    @Column
    @NotNull(message = "La longitude est obligatoire")
    private Double longitude;

    @Column
    private String bio; // Description optionnelle

    // Constructeur pour initialiser le rôle
    public PraticienEntity() {
        setRole(Role.PRATICIEN);
    }
}