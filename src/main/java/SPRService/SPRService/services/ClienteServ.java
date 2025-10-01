package SPRService.SPRService.services;

import SPRService.SPRService.entities.Cliente;

import java.util.List;

public interface ClienteServ {

    List<Cliente> getAllActive();
    List<Cliente> filteredSearch(String dni, String lastName, String firstName);

    Cliente saveClient(Cliente c);
    Cliente editClient(Cliente c);
    void softDeleteClient(Cliente c);

}
