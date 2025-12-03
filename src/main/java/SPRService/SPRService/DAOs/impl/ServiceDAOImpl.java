package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.AuditoriaVenta;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        if (filtros.codigo() != null && filtros.codigo() != 0)
            predicates.add(cb.equal(root.get("id"), filtros.codigo()));

        if (filtros.fchMinCarga() != null && filtros.fchMaxCarga() != null) {
            predicates.add(cb.between(root.get("fechaCarga"), filtros.fchMinCarga(), filtros.fchMaxCarga()));
        } else if (filtros.fchMinCarga() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCarga"), filtros.fchMinCarga()));
        } else if (filtros.fchMaxCarga() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("fechaCarga"), filtros.fchMaxCarga()));
        }

        if (filtros.fchMinRetiro() != null && filtros.fchMaxRetiro() != null) {
            predicates.add(cb.between(root.get("fechaEntrega"), filtros.fchMinRetiro(), filtros.fchMaxRetiro()));
        } else if (filtros.fchMinRetiro() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("fechaEntrega"), filtros.fchMinRetiro()));
        } else if (filtros.fchMaxRetiro() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("fechaEntrega"), filtros.fchMaxRetiro()));
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
    public Service cancelarService(Service s, AuditoriaVenta a) {
        EntityManager em = emProvider.get();
        em.persist(a);
        return em.merge(s);
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

    @Override
    public ReporteComparacionDTO generarComparacion(LocalDate fechaMin, LocalDate fechaMax) {
        EntityManager em = emProvider.get();

        // ===== QUERY SERVICE PAGADOS =====
        Object[] serviceData = em.createQuery(
                        "SELECT COALESCE(SUM(s.montoTotal), 0), " +
                                "       COUNT(s) " +
                                "FROM Service s " +
                                "WHERE s.estadoService = :estado " +
                                "AND s.fechaCarga BETWEEN :fMin AND :fMax",
                        Object[].class)
                .setParameter("estado", EstadoService.PAGADO)
                .setParameter("fMin", fechaMin.atStartOfDay())
                .setParameter("fMax", fechaMax.atTime(23, 59, 59))
                .getSingleResult();

        BigDecimal ingService = (BigDecimal) serviceData[0];
        Long cantService = (Long) serviceData[1];

        // ===== QUERY VENTAS PAGADAS =====
        Object[] ventaData = em.createQuery(
                        "SELECT COALESCE(SUM(v.montoTotal), 0), " +
                                "       COUNT(v) " +
                                "FROM VentaRepuesto v " +
                                "WHERE v.estadoVenta = :estado " +
                                "AND v.fechaVenta BETWEEN :fMin AND :fMax",
                        Object[].class)
                .setParameter("estado", EstadoVentaRepuesto.PAGADO)
                .setParameter("fMin", fechaMin)
                .setParameter("fMax", fechaMax)
                .getSingleResult();

        BigDecimal ingVenta = (BigDecimal) ventaData[0];
        Long cantVenta = (Long) ventaData[1];

        // ===== CONSTRUCCIÓN DEL DTO =====
        return new ReporteComparacionDTO(
                ingService,
                ingVenta,
                cantService,
                cantVenta
        );
    }

}
