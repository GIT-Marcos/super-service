package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ClienteDAO;
import SPRService.SPRService.entities.Cliente;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Singleton
public class ClienteDAOImpl extends GenericDAOImpl<Cliente, Long> implements ClienteDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public ClienteDAOImpl() {
        super(Cliente.class);
    }

    @Override
    public List<Cliente> getAllActive() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT c FROM Cliente c " +
                                "WHERE c.activo = TRUE",
                        Cliente.class)
                .getResultList();
    }

    @Override
    public List<Cliente> filteredSearch(String dni, String lastName, String firstName) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
        Root<Cliente> root = query.from(Cliente.class);
        // poner joins de vehículo y service
        List<Predicate> filtros = new ArrayList<>();
        filtros.add(cb.equal(root.get("activo"), Boolean.TRUE));
        if (!dni.isBlank())
            filtros.add(cb.like(cb.lower(root.get("dni")), "%" + dni.toLowerCase() + "%"));
        if (!lastName.isBlank())
            filtros.add(cb.like(cb.lower(root.get("apellido")), "%" + lastName.toLowerCase(Locale.ROOT) + "%"));
        if (!firstName.isBlank())
            filtros.add(cb.like(cb.lower(root.get("nombre")), "%" + firstName.toLowerCase(Locale.ROOT) + "%"));

        query.where(cb.and(filtros.toArray(filtros.toArray(new Predicate[0]))));
        query.orderBy(cb.asc(root.get("apellido")));
        return em.createQuery(query).getResultList();
    }


}
