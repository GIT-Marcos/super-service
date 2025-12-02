package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserNameException;

import java.util.List;
import java.util.Optional;

public interface UsuarioServ {

    List<Usuario> verTodos();

    List<Usuario> buscar(FiltroUsuarioDTO filtro);

    Usuario loguear(String nombre, String inputPass);

    Optional<Usuario> cargarUsuario(Usuario usuario) throws DuplicateUserNameException;

    Optional<Usuario> modififcarUsuario(Usuario usuario);

    Optional<Usuario> darDeBaja(Usuario usuario);
}
