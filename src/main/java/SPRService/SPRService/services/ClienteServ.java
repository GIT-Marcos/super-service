package SPRService.SPRService.services;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.util.ResultadoPaginado;

import java.util.List;
import java.util.Optional;

public interface ClienteServ {

    List<Cliente> verTodosActivos();
    Optional<Cliente> verOperacionesConVehiculos(Long id);
    Optional<Cliente> verDatosContacto(Long id);
    List<Cliente> filteredSearch(String dni, String lastName, String firstName);
    ResultadoPaginado<Cliente> buscarPaginado(String dni, String apellido, String nombre,
                                              int pagina, int itemsPorPagina);

    Cliente saveClient(Cliente c);
    Optional<Cliente> editClient(Cliente clienteDTO);
    void softDeleteClient(Cliente c);

}
