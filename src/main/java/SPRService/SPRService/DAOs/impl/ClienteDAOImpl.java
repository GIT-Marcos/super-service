package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ClienteDAO;
import SPRService.SPRService.DTOs.ClientesMasIngresosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroClienteDTO;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ClienteDAOImpl extends GenericDAOImpl<Cliente, Long> implements ClienteDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public ClienteDAOImpl() {
        super(Cliente.class);
    }

    @Override
    public List<Cliente> verTodos() {
        return filteredSearch("", "", "", true, true);
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
    public List<Cliente> filteredSearch(String dni, String lastName, String firstName, boolean activos, boolean baja) {
        FiltroClienteDTO filtro = new FiltroClienteDTO(dni, lastName, firstName, activos, baja);
        EntityManager em = emProvider.get();
        return crearQueryBusqueda(em, filtro).getResultList();
    }

    @Override
    public ResultadoPaginado<Cliente> buscarPaginado(FiltroClienteDTO filtro) {
        EntityManager em = emProvider.get();

        // 1. Obtener el conteo total
        Long total = contarClientes(em, filtro);

        // 2. Obtener los resultados paginados
        TypedQuery<Cliente> query = crearQueryBusqueda(em, filtro);

        if (filtro.offset() != null && filtro.limit() != null) {
            query.setFirstResult(filtro.offset());
            query.setMaxResults(filtro.limit());
        }

        List<Cliente> resultados = query.getResultList();

        return new ResultadoPaginado<>(resultados, total);
    }

    @Override
    public List<ClientesMasIngresosDTO> reporteClientesMasIngresos(Integer cantidad, LocalDateTime fechaMin,
                                                                   LocalDateTime fechaMax) {
        EntityManager em = emProvider.get();

        // Usando query nativa porque JPQL no soporta UNION directamente
        Query query = em.createNativeQuery(
                        "SELECT " +
                                "    c.pk_cliente, " +
                                "    c.nombre, " +
                                "    c.apellido, " +
                                "    c.dni, " +
                                "    COALESCE(s.cant_services, 0), " +
                                "    COALESCE(v.cant_ventas, 0), " +
                                "    (COALESCE(s.cant_services, 0) + COALESCE(v.cant_ventas, 0)), " +
                                "    COALESCE(s.total_services, 0), " +
                                "    COALESCE(v.total_ventas, 0), " +
                                "    (COALESCE(s.total_services, 0) + COALESCE(v.total_ventas, 0)) " +
                                "FROM clientes c " +
                                "LEFT JOIN (" +
                                "    SELECT fk_cliente, COUNT(*) as cant_services, SUM(monto_total) as total_services " +
                                "    FROM services " +
                                "    WHERE fechacarga BETWEEN :fechaMin AND :fechaMax " +
                                "    AND estado_service = 'PAGADO' " +
                                "    GROUP BY fk_cliente" +
                                ") s ON c.pk_cliente = s.fk_cliente " +
                                "LEFT JOIN (" +
                                "    SELECT fk_cliente, COUNT(*) as cant_ventas, SUM(monto_total) as total_ventas " +
                                "    FROM ventas_repuestos " +
                                "    WHERE fecha BETWEEN :fechaMin AND :fechaMax " +
                                "    AND activo = true " +
                                "    AND estado_venta = 'PAGADO' " +
                                "    GROUP BY fk_cliente" +
                                ") v ON c.pk_cliente = v.fk_cliente " +
                                "WHERE (s.fk_cliente IS NOT NULL OR v.fk_cliente IS NOT NULL) " +
                                "ORDER BY (COALESCE(s.total_services, 0) + COALESCE(v.total_ventas, 0)) DESC")
                .setParameter("fechaMin", fechaMin)
                .setParameter("fechaMax", fechaMax)
                .setMaxResults(cantidad);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(row -> new ClientesMasIngresosDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        (String) row[3],
                        ((Number) row[4]).intValue(),
                        ((Number) row[5]).intValue(),
                        ((Number) row[6]).intValue(),
                        row[7] != null ? new BigDecimal(row[7].toString()) : BigDecimal.ZERO,
                        row[8] != null ? new BigDecimal(row[8].toString()) : BigDecimal.ZERO,
                        row[9] != null ? new BigDecimal(row[9].toString()) : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());
    }

    /**
     * Cuenta el total de clientes que coinciden con el filtro
     */
    private Long contarClientes(EntityManager em, FiltroClienteDTO filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Cliente> root = countQuery.from(Cliente.class);

        List<Predicate> predicates = construirPredicados(cb, root, filtro);

        countQuery.select(cb.count(root));
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }

    /**
     * Crea la query de búsqueda con todos los filtros y ordenamiento
     */
    private TypedQuery<Cliente> crearQueryBusqueda(EntityManager em, FiltroClienteDTO filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Cliente> query = cb.createQuery(Cliente.class);
        Root<Cliente> root = query.from(Cliente.class);

        List<Predicate> predicates = construirPredicados(cb, root, filtro);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.asc(root.get("apellido")));

        return em.createQuery(query);
    }

    /**
     * Construye los predicados (condiciones WHERE) basándose en el filtro
     */
    private List<Predicate> construirPredicados(CriteriaBuilder cb, Root<Cliente> root,
                                                FiltroClienteDTO filtro) {
        List<Predicate> filtros = new ArrayList<>();

        if (filtro.activos() && !filtro.baja()) {
            filtros.add(cb.equal(root.get("activo"), Boolean.TRUE));
        } else if (!filtro.activos() && filtro.baja()) {
            filtros.add(cb.equal(root.get("activo"), Boolean.FALSE));
        }

        if (filtro.dni() != null && !filtro.dni().isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("dni")),
                    "%" + filtro.dni().toLowerCase() + "%"));
        }
        if (filtro.apellido() != null && !filtro.apellido().isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("apellido")),
                    "%" + filtro.apellido().toLowerCase(Locale.ROOT) + "%"));
        }
        if (filtro.nombre() != null && !filtro.nombre().isBlank()) {
            filtros.add(cb.like(cb.lower(root.get("nombre")),
                    "%" + filtro.nombre().toLowerCase(Locale.ROOT) + "%"));
        }

        return filtros;
    }
}