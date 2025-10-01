package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.UsuarioDAO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;

import java.util.List;

@Singleton
public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Long> implements UsuarioDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public UsuarioDAOImpl() {
        super(Usuario.class);
    }

    @Override
    public List<Usuario> buscarPorNombre(String nombre) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT u FROM Usuario u " +
                                "WHERE u.nombre = :nombre",
                        Usuario.class)
                .setParameter("nombre", nombre)
                .setMaxResults(1)
                .getResultList();
    }

    @Override
    public void cargarUsuario(Usuario usuario) throws DuplicateUserException {
        EntityManager em = emProvider.get();
        try {
            em.persist(usuario);
        } catch (RuntimeException e) {
            if (e instanceof ConstraintViolationException &&
                    e.getCause() instanceof PSQLException) {
                throw new DuplicateUserException();
            }
            throw new RuntimeException("Error inesperado al cargar usuario", e);
        }
    }
}
