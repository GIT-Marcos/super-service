package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.VehiculoDAO;
import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVehiculoDTO;
import SPRService.SPRService.entities.*;
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
import java.util.Optional;

@Singleton
public class VehiculoDAOImpl extends GenericDAOImpl<Vehiculo, Long> implements VehiculoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public VehiculoDAOImpl() {
        super(Vehiculo.class);
    }

    @Override
    public List<Vehiculo> verTodos() {
        return buscarPor("", "", "", true, true);
    }

    @Override
    public Optional<Vehiculo> verDetalle(Long id) {
        EntityManager em = emProvider.get();

        Optional<Vehiculo> result = em.createQuery("select v from Vehiculo v " +
                                "left join fetch v.ordenes o " +
                                "left join fetch o.estadoIngreso " +
                                "left join fetch o.service s " +
                                "left join fetch s.cliente c " +
                                "left join fetch c.contactosCliente " +
                                "join fetch v.modeloVehiculo m " +
                                "join fetch m.marcaVehiculo " +
                                "where v.id = :id",
                        Vehiculo.class)
                .setParameter("id", id)
                .getResultStream().findAny();

        if (result.isPresent()) {
            em.createQuery("select o from Orden o " +
                                    "left join fetch o.trabajos " +
                                    "where o.vehiculo.id = :id",
                            Orden.class)
                    .setParameter("id", id)
                    .getResultList();

            em.createQuery("select o from Orden o " +
                                    "left join fetch o.notaRetiro n " +
                                    "left join fetch n.detalleRetiro d " +
                                    "left join fetch d.repuesto " +
                                    "where o.vehiculo.id = :id",
                            Orden.class)
                    .setParameter("id", id)
                    .getResultList();
        }

        return result;
    }

    @Override
    public List<Vehiculo> buscarPor(String patente, String modelo, String marca, boolean activos, boolean baja) {
        FiltroVehiculoDTO filtro = new FiltroVehiculoDTO(patente, modelo, marca, activos, baja);
        EntityManager em = emProvider.get();
        return crearQueryBusqueda(em, filtro).getResultList();
    }

    @Override
    public ResultadoPaginado<Vehiculo> buscarPaginado(FiltroVehiculoDTO filtro) {
        EntityManager em = emProvider.get();

        // 1. Obtener el conteo total
        Long total = contarVehiculos(em, filtro);

        // 2. Obtener los resultados paginados
        TypedQuery<Vehiculo> query = crearQueryBusqueda(em, filtro);

        if (filtro.offset() != null && filtro.limit() != null) {
            query.setFirstResult(filtro.offset());
            query.setMaxResults(filtro.limit());
        }

        List<Vehiculo> resultados = query.getResultList();

        return new ResultadoPaginado<>(resultados, total);
    }

    /**
     * Cuenta el total de vehículos que coinciden con el filtro
     */
    private Long contarVehiculos(EntityManager em, FiltroVehiculoDTO filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Vehiculo> root = countQuery.from(Vehiculo.class);

        Join<Vehiculo, ModeloVehiculo> joinModelo = root.join("modeloVehiculo", JoinType.LEFT);
        Join<ModeloVehiculo, MarcaVehiculo> joinMarca = joinModelo.join("marcaVehiculo", JoinType.LEFT);

        List<Predicate> predicates = construirPredicados(cb, root, joinModelo, joinMarca, filtro);

        countQuery.select(cb.count(root));
        countQuery.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }

    /**
     * Crea la query de búsqueda con todos los filtros y ordenamiento
     */
    private TypedQuery<Vehiculo> crearQueryBusqueda(EntityManager em, FiltroVehiculoDTO filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehiculo> query = cb.createQuery(Vehiculo.class);
        Root<Vehiculo> root = query.from(Vehiculo.class);

        // Fetches para evitar N+1
        Fetch<Vehiculo, ModeloVehiculo> fetchModelo = root.fetch("modeloVehiculo", JoinType.LEFT);
        fetchModelo.fetch("marcaVehiculo", JoinType.LEFT);

        // Joins para filtros
        Join<Vehiculo, ModeloVehiculo> joinModelo = root.join("modeloVehiculo", JoinType.LEFT);
        Join<ModeloVehiculo, MarcaVehiculo> joinMarca = joinModelo.join("marcaVehiculo", JoinType.LEFT);

        List<Predicate> predicates = construirPredicados(cb, root, joinModelo, joinMarca, filtro);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.desc(root.get("fechaRegistro")));

        return em.createQuery(query);
    }

    /**
     * Construye los predicados (condiciones WHERE) basándose en el filtro
     */
    private List<Predicate> construirPredicados(CriteriaBuilder cb, Root<Vehiculo> root,
                                                Join<Vehiculo, ModeloVehiculo> joinModelo,
                                                Join<ModeloVehiculo, MarcaVehiculo> joinMarca,
                                                FiltroVehiculoDTO filtro) {
        List<Predicate> filtros = new ArrayList<>();

        if (filtro.activos() && !filtro.baja()) {
            filtros.add(cb.equal(root.get("estado"), Boolean.TRUE));
        } else if (!filtro.activos() && filtro.baja()) {
            filtros.add(cb.equal(root.get("estado"), Boolean.FALSE));
        }

        if (filtro.patente() != null && !filtro.patente().isBlank()) {
            filtros.add(cb.like(cb.upper(root.get("patente")),
                    "%" + filtro.patente().toUpperCase(Locale.ROOT) + "%"));
        }
        if (filtro.modelo() != null && !filtro.modelo().isBlank()) {
            filtros.add(cb.like(cb.lower(joinModelo.get("nombreModelo")),
                    "%" + filtro.modelo().toLowerCase() + "%"));
        }
        if (filtro.marca() != null && !filtro.marca().isBlank()) {
            filtros.add(cb.like(cb.lower(joinMarca.get("nombreMarca")),
                    "%" + filtro.marca().toLowerCase() + "%"));
        }

        return filtros;
    }

    @Override
    public List<ModelosMasRegistradosDTO> reporteModelosMasRegistrados(Integer cantidad, LocalDateTime fechaMin,
                                                                       LocalDateTime fechaMax) {
        EntityManager em = emProvider.get();
        TypedQuery<ModelosMasRegistradosDTO> query = em.createQuery(
                        "SELECT new SPRService.SPRService.DTOs.ModelosMasRegistradosDTO(" +
                                "    m.nombreModelo, " +
                                "    CAST(m.anio AS string), " +
                                "    CAST(m.cilindrada AS string), " +
                                "    ma.nombreMarca, " +
                                "    ma.rutaLogo, " +
                                "    COUNT(v.id)" +
                                ") " +
                                "FROM Vehiculo v " +
                                "JOIN v.modeloVehiculo m " +
                                "JOIN m.marcaVehiculo ma " +
                                "WHERE v.fechaRegistro BETWEEN :fechaMin AND :fechaMax " +
                                "GROUP BY m.nombreModelo, m.anio, m.cilindrada, ma.nombreMarca, ma.rutaLogo " +
                                "ORDER BY COUNT(v.id) DESC",
                        ModelosMasRegistradosDTO.class)
                .setParameter("fechaMin", fechaMin)
                .setParameter("fechaMax", fechaMax)
                .setMaxResults(cantidad);
        return query.getResultList();
    }
}