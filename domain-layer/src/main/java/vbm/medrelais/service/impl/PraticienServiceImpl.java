package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.PraticienNotFoundException;
import vbm.medrelais.model.PraticienBO;
import vbm.medrelais.model.enums.Role;
import vbm.medrelais.port.PraticienRepository;
import vbm.medrelais.service.PraticienService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PraticienServiceImpl implements PraticienService {

    private final PraticienRepository praticienRepository;

    @Override
    public PraticienBO getById(Long id) {
        log.info("Récupération du praticien id={}", id);
        return praticienRepository.findById(id)
                .orElseThrow(() -> new PraticienNotFoundException(id));
    }

    @Override
    public List<PraticienBO> getAll() {
        log.info("Récupération de tous les praticiens");
        List<PraticienBO> result = praticienRepository.findAll();
        log.info("{} praticien(s) trouvé(s)", result.size());
        return result;
    }

    @Override
    public List<PraticienBO> getBySpecialite(String specialite) {
        log.info("Recherche de praticiens par spécialité={}", specialite);
        List<PraticienBO> result = praticienRepository.findBySpecialite(specialite);
        log.info("{} praticien(s) trouvé(s) pour spécialité={}", result.size(), specialite);
        return result;
    }

    @Override
    public List<PraticienBO> getByVille(String ville) {
        log.info("Recherche de praticiens par ville={}", ville);
        List<PraticienBO> result = praticienRepository.findByVille(ville);
        log.info("{} praticien(s) trouvé(s) pour ville={}", result.size(), ville);
        return result;
    }

    @Override
    public List<PraticienBO> getNearby(double latitude, double longitude, double rayonKm) {
        log.info("Recherche géographique : lat={}, lon={}, rayon={}km", latitude, longitude, rayonKm);
        List<PraticienBO> result = praticienRepository.findNearby(latitude, longitude, rayonKm);
        log.info("{} praticien(s) trouvé(s) dans le rayon", result.size());
        return result;
    }

    @Override
    public PraticienBO create(PraticienBO praticien) {
        log.info("Création d'un praticien : email={}", praticien.getEmail());
        praticien.setRole(Role.PRATICIEN);
        PraticienBO saved = praticienRepository.save(praticien);
        log.info("Praticien créé avec succès : id={}", saved.getId());
        return saved;
    }

    @Override
    public PraticienBO update(Long id, PraticienBO praticien) {
        log.info("Mise à jour du praticien id={}", id);
        PraticienBO existing = praticienRepository.findById(id)
                .orElseThrow(() -> new PraticienNotFoundException(id));
        praticien.setId(existing.getId());
        praticien.setRole(existing.getRole());
        praticien.setEmail(existing.getEmail());
        praticien.setCreatedAt(existing.getCreatedAt());
        PraticienBO updated = praticienRepository.save(praticien);
        log.info("Praticien mis à jour avec succès : id={}", id);
        return updated;
    }

    @Override
    public void delete(Long id) {
        log.info("Suppression du praticien id={}", id);
        praticienRepository.findById(id)
                .orElseThrow(() -> new PraticienNotFoundException(id));
        praticienRepository.deleteById(id);
        log.info("Praticien supprimé avec succès : id={}", id);
    }
}

