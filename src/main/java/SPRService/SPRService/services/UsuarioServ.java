package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioServ {

    List<Usuario> verTodos();

    List<Usuario> buscar(FiltroUsuarioDTO filtro);

    Usuario loguear(String nombre, String inputPass);

    Usuario cargarUsuario(Usuario usuario);

    Usuario modificarUsuario(Usuario usuarioDTO, String inputPassOriginal);

    Optional<Usuario> darDeBaja(Usuario usuario);
}
