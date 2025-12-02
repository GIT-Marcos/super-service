package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.UsuarioDAO;
import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.exceptions.DuplicateUserNameException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Singleton
public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Long> implements UsuarioDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public UsuarioDAOImpl() {
        super(Usuario.class);
    }

    @Override
    public List<Usuario> verTodos() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT u FROM Usuario u",
                        Usuario.class)
                .getResultList();
    }

    @Override
    public List<Usuario> buscar(FiltroUsuarioDTO filtro) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> query = cb.createQuery(Usuario.class);
        Root<Usuario> root = query.from(Usuario.class);

        List<Predicate> predicados = new ArrayList<>();

        if (filtro.nombre() != null && !filtro.nombre().isBlank())
            predicados.add(cb.like(cb.lower(root.get("nombre")), "%" + filtro.nombre().toLowerCase(Locale.ROOT) + "%"));
        if (filtro.correo() != null && !filtro.correo().isBlank())
            predicados.add(cb.like(cb.lower(root.get("correo")), "%" + filtro.correo().toLowerCase(Locale.ROOT) + "%"));
        if (filtro.roles() != null && !filtro.roles().isEmpty())
            predicados.add(root.get("rol").in(filtro.roles()));

        if (filtro.verActivos() && !filtro.verInactivos()) {
            predicados.add(cb.equal(root.get("activo"), Boolean.TRUE));
        } else if (!filtro.verActivos() && filtro.verInactivos()) {
            predicados.add(cb.equal(root.get("activo"), Boolean.FALSE));
        }

        if (!predicados.isEmpty()) {
            query.where(cb.and(predicados.toArray(new Predicate[0])));
        }

        query.orderBy(cb.asc(root.get("nombre")));
        return em.createQuery(query).getResultList();
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

//    @Override
//    public void cargarUsuario(Usuario usuario){
//        EntityManager em = emProvider.get();
//        try {
//            em.persist(usuario);
//        } catch (RuntimeException e) {
//            if (e instanceof ConstraintViolationException &&
//                    e.getCause() instanceof PSQLException) {
//                throw new DuplicateUserNameException();
//            }
//            throw new RuntimeException("Error inesperado al cargar usuario", e);
//        }
//    }
}
