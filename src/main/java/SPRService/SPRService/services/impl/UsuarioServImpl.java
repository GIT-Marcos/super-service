package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.UsuarioDAO;
import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserException;
import SPRService.SPRService.services.UsuarioServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.hibernate.HibernateException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

@Singleton
public class UsuarioServImpl implements UsuarioServ {

    private final UsuarioDAO daoUsuario;

    @Inject
    public UsuarioServImpl(UsuarioDAO daoUsuario) {
        this.daoUsuario = daoUsuario;
    }

    @Transactional
    @Override
    public List<Usuario> verTodos() {
        return daoUsuario.verTodos();
    }

    @Transactional
    @Override
    public List<Usuario> buscar(FiltroUsuarioDTO filtro) {
        if (filtro == null) filtro = new FiltroUsuarioDTO(
                null, null, null, true, true);
        return daoUsuario.buscar(filtro);
    }

    @Transactional
    @Override
    public void cargarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new NullPointerException("usuario a cargar nulo.");
        }
        String hashed = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
        usuario.setPassword(hashed);
        try {
            daoUsuario.cargarUsuario(usuario);
        } catch (DuplicateUserException e) {
            throw new RuntimeException("El nombre de usuario: " +
                    usuario.getNombre() + " ya existe en bd.", e);
        }
    }

    @Transactional
    @Override
    public Usuario loguear(String nombre, String inputPass) {
        if (nombre == null || inputPass == null) {
            throw new NullPointerException("nombre o contraseña nula al loguear.");
        }
        Usuario usuario;
        List<Usuario> list = daoUsuario.buscarPorNombre(nombre);
        if (list.isEmpty()) {
            throw new HibernateException("No se encontró usuario con nombre " + nombre);
        } else {
            usuario = list.getFirst();
            if (!BCrypt.checkpw(inputPass, usuario.getPassword())) {
                throw new HibernateException("Contraseña incorrecta para el usuario: " + nombre);
            }
        }
        return usuario;
    }

    //TODO: reemplazar en casos con estos usar Optional<>
    @Override
    public Optional<Usuario> darDeBaja(Usuario usuario) {
        usuario.setActivo(Boolean.FALSE);
        return Optional.ofNullable(daoUsuario.update(usuario));
    }
}
