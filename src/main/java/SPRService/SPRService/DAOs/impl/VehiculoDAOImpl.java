package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.VehiculoDAO;
import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.*;
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
        return buscarPor("", "", "");
    }

    @Override
    public Optional<Vehiculo> verDetalle(Long id) {
        EntityManager em = emProvider.get();

        // ORDENES
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
            // TRABAJOS
            em.createQuery("select o from Orden o " +
                                    "left join fetch o.trabajos " +
                                    "where o.vehiculo.id = :id",
                            Orden.class)
                    .setParameter("id", id)
                    .getResultList();

            // DETALLES NOTA
            em.createQuery("select o from Orden o " +
                                    "left join fetch o.notaRetiro n " +
                                    "left join fetch n.detalleRetiro d " +
                                    "left join fetch d.repuesto " +
                                    "where o.vehiculo.id = :id",
                            Orden.class)
                    .setParameter("id", id)
                    .getResultList();
        }

        // CLIENTES DE VEHÍCULO
        // todo: no funciona
//        em.createQuery("select v from Vehiculo v " +
//                                "left join fetch v.clientes " +
//                                "where v.id = :id",
//                        Vehiculo.class)
//                .setParameter("id", id);
        return result;
    }

    @Override
    public List<Vehiculo> buscarPor(String patente, String modelo, String marca) {
        EntityManager em = emProvider.get();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Vehiculo> query = cb.createQuery(Vehiculo.class);
        Root<Vehiculo> root = query.from(Vehiculo.class);

        Fetch<Vehiculo, ModeloVehiculo> fetchModelo = root.fetch("modeloVehiculo", JoinType.LEFT);
        fetchModelo.fetch("marcaVehiculo", JoinType.LEFT);

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
}
