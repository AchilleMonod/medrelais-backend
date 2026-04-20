package vbm.medrelais.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vbm.medrelais.exception.ClientServiceException;
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.port.ClientRepository;
import vbm.medrelais.service.ClientService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public List<ClientBO> getAllClients() {
        log.info("Récupération de tous les clients");
        List<ClientBO> result = clientRepository.getAllClients();
        log.info("{} client(s) trouvé(s)", result.size());
        return result;
    }

    @Override
    public ClientBO getClientById(Long id) {
        log.info("Récupération du client id={}", id);
        ClientBO result = clientRepository.getClientByID(id);
        if (result == null) {
            log.warn("Aucun client trouvé pour id={}", id);
        }
        return result;
    }

    @Override
    public void createClient(ClientBO clientBO) {
        log.info("Création d'un client : nom={} prenom={}", clientBO.getNom(), clientBO.getPrenom());
        clientRepository.createClient(clientBO);
        log.info("Client créé avec succès : nom={} prenom={}", clientBO.getNom(), clientBO.getPrenom());
    }

    @Override
    public void updateClient(ClientBO clientBO) {
        Long id = clientBO.getId();
        log.info("Mise à jour du client id={}", id);
        if (clientRepository.getClientByID(id) == null) {
            log.warn("Tentative de mise à jour d'un client inexistant id={}", id);
            throw new ClientServiceException("Client introuvable avec l'id : " + id);
        }
        clientRepository.updateClient(clientBO);
        log.info("Client mis à jour avec succès id={}", id);
    }

    @Override
    public void deleteClient(Long id) {
        log.info("Suppression du client id={}", id);
        if (clientRepository.getClientByID(id) == null) {
            log.warn("Tentative de suppression d'un client inexistant id={}", id);
            throw new ClientServiceException("Client introuvable avec l'id : " + id);
        }
        clientRepository.deleteClient(id);
        log.info("Client supprimé avec succès id={}", id);
    }
}
