package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ModeloVehiculoDAO;
import SPRService.SPRService.entities.ModeloVehiculo;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.util.List;

@Singleton
public class ModeloVehiculoDAOImpl extends GenericDAOImpl<ModeloVehiculo, Long> implements ModeloVehiculoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public ModeloVehiculoDAOImpl() {
        super(ModeloVehiculo.class);
    }

    @Override
    public List<ModeloVehiculo> getAllModels() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT m FROM ModeloVehiculo m " +
                        "ORDER BY m.nombreModelo ASC",
                ModeloVehiculo.class).getResultList();
    }
}
