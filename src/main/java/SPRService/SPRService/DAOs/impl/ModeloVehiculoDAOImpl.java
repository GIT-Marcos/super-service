package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ModeloVehiculoDAO;
import SPRService.SPRService.entities.ModeloVehiculo;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@Singleton
public class ModeloVehiculoDAOImpl extends GenericDAOImpl<ModeloVehiculo, Long> implements ModeloVehiculoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public ModeloVehiculoDAOImpl() {
        super(ModeloVehiculo.class);
    }

    @Override
    public List<ModeloVehiculo> traerModelosConVehiculos() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT m FROM ModeloVehiculo m " +
                        "LEFT JOIN FETCH m.marcaVehiculo " +
                        "LEFT JOIN FETCH m.vehiculos " +
                        "ORDER BY m.nombreModelo ASC",
                ModeloVehiculo.class).getResultList();
    }

    @Override
    public Optional<ModeloVehiculo> traerVehiculosDeModelo(Long id) {
        EntityManager em = emProvider.get();
        return em.createQuery("select m from ModeloVehiculo m " +
                                "left join fetch m.marcaVehiculo " +
                                "left join fetch m.vehiculos " +
                                "where m.id = :id",
                ModeloVehiculo.class)
                .setParameter("id", id)
                .getResultStream().findFirst();
    }
}
