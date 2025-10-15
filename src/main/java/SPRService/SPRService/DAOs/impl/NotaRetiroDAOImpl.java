package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.NotaRetiroDAO;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class NotaRetiroDAOImpl extends GenericDAOImpl<NotaRetiro, Long> implements NotaRetiroDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public NotaRetiroDAOImpl() {
        super(NotaRetiro.class);
    }

    @Override
    public List<NotaRetiro> verTodasPorFecha() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT n FROM NotaRetiro n " +
                        "WHERE n.activo = TRUE " +
                        "ORDER BY n.fecha DESC",
                NotaRetiro.class).getResultList();
    }

    @Override
    public ResultadoPaginado<NotaRetiro> buscarPaginado(LocalDate fechaMin, LocalDate fechaMax,
                                                        int pagina, int tamanioPagina) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // --- 1. CONSULTA PARA OBTENER EL TOTAL DE RESULTADOS (CONTEO) ---
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<NotaRetiro> countRoot = countQuery.from(NotaRetiro.class);
        countQuery.select(cb.count(countRoot));

        // Aplicamos los mismos predicados de filtro a la consulta de conteo
        Predicate[] countPredicates = crearPredicados(cb, countRoot, fechaMin, fechaMax);
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

        // Aplicamos los predicados de filtro a la consulta de datos
        Predicate[] dataPredicates = crearPredicados(cb, dataRoot, fechaMin, fechaMax);
        if (dataPredicates.length > 0) {
            dataQuery.where(dataPredicates);
        }

        // Ordenamos los resultados (lo más común es por fecha descendente)
        dataQuery.orderBy(cb.desc(dataRoot.get("fecha"))); // <-- Asegúrate que tu entidad NotaRetiro tiene un campo llamado "fecha"

        // Creamos la consulta final y aplicamos la paginación
        TypedQuery<NotaRetiro> typedQuery = em.createQuery(dataQuery);
        typedQuery.setFirstResult(pagina * tamanioPagina); // Offset (desde dónde empezar)
        typedQuery.setMaxResults(tamanioPagina);         // Limit (cuántos traer)

        List<NotaRetiro> notas = typedQuery.getResultList();

        // --- 3. DEVOLVER EL RESULTADO COMPLETO ---
        return new ResultadoPaginado<>(notas, totalResultados);
    }

    @Override
    public List<NotaRetiro> buscarPorFecha(LocalDate fechaMin, LocalDate fechaMax) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT n FROM NotaRetiro n " +
                                "WHERE n.fecha BETWEEN :fechaMin AND :fechaMax",
                        NotaRetiro.class)
                .setParameter("fechaMin", fechaMin)
                .setParameter("fechaMax", fechaMax)
                .getResultList();
    }

    /**
     * Auxiliar para crear un array de predicados (filtros) basados en las fechas.
     * Esto evita duplicar código y previene errores al usar la API de Criteria.
     */
    private Predicate[] crearPredicados(CriteriaBuilder cb, Root<NotaRetiro> root,
                                        LocalDate fechaMin, LocalDate fechaMax) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("activo"), true));
        if (fechaMin != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), fechaMin));
        }
        if (fechaMax != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("fecha"), fechaMax));
        }
        return predicates.toArray(new Predicate[0]);
    }
}
