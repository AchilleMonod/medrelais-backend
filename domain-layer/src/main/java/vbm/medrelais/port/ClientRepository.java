package vbm.medrelais.port;

import vbm.medrelais.model.ClientBO;

import java.util.List;

public interface ClientRepository {
    List<ClientBO> getAllClients();

    ClientBO getClientByID(Long id);

    void createClient(ClientBO clientBO);

    void updateClient(ClientBO clientBO);

    void deleteClient(Long id);
}
