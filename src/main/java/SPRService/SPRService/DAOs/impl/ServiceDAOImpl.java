package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.entities.Service;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceDAOImpl extends GenericDAOImpl<Service, Long> implements ServiceDAO {

    @Inject
    Provider<EntityManager> emProvider;

    public ServiceDAOImpl() {
        super(Service.class);
    }


    @Override
    public Set<Service> verTodos() {
        EntityManager em = emProvider.get();
        List<Service> services = em.createQuery("SELECT DISTINCT s FROM Service s " +
                                "JOIN FETCH s.orden o " +
                                "JOIN FETCH o.trabajos " +
                                "ORDER BY s.fechaCarga ASC",
                        Service.class)
                .getResultList();
        return new HashSet<>(services);
    }
}
