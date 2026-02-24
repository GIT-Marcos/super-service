package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.DatosReporteServiceDTO;
import SPRService.SPRService.DTOs.ReporteComparacionDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.AuditoriaVenta;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.entities.Orden;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceDAOImpl extends GenericDAOImpl<Service, Long> implements ServiceDAO {

    @Inject
    Provider<EntityManager> emProvider;
    private final String DTO = "SPRService.SPRService.DTOs.DatosReporteServiceDTO";

    public ServiceDAOImpl() {
        super(Service.class);
    }

    @Override
    public List<Service> verTodos() {
        return buscarConFiltros(new FiltroServiceDTO());
    }

    @Override
    public Optional<Service> traerDatosParaModificar(Long id) {
        EntityManager em = emProvider.get();
        // trabajos
        Optional<Service> result = em.createQuery("select s from Service s " +
                                "left join fetch s.orden o " +
                                "left join fetch o.vehiculo v " +
                                "left join fetch v.modeloVehiculo mo " +
                                "left join fetch mo.marcaVehiculo " +
                                "left join fetch o.estadoIngreso " +
                                "left join fetch s.cliente " +
                                "left join fetch o.trabajos " +
                                "where s.id = :id",
                        Service.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
        // detalles retiro
        em.createQuery("select o from Orden o " +
                                "left join fetch o.notaRetiro n " +
                                "left join fetch n.detalleRetiro d " +
                                "left join fetch d.repuesto r " +
                                "left join fetch r.marcaRepuesto " +
                                "left join fetch r.stock " +
                                "where o.service.id = :id",
                        Orden.class)
                .setParameter("id", id)
                .getResultList();
        // pagos
        em.createQuery("select s from Service s " +
                                "left join fetch s.pagos " +
                                "where s.id = :id",
                        Service.class)
                .setParameter("id", id)
                .getResultList();
        return result;
    }

    @Override
    public Optional<Service> datosPagos(Long id) {
        EntityManager em = emProvider.get();
        return em.createQuery("select s from Service s " +
                                "left join fetch s.pagos " +
                                "where s.id = :id",
                        Service.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    @Override
    public Optional<Service> traerDatosParaTicket(Long id) {
        EntityManager em = emProvider.get();
        return em.createQuery("select s from Service s " +
                                "left join fetch s.cliente " +
                                "left join fetch s.orden o " +
                                "left join fetch o.vehiculo v " +
                                "left join fetch v.modeloVehiculo mo " +
                                "left join fetch mo.marcaVehiculo " +
                                "where s.id = :id",
                        Service.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    @Override
    public List<Service> buscarConFiltros(FiltroServiceDTO filtros) {
        EntityManager em = emProvider.get();
        return crearQueryBusqueda(em, filtros).getResultList();
    }

    @Override
    public ResultadoPaginado<Service> buscarPaginado(FiltroServiceDTO filtros) {
        EntityManager em = emProvider.get();

        // 1. Obtener el conteo total
        Long total = contarServices(em, filtros);

        // 2. Obtener los resultados paginados
        TypedQuery<Service> query = crearQueryBusqueda(em, filtros);

        if (filtros.offset() != null && filtros.limit() != null) {
            query.setFirstResult(filtros.offset());
            query.setMaxResults(filtros.limit());
        }

        List<Service> resultados = query.getResultList();
        return new ResultadoPaginado<>(resultados, total);
    }

    /**
     * Cuenta el total de services que coinciden con el filtro
     */
    private Long contarServices(EntityManager em, FiltroServiceDTO filtros) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Service> root = countQuery.from(Service.class);

        Join<Service, Cliente> joinCliente = root.join("cliente", JoinType.LEFT);
        List<Predicate> predicates = construirPredicados(cb, root, joinCliente, filtros);
        countQuery.select(cb.count(root));
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(countQuery).getSingleResult();
    }

    /**
     * Crea la query de búsqueda con todos los filtros
     */
    private TypedQuery<Service> crearQueryBusqueda(EntityManager em, FiltroServiceDTO filtros) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Service> query = cb.createQuery(Service.class);
        Root<Service> root = query.from(Service.class);

        root.fetch("cliente", JoinType.LEFT);
        Join<Service, Cliente> joinCliente = root.join("cliente", JoinType.LEFT);

        query.distinct(true);
        List<Predicate> predicates = construirPredicados(cb, root, joinCliente, filtros);
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(root.get("fechaEntrega")));
        return em.createQuery(query);
    }

    /**
     * Construye los predicados (condiciones WHERE) basándose en el filtro
     */
    private List<Predicate> construirPredicados(CriteriaBuilder cb, Root<Service> root,
                                                Join<Service, Cliente> joinCliente,
                                                FiltroServiceDTO filtros) {
        List<Predicate> predicates = new ArrayList<>();

        if (filtros.codigo() != null && filtros.codigo() != 0) {
            predicates.add(cb.equal(root.get("id"), filtros.codigo()));
        }

        if (filtros.dniCliente() != null && !filtros.dniCliente().isBlank()) {
            predicates.add(cb.like(cb.lower(joinCliente.get("dni")),
                    "%" + filtros.dniCliente().toLowerCase() + "%"));
        }

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

        if (filtros.montoMinimo() != null && filtros.montoMaximo() != null) {
            predicates.add(cb.between(root.get("montoTotal"), filtros.montoMinimo(), filtros.montoMaximo()));
        } else if (filtros.montoMinimo() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("montoTotal"), filtros.montoMinimo()));
        } else if (filtros.montoMaximo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("montoTotal"), filtros.montoMaximo()));
        }

        return predicates;
    }

    @Override
    public void cargarAuditoriaCancelacion(AuditoriaVenta a) {
        EntityManager em = emProvider.get();
        em.persist(a);
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
    public ReporteComparacionDTO generarComparacion(LocalDateTime fechaMin, LocalDateTime fechaMax) {
        EntityManager em = emProvider.get();

        Object[] serviceData = em.createQuery(
                        "SELECT COALESCE(SUM(s.montoTotal), 0), " +
                                "       COUNT(s) " +
                                "FROM Service s " +
                                "WHERE s.estadoService = :estado " +
                                "AND s.fechaCarga BETWEEN :fMin AND :fMax",
                        Object[].class)
                .setParameter("estado", EstadoService.PAGADO)
                .setParameter("fMin", fechaMin)
                .setParameter("fMax", fechaMax)
                .getSingleResult();

        BigDecimal ingService = (BigDecimal) serviceData[0];
        Long cantService = (Long) serviceData[1];

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
        return new ReporteComparacionDTO(ingService, ingVenta, cantService, cantVenta);
    }
}