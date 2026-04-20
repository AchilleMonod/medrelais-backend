package vbm.medrelais.database.adapter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import vbm.medrelais.database.dao.ClientDAO;
import vbm.medrelais.database.entities.ClientEntity;
import vbm.medrelais.database.mapper.ClientEntityMapper;
import vbm.medrelais.model.ClientBO;
import vbm.medrelais.port.ClientRepository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientDAO clientDAO;
    private final ClientEntityMapper clientEntityMapper;

    @Override
    public List<ClientBO> getAllClients() {
        return clientDAO.findAll().stream()
                .map(clientEntityMapper::toBO)
                .toList();
    }

    @Override
    public ClientBO getClientByID(Long id) {
        return clientDAO.findById(id)
                .map(clientEntityMapper::toBO)
                .orElse(null);
    }

    @Override
    public void createClient(ClientBO clientBO) {
        persist(clientBO);
    }

    @Override
    public void updateClient(ClientBO clientBO) {
        persist(clientBO);
    }

    @Override
    public void deleteClient(Long id) {
        clientDAO.deleteById(id);
    }

    private void persist(ClientBO clientBO) {
        ClientEntity entity = clientEntityMapper.toEntity(clientBO);
        clientDAO.save(entity);
    }
}
