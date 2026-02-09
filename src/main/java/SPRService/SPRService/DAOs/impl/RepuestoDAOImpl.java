package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.RepuestoDAO;
import SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroRepuestoDTO;
import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Singleton
public class RepuestoDAOImpl extends GenericDAOImpl<Repuesto, Long> implements RepuestoDAO {

    @Inject
    private Provider<EntityManager> emProvider;
    private final String DTO = "SPRService.SPRService.DTOs.ReporteUsoDeRepuestosDTO";

    public RepuestoDAOImpl() {
        super(Repuesto.class);
    }

    @Override
    public List<Repuesto> verTodos() {
        return buscarRepuestos(new FiltroRepuestoDTO());
    }

    @Override
    public List<Repuesto> validarUnicidadCodBarras(Repuesto r) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT r FROM Repuesto r " +
                                "WHERE r.codBarra = :cod",
                        Repuesto.class)
                .setParameter("cod", r.getCodBarra())
                .setMaxResults(1)
                .getResultList();
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
    public List<Repuesto> buscarRepuestos(FiltroRepuestoDTO filtro) {
        EntityManager em = emProvider.get();
        TypedQuery<Repuesto> query = crearQueryBusqueda(em, filtro);
        return query.getResultList();
    }

    @Override
    public ResultadoPaginado<Repuesto> buscarRepuestosPaginado(FiltroRepuestoDTO filtro) {
        EntityManager em = emProvider.get();

        // 1. Obtener el conteo total
        Long total = contarRepuestos(em, filtro);

        // 2. Obtener los resultados paginados
        TypedQuery<Repuesto> query = crearQueryBusqueda(em, filtro);

        if (filtro.offset() != null && filtro.limit() != null) {
            query.setFirstResult(filtro.offset());
            query.setMaxResults(filtro.limit());
        }

        List<Repuesto> resultados = query.getResultList();

        return new ResultadoPaginado<>(resultados, total);
    }

    /**
     * Cuenta el total de repuestos que coinciden con el filtro (sin paginación)
     */
    private Long contarRepuestos(EntityManager em, FiltroRepuestoDTO filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Repuesto> root = countQuery.from(Repuesto.class);

        Join<Repuesto, MarcaRepuesto> joinMarca = root.join("marcaRepuesto", JoinType.LEFT);
        Join<Repuesto, Stock> joinStock = root.join("stock", JoinType.LEFT);

        List<Predicate> predicates = construirPredicados(cb, root, joinMarca, joinStock, filtro);

        countQuery.select(cb.count(root));
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }

    /**
     * Crea la query de búsqueda con todos los filtros y ordenamiento
     */
    private TypedQuery<Repuesto> crearQueryBusqueda(EntityManager em, FiltroRepuestoDTO filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Repuesto> query = cb.createQuery(Repuesto.class);
        Root<Repuesto> root = query.from(Repuesto.class);

        // Fetches para evitar N+1
        Fetch<Repuesto, Stock> fetchStock = root.fetch("stock", JoinType.LEFT);
        fetchStock.fetch("ubicacion", JoinType.LEFT);
        root.fetch("marcaRepuesto", JoinType.LEFT);

        // Joins para filtros
        Join<Repuesto, MarcaRepuesto> joinMarca = root.join("marcaRepuesto", JoinType.LEFT);
        Join<Repuesto, Stock> joinStock = root.join("stock", JoinType.LEFT);

        List<Predicate> predicates = construirPredicados(cb, root, joinMarca, joinStock, filtro);

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        // Ordenamiento
        if (filtro.tipoOrden() != null && filtro.colOrden() != null) {
            if (filtro.tipoOrden() == 0) {
                query.orderBy(cb.asc(root.get(filtro.colOrden())));
            } else if (filtro.tipoOrden() == 1) {
                query.orderBy(cb.desc(root.get(filtro.colOrden())));
            }
        }

        return em.createQuery(query);
    }

    /**
     * Construye los predicados (condiciones WHERE) basándose en el filtro
     */
    private List<Predicate> construirPredicados(CriteriaBuilder cb, Root<Repuesto> root,
                                                Join<Repuesto, MarcaRepuesto> joinMarca,
                                                Join<Repuesto, Stock> joinStock,
                                                FiltroRepuestoDTO filtro) {
        List<Predicate> filtros = new ArrayList<>();

        filtros.add(cb.equal(root.get("activo"), Boolean.TRUE));
        filtros.add(cb.equal(joinStock.get("activo"), Boolean.TRUE));

        if (filtro.codBarras() != null && !filtro.codBarras().isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("codBarra")),
                    "%" + filtro.codBarras().toLowerCase() + "%"));
        }
        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("detalle")),
                    "%" + filtro.nombre().toLowerCase() + "%"));
        }
        if (filtro.marca() != null && !filtro.marca().isBlank()) {
            filtros.add(cb.like(cb.lower(joinMarca.get("nombreMarca")),
                    "%" + filtro.marca().toLowerCase(Locale.ROOT) + "%"));
        }

        // Filtros de stock
        if (filtro.stockNormal() && !filtro.stockBajo()) {
            filtros.add(cb.greaterThan(joinStock.get("cantidadExistente"),
                    joinStock.get("cantMinima")));
        } else if (filtro.stockBajo() && !filtro.stockNormal()) {
            filtros.add(cb.lessThanOrEqualTo(joinStock.get("cantidadExistente"),
                    joinStock.get("cantMinima")));
        }

        return filtros;
    }

    @Override
    public List<Object[]> masRetiradosParaVenta(Integer cantidadRepuestos,
                                                LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        EntityManager em = emProvider.get();
        TypedQuery<Object[]> query = em.createQuery(
                        "SELECT dr.repuesto, COUNT(dr.repuesto) "
                                + "FROM NotaRetiro nr "
                                + "JOIN nr.detalleRetiro dr "
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
    public ReporteUsoDeRepuestosDTO usoDeRepuestos(LocalDateTime fechaMin, LocalDateTime fechaMax) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT new " + DTO + "(" +
                                "SUM(CASE WHEN n.tipoUso = SPRService.SPRService.entities.NotaRetiro.TipoUsoRetiro.VENTA THEN dr.cantidadRetirada ELSE 0.0 END), " +
                                "SUM(CASE WHEN n.tipoUso = SPRService.SPRService.entities.NotaRetiro.TipoUsoRetiro.SERVICE THEN dr.cantidadRetirada ELSE 0.0 END) " +
                                ") " +
                                "FROM NotaRetiro n " +
                                "JOIN n.detalleRetiro dr " +
                                "WHERE n.fecha BETWEEN :fMin AND :fMax",
                        ReporteUsoDeRepuestosDTO.class)
                .setParameter("fMin", fechaMin)
                .setParameter("fMax", fechaMax)
                .getSingleResult();
    }
}