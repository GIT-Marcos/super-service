package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class ServiceDAOImpl extends GenericDAOImpl<Service, Long> implements ServiceDAO {

    @Inject
    Provider<EntityManager> emProvider;
    private final String DTO = "SPRService.SPRService.DTOs.DatosReporteServiceDTO";

    public ServiceDAOImpl() {
        super(Service.class);
    }


    @Override
    public List<Service> verTodos() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT s FROM Service s " +
                                "JOIN FETCH s.orden o " +
                                "JOIN FETCH o.trabajos " +
                                "LEFT JOIN FETCH s.pagos " +
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
        query.distinct(true);
        //todo: usar este formato en los otros
        root.fetch("cliente", JoinType.LEFT);
        root.fetch("orden", JoinType.LEFT).fetch("trabajos", JoinType.LEFT);
        root.fetch("pagos", JoinType.LEFT);

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

    @Override
    public List<Object[]> totalIngresosAnual(Integer anio) {
        EntityManager em = emProvider.get();

        return em.createQuery(
                        "SELECT MONTH(s.fechaCarga), SUM(s.montoTotal) " +
                                "FROM Service s " +
                                "WHERE YEAR(s.fechaCarga) = :anio " +
                                "AND s.estadoService = :estado " +
                                "GROUP BY MONTH(s.fechaCarga) " +
                                "ORDER BY MONTH(s.fechaCarga) ASC",
                        Object[].class)
                .setParameter("anio", anio)
                .setParameter("estado", EstadoService.PAGADO)
                .getResultList();
    }

    @Override
    public List<Object[]> cantidadDeServicesAnual(Integer anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT MONTH(s.fechaCarga), COUNT(s.montoTotal) " +
                                "FROM Service s " +
                                "WHERE YEAR(s.fechaCarga) = :anio " +
                                "AND s.estadoService = :estado " +
                                "GROUP BY MONTH(s.fechaCarga) " +
                                "ORDER BY MONTH(s.fechaCarga) ASC",
                        Object[].class)
                .setParameter("anio", anio)
                .setParameter("estado", EstadoService.PAGADO)
                .getResultList();
    }

    @Override
    public DatosReporteServiceDTO generarDatosAnuales(Integer anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT new " + DTO + "(" +
                                "  SUM(s.montoTotal), " +
                                "  AVG(s.montoTotal), " +
                                "  COUNT(s), " +
                                "  SUM(s.orden.totalTrabajos), " +
                                "  SUM(s.orden.totalRepuestos)" +
                                ") " +
                                "FROM Service s " +
                                "WHERE YEAR(s.fechaCarga) = :anio " +
                                "AND s.estadoService = :estado",
                        DatosReporteServiceDTO.class)
                .setParameter("anio", anio)
                .setParameter("estado", EstadoService.PAGADO)
                .getSingleResult();
    }
}
