package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.VentaRepuestoDAO;
import SPRService.SPRService.DTOs.ReporteIngresosRepuestoDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class VentaRepuestoDAOImpl extends GenericDAOImpl<VentaRepuesto, Long> implements VentaRepuestoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public VentaRepuestoDAOImpl() {
        super(VentaRepuesto.class);
    }

    @Override
    public Optional<VentaRepuesto> verDetalle(Long id) {
        EntityManager em = emProvider.get();
        // detalles de nota
        Optional<VentaRepuesto> result = em.createQuery("select v from VentaRepuesto v " +
                                "left join fetch v.notaRetiro n " +
                                "left join fetch n.detalleRetiro d " +
                                "left join fetch d.repuesto r " +
                                "left join fetch r.marcaRepuesto " +
                                "left join fetch v.cliente c " +
                                "left join fetch c.contactosCliente " +
                                "where v.id = :id",
                        VentaRepuesto.class)
                .setParameter("id", id)
                .getResultStream().findFirst();

        if (result.isPresent()) {
            // pagos de venta
            em.createQuery("select v from VentaRepuesto v " +
                                    "left join fetch v.pagos " +
                                    "where v.id = :id",
                            VentaRepuesto.class)
                    .setParameter("id", id)
                    .getResultList();

            if (result.get().getCliente() != null) {
                // emails cliente
                em.createQuery("select v from VentaRepuesto v " +
                                        "left join fetch v.cliente c " +
                                        "left join fetch c.contactosCliente co " +
                                        "left join fetch co.emailSet " +
                                        "where v.id = :id",
                                VentaRepuesto.class)
                        .setParameter("id", id)
                        .getResultList();
                // tels cliente
                em.createQuery("select v from VentaRepuesto v " +
                                        "left join fetch v.cliente c " +
                                        "left join fetch c.contactosCliente co " +
                                        "left join fetch co.nroTelefonoSet " +
                                        "where v.id = :id",
                                VentaRepuesto.class)
                        .setParameter("id", id)
                        .getResultList();
            }
        }

        return result;
    }

    @Override
    public Optional<VentaRepuesto> fetchParaEdicion(Long id) {
        EntityManager em = emProvider.get();
        // ventas de cliente
//        Optional<VentaRepuesto> result = em.createQuery("select v from VentaRepuesto v " +
//                                "left join fetch v.cliente c " +
//                                "left join fetch c.ventas " +
//                                "where v.id = :id",
//                VentaRepuesto.class)
//                .setParameter("id", id)
//                .getResultStream().findFirst();
        // pagos de la venta
        return em.createQuery("select v from VentaRepuesto v " +
                                "left join fetch v.pagos " +
                                "where v.id = :id",
                        VentaRepuesto.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    @Override
    public ResultadoPaginado<VentaRepuesto> buscarPaginadoConFiltros(FiltroVentaRepuestoDTO filtro,
                                                                     int pagina, int tamanioPagina) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // --- 1. CONSULTA PARA OBTENER EL TOTAL DE RESULTADOS (CONTEO) ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<VentaRepuesto> countRoot = countQuery.from(VentaRepuesto.class);
        countQuery.select(cb.count(countRoot));
        // Aplicamos los filtros a la consulta de conteo
        aplicarFiltros(filtro, cb, countQuery, countRoot);

        Long totalResultados = em.createQuery(countQuery).getSingleResult();

        // Si no hay resultados, no hace falta ejecutar la segunda consulta
        if (totalResultados == 0) {
            return new ResultadoPaginado<>(new ArrayList<>(), 0L);
        }

        // --- 2. CONSULTA PARA OBTENER LOS DATOS DE LA PÁGINA ACTUAL ---
        CriteriaQuery<VentaRepuesto> dataQuery = cb.createQuery(VentaRepuesto.class);
        Root<VentaRepuesto> dataRoot = dataQuery.from(VentaRepuesto.class);

        dataRoot.fetch("cliente", JoinType.LEFT);

        dataQuery.select(dataRoot).distinct(true);
        // Volvemos a aplicar los mismos filtros, pero ahora a la consulta de datos
        aplicarFiltros(filtro, cb, dataQuery, dataRoot);

        // Aplicar ordenación
        if (filtro.tipoOrden() != null && filtro.colOrden() != null && !filtro.colOrden().isBlank()) {
            if (filtro.tipoOrden() == 0) { // Descendente
                dataQuery.orderBy(cb.desc(dataRoot.get(filtro.colOrden())));
            } else { // Ascendente
                dataQuery.orderBy(cb.asc(dataRoot.get(filtro.colOrden())));
            }
        } else {
            dataQuery.orderBy(cb.desc(dataRoot.get("fechaVenta"))); // Un orden por defecto
        }

        TypedQuery<VentaRepuesto> typedDataQuery = em.createQuery(dataQuery);

        // Aplicar paginación
        typedDataQuery.setFirstResult(pagina * tamanioPagina);
        typedDataQuery.setMaxResults(tamanioPagina);

        List<VentaRepuesto> ventas = typedDataQuery.getResultList();

        // --- 3. DEVOLVER EL RESULTADO COMPLETO ---
        return new ResultadoPaginado<>(ventas, totalResultados);
    }

    //todo: eliminar estado venta y que los reportes trabajen sobre las que están pagadas
    @Override
    public List<Object[]> cantidadVentasPorMeses(Integer anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT MONTH(v.fechaVenta), COUNT(v) "
                                + "FROM VentaRepuesto v "
                                + "WHERE YEAR(v.fechaVenta) = :anio "
                                + "AND v.activo = true "
                                + "GROUP BY MONTH(v.fechaVenta) "
                                + "ORDER BY MONTH(v.fechaVenta) ASC",
                        Object[].class)
                .setParameter("anio", anio)
                .getResultList();

    }

    @Override
    public List<Object[]> totalVentasPorMeses(Integer anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT MONTH(v.fechaVenta), SUM(v.montoTotal) "
                                + "FROM VentaRepuesto v "
                                + "WHERE YEAR(v.fechaVenta) = :anio "
                                + "AND v.activo = true "
                                + "GROUP BY MONTH(v.fechaVenta) "
                                + "ORDER BY MONTH(v.fechaVenta) ASC",
                        Object[].class)
                .setParameter("anio", anio)
                .getResultList();
    }

    public List<VentaRepuestosEnMesDTO> reporteTotalVentasDiariasEnMes(Integer anio, Integer mes) {
        EntityManager em = emProvider.get();
        String hql = "SELECT NEW SPRService.SPRService.DTOs.VentaRepuestosEnMesDTO(" +
                "    DAY(v.fechaVenta), " +
                "    SUM(v.montoTotal)" +
                ") " +
                "FROM VentaRepuesto v " +
                "WHERE YEAR(v.fechaVenta) = :anio " +
                "  AND MONTH(v.fechaVenta) = :mes " +
                "  AND v.activo = true " +
                "GROUP BY DAY(v.fechaVenta) " +
                "ORDER BY DAY(v.fechaVenta) ASC";

        TypedQuery<VentaRepuestosEnMesDTO> query = em.createQuery(hql, VentaRepuestosEnMesDTO.class);
        query.setParameter("anio", anio);
        query.setParameter("mes", mes);

        return query.getResultList();
    }

    @Override
    public Long cantidadDeVentasEnAnio(int anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT COUNT(v) FROM VentaRepuesto v " +
                                "WHERE YEAR(v.fechaVenta) = :anio " +
                                "AND v.activo = true",
                        Long.class)
                .setParameter("anio", anio)
                .getSingleResult();
    }

    @Override
    public BigDecimal ingresosDeVentasEnAnio(int anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT SUM(v.montoTotal) FROM VentaRepuesto v " +
                                "WHERE YEAR(v.fechaVenta) = :anio " +
                                "AND v.activo = true",
                        BigDecimal.class)
                .setParameter("anio", anio)
                .getSingleResult();
    }

    @Override
    public Double ingresosPromedioPorVentaEnAnio(int anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT AVG(v.montoTotal) FROM VentaRepuesto v " +
                                "WHERE YEAR(v.fechaVenta) = :anio " +
                                "AND v.activo = true",
                        Double.class)
                .setParameter("anio", anio)
                .getSingleResult();
    }

    @Override
    public List<ReporteIngresosRepuestoDTO> ingresosPorRepuesto(LocalDateTime fechaMin, LocalDateTime fechaMax, Integer cantidad) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT new SPRService.SPRService.DTOs.ReporteIngresosRepuestoDTO(" +
                                "r.codBarra, " +
                                "mr.nombreMarca, " +
                                "r.detalle, " +
                                "SUM(dr.cantidadRetirada), " +
                                "SUM(dr.subTotal)) " +
                                "FROM VentaRepuesto v " +
                                "JOIN v.notaRetiro nr " +
                                "JOIN nr.detalleRetiro dr " +
                                "JOIN dr.repuesto r " +
                                "JOIN r.marcaRepuesto mr " +
                                "WHERE v.estadoVenta = :estado " +
                                "AND v.activo = true " +
                                // SOLUCIÓN: Quitamos el OR ... IS NULL
                                "AND v.fechaVenta >= :fechaMin " +
                                "AND v.fechaVenta <= :fechaMax " +
                                "GROUP BY r.codBarra, mr.nombreMarca, r.detalle " +
                                "ORDER BY SUM(dr.subTotal) DESC",
                        ReporteIngresosRepuestoDTO.class)
                .setParameter("estado", EstadoVentaRepuesto.PAGADO)
                .setParameter("fechaMin", fechaMin)
                .setParameter("fechaMax", fechaMax)
                .setMaxResults(cantidad)
                .getResultList();
    }

    @Override
    public void auditoriaCancelacion(AuditoriaVenta auditoriaVenta) {
        EntityManager em = emProvider.get();
        em.persist(auditoriaVenta);
    }

    private void aplicarFiltros(FiltroVentaRepuestoDTO filtro, CriteriaBuilder cb, CriteriaQuery<?> query,
                                Root<VentaRepuesto> root) {
        List<Predicate> predicados = new ArrayList<>();

        if (filtro.codVenta() != null && filtro.codVenta() > 0) {
            predicados.add(cb.equal(root.get("id"), filtro.codVenta()));
        }
        if (filtro.dni() != null && !filtro.dni().isBlank()) {
            predicados.add(cb.like(cb.lower(root.get("cliente").get("dni")),
                    "%" + filtro.dni().toLowerCase() + "%"));
        }
        if (filtro.estados() != null && !filtro.estados().isEmpty()) {
            predicados.add(root.get("estadoVenta").in(filtro.estados()));
        }
        if (filtro.montoMin() != null && filtro.montoMax() != null) {
            predicados.add(cb.between(root.get("montoTotal"), filtro.montoMin(), filtro.montoMax()));
        } else if (filtro.montoMin() != null) {
            predicados.add(cb.greaterThanOrEqualTo(root.get("montoTotal"), filtro.montoMin()));
        } else if (filtro.montoMax() != null) {
            predicados.add(cb.lessThanOrEqualTo(root.get("montoTotal"), filtro.montoMax()));
        }
        if (filtro.fechaMin() != null && filtro.fechaMax() != null) {
            predicados.add(cb.between(root.get("fechaVenta"), filtro.fechaMin(), filtro.fechaMax()));
        } else if (filtro.fechaMin() != null) {
            predicados.add(cb.greaterThanOrEqualTo(root.get("fechaVenta"), filtro.fechaMin()));
        } else if (filtro.fechaMax() != null) {
            predicados.add(cb.lessThanOrEqualTo(root.get("fechaVenta"), filtro.fechaMax()));
        }

        // Aplicamos la lista de predicados a la consulta
        if (!predicados.isEmpty()) {
            query.where(cb.and(predicados.toArray(new Predicate[0])));
        }
    }
}
