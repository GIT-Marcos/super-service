package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.UsuarioDAO;
import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
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
    private void verificarUnicidad(Usuario usu) {
        daoUsuario.validarNombre(usu.getId(), usu.getNombre())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("El usuario con el nombre '" + u.getNombre() +
                            "' ya existe en el sistema.");
                });

        daoUsuario.validarMail(usu.getId(), usu.getCorreo())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("El correo '" + u.getCorreo() +
                            "' ya pertenece a otro usuario.");
                });
    }

    @Transactional
    @Override
    public Usuario cargarUsuario(Usuario usuario) {
        verificarUnicidad(usuario);

        String hashed = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
        usuario.setPassword(hashed);

        daoUsuario.save(usuario);
        return usuario;
    }

    @Transactional
    @Override
    public Usuario modificarUsuario(Usuario usuarioDTO, String inputPassOriginal) {
        Usuario usuarioOriginal = daoUsuario.getById(usuarioDTO.getId());
        verificarPass(inputPassOriginal, usuarioOriginal);

        verificarUnicidad(usuarioDTO);

        String hashed = BCrypt.hashpw(usuarioDTO.getPassword(), BCrypt.gensalt());
        usuarioOriginal.setPassword(hashed);
        usuarioOriginal.setRol(usuarioDTO.getRol());
        usuarioOriginal.setCorreo(usuarioDTO.getCorreo());
        usuarioOriginal.setNombre(usuarioDTO.getNombre());

        return usuarioOriginal;
    }

    private void verificarPass(String inputPassOriginal, Usuario u) {
        if (!BCrypt.checkpw(inputPassOriginal, u.getPassword()))
            throw new IllegalArgumentException("La contraseña ingresada no es correcta para el usuario: " +
                    u.getNombre());
    }

    @Transactional
    @Override
    public Usuario loguear(String nombre, String inputPass) {
        Optional<Usuario> result = daoUsuario.buscarParaLogin(nombre);
        if (result.isEmpty()) {
            throw new HibernateException("No se encontró usuario con nombre " + nombre);
        }

        Usuario usuarioEncontrado = result.get();

        if (usuarioEncontrado.getActivo() == false)
            throw new HibernateException("El usuario: '" + usuarioEncontrado.getNombre() + "' está dado de baja.");

        if (!BCrypt.checkpw(inputPass, usuarioEncontrado.getPassword()))
            throw new HibernateException("Contraseña incorrecta para el usuario: " + nombre);

        return usuarioEncontrado;
    }

    @Transactional
    @Override
    public Optional<Usuario> darDeBaja(Usuario usuario) {
        usuario.setActivo(Boolean.FALSE);
        return Optional.ofNullable(daoUsuario.update(usuario));
    }

    @Transactional
    @Override
    public void reactivar(Long id) {
        Usuario managed = daoUsuario.getById(id);
        managed.setActivo(true);
    }
}
