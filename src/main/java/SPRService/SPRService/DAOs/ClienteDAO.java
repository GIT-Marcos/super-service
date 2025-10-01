package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Cliente;

import java.util.List;

public interface ClienteDAO extends GenericDAO<Cliente, Long> {

    List<Cliente> getAllActive();
    List<Cliente> filteredSearch(String dni, String lastName, String firstName);

}
