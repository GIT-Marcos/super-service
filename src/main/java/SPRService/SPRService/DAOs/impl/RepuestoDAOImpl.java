package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.RepuestoDAO;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RepuestoDAOImpl extends GenericDAOImpl<Repuesto, Long> implements RepuestoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public RepuestoDAOImpl() {
        super(Repuesto.class);
    }

    @Override
    public List<Repuesto> allActiveProducts() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT r FROM Repuesto r " +
                        "WHERE r.activo = true",
                Repuesto.class).getResultList();
    }

    @Override
    public Long cuentaRespBajoStock() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT COUNT(r) FROM Repuesto r "
                                + "WHERE r.stock.cantidadExistente <= r.stock.cantMinima AND "
                                + "r.stock.activo = true",
                        Long.class)
                .getSingleResult();
    }

    @Override
    public List<Boolean> consultaEstado(String codBarra) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT r.activo FROM Repuesto r " +
                                "WHERE r.codBarra = :codBarra",
                        Boolean.class)
                .setParameter("codBarra", codBarra)
                .setMaxResults(1)
                .getResultList();
    }

    @Override
    public Long consultarId(String codBarra) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT r.id FROM Repuesto r " +
                                "WHERE r.codBarra = :codBarra",
                        Long.class)
                .setParameter("codBarra", codBarra)
                .getSingleResult();
    }

    @Override
    public List<Repuesto> buscarConFiltros(String inputParaBuscar, Integer opcionBusqueda, Boolean stockNormal,
                                           Boolean stockBajo, String nombreColumnaOrnenar, Integer tipoOrden) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Repuesto> query = cb.createQuery(Repuesto.class);
        Root<Repuesto> root = query.from(Repuesto.class);
        Join<Repuesto, Stock> joinStock = root.join("stock");
        List<Predicate> filtros = new ArrayList<>();
        filtros.add(cb.equal(root.get("activo"), Boolean.TRUE));

        //SI SE QUIERE BUSCAR ALGO...
        if (inputParaBuscar != null) {
            switch (opcionBusqueda) {
                case 0: //se eligió cod barra
                    filtros.add(cb.like(cb.lower(root.get("codBarra")), "%" + inputParaBuscar.toLowerCase() + "%"));
                    break;
                case 1: //se eligió detalle
                    filtros.add(cb.like(cb.lower(root.get("detalle")), "%" + inputParaBuscar.toLowerCase() + "%"));
                    break;
                case 2:
                    filtros.add(cb.like(cb.lower(root.get("marca")), "%" + inputParaBuscar.toLowerCase() + "%"));
                    break;
                default:
                    throw new AssertionError();
            }
        }
        //SI LOS 2 VIENEN VERDADEROS, O SEA QUIERE VER TODOS, NO ENTRA EN NINGÚN IF
        if (stockNormal && !stockBajo) {
            filtros.add(cb.greaterThan(joinStock.get("cantidad"), joinStock.get("cantMinima")));
        } else if (stockBajo && !stockNormal) {
            filtros.add(cb.lessThanOrEqualTo(joinStock.get("cantidad"), joinStock.get("cantMinima")));
        }
        query.where(cb.and(filtros.toArray(new Predicate[0])));
        if (tipoOrden == 0) {
            query.orderBy(cb.asc(root.get(nombreColumnaOrnenar)));
        } else if (tipoOrden == 1) {
            query.orderBy(cb.desc(root.get(nombreColumnaOrnenar)));
        }
        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Repuesto> buscarConCriteria(String codBarras, String nombreProd, String marcaProd,
                                            Boolean verStockNormal, Boolean verStockBajo,
                                            String colParaOrdenar, Integer tipoOrden) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Repuesto> query = cb.createQuery(Repuesto.class);
        Root<Repuesto> root = query.from(Repuesto.class);
        Join<Repuesto, Stock> joinStock = root.join("stock", JoinType.LEFT);
        List<Predicate> filtros = new ArrayList<>();
        filtros.add(cb.equal(root.get("activo"), Boolean.TRUE));
        filtros.add(cb.equal(joinStock.get("activo"), Boolean.TRUE));

        if (!codBarras.isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("codBarra")), "%" + codBarras.toLowerCase() + "%"));
        }
        if (!nombreProd.isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("detalle")), "%" + nombreProd.toLowerCase() + "%"));
        }
        if (!marcaProd.isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("marca")), "%" + marcaProd.toLowerCase() + "%"));
        }
        //SI LOS 2 VIENEN VERDADEROS, O SEA QUIERE VER TODOS, NO ENTRA EN NINGÚN IF
        if (verStockNormal && !verStockBajo) {
            filtros.add(cb.greaterThan(joinStock.get("cantidadExistente"), joinStock.get("cantMinima")));
        } else if (verStockBajo && !verStockNormal) {
            filtros.add(cb.lessThanOrEqualTo(joinStock.get("cantidadExistente"), joinStock.get("cantMinima")));
        }
        query.where(cb.and(filtros.toArray(new Predicate[0])));
        if (tipoOrden != null && colParaOrdenar != null) {
            if (tipoOrden == 0) {
                query.orderBy(cb.asc(root.get(colParaOrdenar)));
            } else if (tipoOrden == 1) {
                query.orderBy(cb.desc(root.get(colParaOrdenar)));
            }
        }
        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Object[]> masRetiradosParaVenta(Integer cantidadRepuestos,
                                                LocalDate fechaInicio, LocalDate fechaFin) {
        EntityManager em = emProvider.get();
        TypedQuery<Object[]> query = em.createQuery(
                        "SELECT dr.repuesto, COUNT(dr.repuesto) "
                                + "FROM NotaRetiro nr "
                                + "JOIN nr.detalleRetiroList dr "
                                + "WHERE nr.fecha BETWEEN :fechaInicio AND :fechaFin "
                                + "GROUP BY dr.repuesto "
                                + "ORDER BY COUNT(dr.repuesto) DESC",
                        Object[].class)
                .setParameter("fechaInicio", fechaInicio)
                .setParameter("fechaFin", fechaFin)
                .setMaxResults(cantidadRepuestos);
        return query.getResultList();
    }

    @Override
    public void borradoLogico(Repuesto repuesto) {
        EntityManager em = emProvider.get();
        em.merge(repuesto);
    }
}
