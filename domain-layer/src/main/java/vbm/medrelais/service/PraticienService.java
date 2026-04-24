package vbm.medrelais.service;

import vbm.medrelais.model.PraticienBO;

import java.util.List;

public interface PraticienService {

    PraticienBO getById(Long id);

    List<PraticienBO> getAll();

    List<PraticienBO> getBySpecialite(String specialite);

    List<PraticienBO> getByVille(String ville);

    List<PraticienBO> getNearby(double latitude, double longitude, double rayonKm);

    PraticienBO create(PraticienBO praticien);

    PraticienBO update(Long id, PraticienBO praticien);

    void delete(Long id);
}

