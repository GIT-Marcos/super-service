package SPRService.SPRService.services;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.util.ResultadoPaginado;

import java.util.List;
import java.util.Optional;

public interface ClienteServ {

    List<Cliente> verTodosActivos();

    Optional<Cliente> verOperacionesConVehiculos(Long id);

    Optional<Cliente> verDatosContacto(Long id);

    ResultadoPaginado<Cliente> buscarPaginado(String dni, String apellido, String nombre,
                                              boolean activos, boolean baja, int pagina, int itemsPorPagina);

    Cliente saveClient(Cliente c);

    Optional<Cliente> editClient(Cliente clienteDTO);

    void softDeleteClient(Cliente c);

}
