package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.UsuarioDAO;
import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.entities.Usuario;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Singleton
public class UsuarioDAOImpl extends GenericDAOImpl<Usuario, Long> implements UsuarioDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public UsuarioDAOImpl() {
        super(Usuario.class);
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
    public Optional<Usuario> buscarParaLogin(String nombre) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT u FROM Usuario u " +
                                "WHERE u.nombre = :nombre",
                        Usuario.class)
                .setParameter("nombre", nombre)
                .setMaxResults(1)
                .getResultStream().findFirst();
    }

    @Override
    public Optional<Usuario> validarNombre(Long id, String nombre) {
        EntityManager em = emProvider.get();
        String jpql = "select u from Usuario u where u.nombre = :nombre";
        if (id != null) {
            jpql += " and u.id != :id";
        }
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class)
                .setParameter("nombre", nombre)
                .setMaxResults(1);
        if (id != null) {
            query.setParameter("id", id);
        }

        return query.getResultStream().findFirst();
    }

    @Override
    public Optional<Usuario> validarMail(Long id, String mail) {
        EntityManager em = emProvider.get();
        String jpql = "select u from Usuario u where u.correo = :mail";
        if (id != null) {
            jpql += " and u.id != :id";
        }
        TypedQuery<Usuario> query = em.createQuery(jpql, Usuario.class)
                .setParameter("mail", mail)
                .setMaxResults(1);
        if (id != null) {
            query.setParameter("id", id);
        }

        return query.getResultStream().findFirst();
    }
}
