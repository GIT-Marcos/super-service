package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.NotaRetiroDAO;
import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class NotaRetiroDAOImpl extends GenericDAOImpl<NotaRetiro, Long> implements NotaRetiroDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public NotaRetiroDAOImpl() {
        super(NotaRetiro.class);
    }

    @Override
    public ResultadoPaginado<NotaRetiro> buscarPaginado(FiltroNotaRetiro filtros, int pagina, int tamanioPagina) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // --- 1. CONSULTA PARA OBTENER EL TOTAL DE RESULTADOS (CONTEO) ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<NotaRetiro> countRoot = countQuery.from(NotaRetiro.class);
        countQuery.select(cb.count(countRoot));

        // Generamos los predicados basándonos en el DTO
        Predicate[] countPredicates = crearPredicados(cb, countRoot, filtros);
        if (countPredicates.length > 0) {
            countQuery.where(countPredicates);
        }

        Long totalResultados = em.createQuery(countQuery).getSingleResult();

        // Optimización: si no hay resultados, no ejecutamos la consulta de datos
        if (totalResultados == 0) {
            return new ResultadoPaginado<>(Collections.emptyList(), 0L);
        }

        // --- 2. CONSULTA PARA OBTENER LOS DATOS DE LA PÁGINA ACTUAL ---
        CriteriaQuery<NotaRetiro> dataQuery = cb.createQuery(NotaRetiro.class);
        Root<NotaRetiro> dataRoot = dataQuery.from(NotaRetiro.class);
        dataQuery.select(dataRoot);

        // Reutilizamos la lógica de predicados
        Predicate[] dataPredicates = crearPredicados(cb, dataRoot, filtros);
        if (dataPredicates.length > 0) {
            dataQuery.where(dataPredicates);
        }

        // Ordenamos por Fecha descendente (y por ID para desempatar y mantener consistencia en paginación)
        dataQuery.orderBy(
                cb.desc(dataRoot.get("fecha")),
                cb.desc(dataRoot.get("id"))
        );

        // Ejecutamos la consulta paginada
        TypedQuery<NotaRetiro> typedQuery = em.createQuery(dataQuery);
        typedQuery.setFirstResult(pagina * tamanioPagina); // Offset
        typedQuery.setMaxResults(tamanioPagina);         // Limit

        List<NotaRetiro> notas = typedQuery.getResultList();

        // --- 3. DEVOLVER EL RESULTADO COMPLETO ---
        return new ResultadoPaginado<>(notas, totalResultados);
    }

    @Override
    public Optional<NotaRetiro> verDetalles(Long id) {
        EntityManager em = emProvider.get();
        return em.createQuery("select n from NotaRetiro n " +
                                "left join fetch n.detalleRetiroList d " +
                                "left join fetch d.repuesto r " +
                                "left join fetch r.marcaRepuesto " +
                                "left join fetch r.stock s " +
                                "left join fetch s.ubicacion " +
                                "where n.id = :id",
                        NotaRetiro.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }

    /**
     * Crea los filtros SQL basados en el DTO FiltroNotaRetiro.
     */
    private Predicate[] crearPredicados(CriteriaBuilder cb, Root<NotaRetiro> root, FiltroNotaRetiro filtros) {
        List<Predicate> predicates = new ArrayList<>();

        if (filtros != null) {
            // 1. Filtro por Rango de Fechas
            if (filtros.fechaMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), filtros.fechaMin()));
            }
            if (filtros.fechaMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fecha"), filtros.fechaMax()));
            }

            // 2. Filtro por Estado (Activo/Inactivo)
            if (filtros.activo() != null) {
                predicates.add(cb.equal(root.get("activo"), filtros.activo()));
            }

            // 3. Filtro por Tipos de Uso (Set IN clause)
            if (filtros.tipoDeUsos() != null && !filtros.tipoDeUsos().isEmpty()) {
                // Esto genera SQL: WHERE tipo_uso IN ('VENTA', 'SERVICE', ...)
                predicates.add(root.get("tipoUso").in(filtros.tipoDeUsos()));
            }
        }

        return predicates.toArray(new Predicate[0]);
    }
}