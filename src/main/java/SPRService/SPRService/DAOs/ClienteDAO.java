package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteDAO extends GenericDAO<Cliente, Long> {

    /**
     * Usado para consulta las operaciones de 1 cliente particular.
     * Busca 1 cliente y lo trae con sus services y ventas.
     * @param id del cliente que se intenta consultar.
     */
    Optional<Cliente> verDetalle(Long id);

    /**
     * Usada para filtrar según sus parámetros.
     * Usada para ver todos al argumentarle vacíos.
     */
    List<Cliente> filteredSearch(String dni, String lastName, String firstName);

}
