package vbm.medrelais.service;


import vbm.medrelais.model.ClientBO;
import java.util.List;

public interface ClientService {

    List<ClientBO> getAllClients();
    ClientBO getClientById(Long id);

    void createClient(ClientBO clientBO);
    void updateClient(ClientBO clientBO);
    void deleteClient(Long id);
}
