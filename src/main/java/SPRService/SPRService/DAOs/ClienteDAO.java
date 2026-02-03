package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.filtros.FiltroClienteDTO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.util.ResultadoPaginado;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO extends GenericDAO<Cliente, Long> {

    /**
     * Trae todos los clientes con sus datos mínimos para llenar la tabla principal.
     */
    List<Cliente> verTodos();

    /**
     * Usado para consulta las relaciones de 1 cliente particular.
     * Busca 1 cliente y lo trae con sus services, ventas y vehículos.
     * @param id del cliente que se intenta consultar.
     */
    Optional<Cliente> verOperacionesConVehiculos(Long id);

    /**
     * Trae los datos de contacto (eMails y teléfonos) de 1 cliente.
     * Usado para la edición.
     */
    Optional<Cliente> verDatosContacto(Long id);

    /**
     * Usada para filtrar según sus parámetros.
     */
    List<Cliente> filteredSearch(String dni, String lastName, String firstName);

    ResultadoPaginado<Cliente> buscarPaginado(FiltroClienteDTO filtro);

}
