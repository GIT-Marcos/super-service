package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.VentaRepuestoDAO;
import SPRService.SPRService.entities.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import SPRService.SPRService.enums.EstadoVentaRepuesto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class VentaRepuestoDAOImpl extends GenericDAOImpl<VentaRepuesto, Long> implements VentaRepuestoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public VentaRepuestoDAOImpl() {
        super(VentaRepuesto.class);
    }

    @Override
    public List<VentaRepuesto> buscarVentas(Long codVenta, List<EstadoVentaRepuesto> estadosVenta,
                                            BigDecimal montoMinimo, BigDecimal montomaximo, String nombreColOrdenar,
                                            Integer tipoOrden, LocalDate fechaMinima, LocalDate fechaMaxima) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<VentaRepuesto> query = cb.createQuery(VentaRepuesto.class);
        Root<VentaRepuesto> root = query.from(VentaRepuesto.class);
        Join<VentaRepuesto, NotaRetiro> joinNota = root.join("notaRetiro");
        Join<NotaRetiro, DetalleRetiro> joinDetalles = joinNota.join("detalleRetiroList");
        Join<DetalleRetiro, Repuesto> joinRepuesto = joinDetalles.join("repuesto");
        Join<Repuesto, Stock> joinStock = joinRepuesto.join("stock");
        List<Predicate> filtros = new ArrayList<>();

        //SI SE QUIERE BUSCAR ALGO POR CÓDIGO...
        if (codVenta != null) {
            filtros.add(cb.equal(root.get("id"), String.valueOf(codVenta)));
        }
        //SI SE BUSCA UN ESTADO != DE CUALQUIERA...
        if (estadosVenta != null && !estadosVenta.isEmpty()) {
            filtros.add(root.get("estadoVenta").in(estadosVenta));
        }

        //FILTROS DEL MONTO
        if (montoMinimo != null && montomaximo != null) {
            filtros.add(cb.between(root.get("montoTotal"), montoMinimo, montomaximo));
        } else if (montoMinimo != null) {
            filtros.add(cb.greaterThanOrEqualTo(root.get("montoTotal"), montoMinimo));
        } else if (montomaximo != null) {
            filtros.add(cb.lessThanOrEqualTo(root.get("montoTotal"), montomaximo));
        }

        if (fechaMinima != null && fechaMaxima != null) {
            filtros.add(cb.between(root.get("fechaVenta"), fechaMinima, fechaMaxima));
        } else if (fechaMinima != null) {
            filtros.add(cb.greaterThanOrEqualTo(root.get("fechaVenta"), fechaMinima));
        } else if (fechaMaxima != null) {
            filtros.add(cb.lessThanOrEqualTo(root.get("fechaVenta"), fechaMaxima));
        }

        query.where(cb.and(filtros.toArray(new Predicate[0])));
        if (tipoOrden == 0) {
            query.orderBy(cb.asc(root.get(nombreColOrdenar)));
        } else if (tipoOrden == 1) {
            query.orderBy(cb.desc(root.get(nombreColOrdenar)));
        }

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Object[]> cantidadVentasPorMeses(Integer anio) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT MONTH(v.fechaVenta), COUNT(v) "
                                + "FROM VentaRepuesto v "
                                + "WHERE YEAR(v.fechaVenta) = :anio "
                                + "AND v.activo = true "
                                + "GROUP BY MONTH(v.fechaVenta) "
                                + "ORDER BY MONTH(v.fechaVenta)",
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
                                + "ORDER BY MONTH(v.fechaVenta)",
                        Object[].class)
                .setParameter("anio", anio)
                .getResultList();
    }

    @Override
    public VentaRepuesto borradoLogico(VentaRepuesto ventaRepuesto, AuditoriaVenta auditoriaVenta) {
        EntityManager em = emProvider.get();
        em.persist(auditoriaVenta);
        return em.merge(ventaRepuesto);
    }
}
