package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserException;

import java.util.List;

public interface UsuarioServ {

    List<Usuario> verTodos();

    List<Usuario> buscar(FiltroUsuarioDTO filtro);

    void cargarUsuario(Usuario usuario) throws DuplicateUserException;

    Usuario loguear(String nombre, String inputPass);

}
