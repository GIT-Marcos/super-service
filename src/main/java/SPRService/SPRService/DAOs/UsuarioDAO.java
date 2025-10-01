package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserException;
import org.hibernate.Session;

import java.util.List;

public interface UsuarioDAO extends GenericDAO<Usuario, Long> {

    //LECTURA

    /**
     * Usado para el logueo. Trae la entidad con la contraseña cifrada y se compara la ingresada en servicio.
     *
     * @param nombre nombre del usuario que quiere loguearse.
     */
    List<Usuario> buscarPorNombre(String nombre);

    //ESCRITURA

    /**
     * Para cargar guardar usuarios nuevos.
     *
     * @throws DuplicateUserException si el usuario que se quiere guardar tiene el mismo
     *                                nombre que otro que ya existe en bd.
     */
    void cargarUsuario(Usuario usuario) throws DuplicateUserException;

}
