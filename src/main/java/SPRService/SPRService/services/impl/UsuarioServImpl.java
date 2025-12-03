package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.UsuarioDAO;
import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserNameException;
import SPRService.SPRService.services.UsuarioServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import org.hibernate.HibernateException;
import org.hibernate.exception.ConstraintViolationException;
import org.mindrot.jbcrypt.BCrypt;
import org.postgresql.util.PSQLException;

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
        return daoUsuario.getAll();
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
    public Optional<Usuario> cargarUsuario(Usuario usuario) throws DuplicateUserNameException {
        if (usuario == null) {
            throw new NullPointerException("usuario a cargar nulo.");
        }
        String hashed = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
        usuario.setPassword(hashed);
        try {
            daoUsuario.save(usuario);
        } catch (RuntimeException e) {
            if (e instanceof ConstraintViolationException &&
                    e.getCause() instanceof PSQLException) {
                throw new DuplicateUserNameException("El usuario con el nombre: " + usuario.getNombre() +
                        " ya existe en el sistema.");
            }
            throw new RuntimeException("Error inesperado al cargar usuario", e);
        }
        return Optional.of(usuario);
    }

    @Transactional
    @Override
    public Optional<Usuario> modificarUsuario(Usuario usuario, String inputPassOriginal) throws DuplicateUserNameException {
        try {
            Usuario usuarioOriginal = daoUsuario.getById(usuario.getId());
            verificarPass(inputPassOriginal, usuarioOriginal);
            String hashed = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
            usuario.setPassword(hashed);

            Usuario updated = daoUsuario.update(usuario);
            daoUsuario.flush();
            return Optional.ofNullable(updated);
        } catch (RuntimeException e) {
            if (e instanceof ConstraintViolationException &&
                    e.getCause() instanceof PSQLException) {
                throw new DuplicateUserNameException("El usuario con el nombre: " + usuario.getNombre() +
                        " ya existe en el sistema.");
            } else if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new RuntimeException("Error inesperado al cargar usuario", e);
        }
    }

    private void verificarPass(String inputPassOriginal, Usuario u) {
        if (!BCrypt.checkpw(inputPassOriginal, u.getPassword()))
            throw new IllegalArgumentException("La contraseña ingresada no es correcta para el usuario: " +
                    u.getNombre());
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

    //TODO: reemplazar en casos como estos usar Optional<>
    @Transactional
    @Override
    public Optional<Usuario> darDeBaja(Usuario usuario) {
        usuario.setActivo(Boolean.FALSE);
        return Optional.ofNullable(daoUsuario.update(usuario));
    }
}
