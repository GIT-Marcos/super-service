package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;

import java.util.List;

public interface UsuarioDAO extends GenericDAO<Usuario, Long> {

    //LECTURA

    List<Usuario> buscar(FiltroUsuarioDTO filtro);

    /**
     * Usado para el logueo. Trae la entidad con la contraseña cifrada y se compara la ingresada en servicio.
     *
     * @param nombre nombre del usuario que quiere loguearse.
     */
    List<Usuario> buscarPorNombre(String nombre);
}
