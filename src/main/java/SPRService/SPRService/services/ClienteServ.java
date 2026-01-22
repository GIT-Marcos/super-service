package SPRService.SPRService.services;

import SPRService.SPRService.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteServ {

    List<Cliente> getAllActive();
    List<Cliente> filteredSearch(String dni, String lastName, String firstName);

    Cliente saveClient(Cliente c);
    Optional<Cliente> editClient(Cliente clienteDTO);
    void softDeleteClient(Cliente c);

}
