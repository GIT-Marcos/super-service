package SPRService.SPRService.services;

import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserException;

public interface UsuarioServ {

    void cargarUsuario(Usuario usuario) throws DuplicateUserException;

    Usuario loguear(String nombre, String inputPass);

}
