package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.UbicacionDAO;
import SPRService.SPRService.entities.Ubicacion;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;

import java.util.List;

public class UbicacionDAOImpl extends GenericDAOImpl<Ubicacion, Long> implements UbicacionDAO {

    @Inject
    private Provider<EntityManager> emProvider;

    public UbicacionDAOImpl() {
        super(Ubicacion.class);
    }

    @Override
    public List<Ubicacion> verTodas() {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT u FROM Ubicacion u " +
                                "ORDER BY u.ubicacion ASC",
                        Ubicacion.class)
                .getResultList();
    }

    @Override
    public List<Ubicacion> validarUnicidadNombre(Ubicacion u) {
        EntityManager em = emProvider.get();
        return em.createQuery("SELECT u FROM Ubicacion u " +
                                "WHERE u.ubicacion = :nombre",
                        Ubicacion.class)
                .setParameter("nombre", u.getUbicacion())
                .setMaxResults(1)
                .getResultList();
    }
}
