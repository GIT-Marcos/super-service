package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.MarcaVehiculoDAO;
import SPRService.SPRService.entities.MarcaVehiculo;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;

import java.util.List;

@Singleton
public class MarcaVehiculoDAOImpl extends GenericDAOImpl<MarcaVehiculo, Long> implements MarcaVehiculoDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public MarcaVehiculoDAOImpl() {
        super(MarcaVehiculo.class);
    }

    @Override
    public List<MarcaVehiculo> getAllAlphabetically() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT DISTINCT m FROM MarcaVehiculo m " +
                        "LEFT JOIN FETCH m.modelosVehiculos " +
                        "ORDER BY m.nombreMarca ASC",
                MarcaVehiculo.class).getResultList();
    }
}
