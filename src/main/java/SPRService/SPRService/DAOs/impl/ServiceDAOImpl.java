package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.Orden;
import SPRService.SPRService.entities.Service;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class ServiceDAOImpl extends GenericDAOImpl<Service, Long> implements ServiceDAO {

    @Inject
    Provider<EntityManager> emProvider;

    public ServiceDAOImpl() {
        super(Service.class);
    }


    @Override
    public List<Service> verTodos() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT s FROM Service s " +
                                "JOIN FETCH s.orden o " +
                                "JOIN FETCH o.trabajos " +
                                "ORDER BY s.fechaCarga ASC",
                        Service.class)
                .getResultList();
    }

    @Override
    public List<Service> buscarConFiltros(FiltroServiceDTO filtros) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Service> query = cb.createQuery(Service.class);
        Root<Service> root = query.from(Service.class);
        Join<Service, Cliente> joinCliente = root.join("cliente", JoinType.LEFT);
        Join<Service, Orden> joinOrden = root.join("orden", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        //todo: agregar estado a los services
        //predicates.add(cb.equal(root.get("activo"), Boolean.TRUE));

        if (filtros.codigo() != null && filtros.codigo() != 0)
            predicates.add(cb.equal(root.get("id"), filtros.codigo()));

        if (filtros.fechaMinima() != null && filtros.fechaMaxima() != null) {
            predicates.add(cb.between(root.get("fechaCarga"), filtros.fechaMinima(), filtros.fechaMaxima()));
        } else if (filtros.fechaMinima() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCarga"), filtros.fechaMinima()));
        } else if (filtros.fechaMaxima() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("fechaCarga"), filtros.fechaMaxima()));
        }

        if (filtros.prioridadServices() != null && !filtros.prioridadServices().isEmpty()) {
            predicates.add(root.get("prioridad").in(filtros.prioridadServices()));
        }

        if (filtros.estados() != null && !filtros.estados().isEmpty()) {
            predicates.add(root.get("estadoService").in(filtros.estados()));
        }

        query.where(cb.and(predicates.toArray(predicates.toArray(new Predicate[0]))));
        return em.createQuery(query).getResultList();
    }
}
