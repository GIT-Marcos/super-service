package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.VehiculoDAO;
import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.MarcaVehiculo;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.entities.Vehiculo;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Singleton
public class VehiculoDAOImpl extends GenericDAOImpl<Vehiculo, Long> implements VehiculoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public VehiculoDAOImpl() {
        super(Vehiculo.class);
    }

    @Override
    public List<Vehiculo> getAllActive() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT v FROM Vehiculo v " +
                        "WHERE v.estado = TRUE",
                Vehiculo.class).getResultList();
    }

    @Override
    public List<Vehiculo> buscarPor(String patente, String modelo, String marca) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehiculo> query = cb.createQuery(Vehiculo.class);
        Root<Vehiculo> root = query.from(Vehiculo.class);
        Join<Vehiculo, ModeloVehiculo> joinModelo = root.join("modeloVehiculo", JoinType.LEFT);
        Join<ModeloVehiculo, MarcaVehiculo> joinMarca = joinModelo.join("marcaVehiculo", JoinType.LEFT);
        List<Predicate> filtros = new ArrayList<>();
        filtros.add(cb.equal(root.get("estado"), Boolean.TRUE));

        if (!patente.isBlank())
            filtros.add(cb.like(cb.upper(root.get("patente")), "%" + patente.toUpperCase(Locale.ROOT) + "%"));
        if (!modelo.isBlank())
            filtros.add(cb.like(cb.lower(joinModelo.get("nombreModelo")), "%" + modelo + "%"));
        if (!marca.isBlank())
            filtros.add(cb.like(cb.lower(joinMarca.get("nombreMarca")), "%" + marca + "%"));

        query.where(cb.and(filtros.toArray(filtros.toArray(new Predicate[0]))));
        query.orderBy(cb.asc(joinModelo.get("nombreModelo")));
        return em.createQuery(query).getResultList();
    }

    @Override
    public List<Boolean> consultarEstado(String patente) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT v.estado FROM Vehiculo v " +
                                "WHERE v.patente = :patente",
                        Boolean.class)
                .setParameter("patente", patente)
                .setMaxResults(1)
                .getResultList();
    }

    @Override
    public List<ModelosMasRegistradosDTO> reporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                       LocalDate fechaMax) {
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

    @Override
    public void borradoLogico(Vehiculo vehiculo) {
        EntityManager em = emProvider.get();
        em.merge(vehiculo);
    }
}
