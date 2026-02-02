package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ClienteDAO;
import SPRService.SPRService.entities.Cliente;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Singleton
public class ClienteDAOImpl extends GenericDAOImpl<Cliente, Long> implements ClienteDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public ClienteDAOImpl() {
        super(Cliente.class);
    }

    @Override
    public List<Cliente> verTodos() {
        return filteredSearch("", "", "");
    }

    @Override
    public Optional<Cliente> verOperacionesConVehiculos(Long id) {
        EntityManager em = emProvider.get();
        // Ventas
        Optional<Cliente> result = em.createQuery("select c from Cliente c " +
                                "left join fetch c.ventas v " +
                                "left join fetch c.contactosCliente " +
                                "where c.id = :id",
                        Cliente.class)
                .setParameter("id", id)
                .getResultStream().findAny();

        if (result.isEmpty()) return result;

        // Services
        em.createQuery("select c from Cliente c " +
                                "left join fetch c.services s " +
                                "left join fetch s.orden o " +
                                "left join fetch o.estadoIngreso " +
                                "left join fetch o.vehiculo " +
                                "where c.id = :id",
                        Cliente.class)
                .setParameter("id", id)
                .getResultList();

        // vehículos
        em.createQuery("select c from Cliente c " +
                                "left join fetch c.vehiculos v " +
                                "left join fetch v.modeloVehiculo mo " +
                                "left join fetch mo.marcaVehiculo " +
                                "where c.id = :id",
                        Cliente.class)
                .setParameter("id", id)
                .getResultList();

        return result;
    }

    @Override
    public Optional<Cliente> verDatosContacto(Long id) {
        EntityManager em = emProvider.get();
        // eMails
        Optional<Cliente> result = em.createQuery("select c from Cliente c " +
                                "left join fetch c.contactosCliente co " +
                                "left join fetch co.emailSet " +
                                "where c.id = :id",
                Cliente.class)
                .setParameter("id", id)
                .getResultStream().findFirst();

        // teléfonos
        em.createQuery("select c from Cliente c " +
                                "left join fetch c.contactosCliente co " +
                                "left join fetch co.nroTelefonoSet " +
                                "where c.id = :id",
                        Cliente.class)
                .setParameter("id", id)
                .getResultList();

        return result;
    }

    @Override
    public List<Cliente> filteredSearch(String dni, String lastName, String firstName) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
        Root<Cliente> root = query.from(Cliente.class);

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
