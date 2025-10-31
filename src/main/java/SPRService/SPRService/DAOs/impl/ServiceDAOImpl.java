package SPRService.SPRService.DAOs.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.entities.Service;
import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.persistence.EntityManager;

public class ServiceDAOImpl extends GenericDAOImpl<Service, Long> implements ServiceDAO {

    @Inject
    Provider<EntityManager> emProvider;

    public ServiceDAOImpl() {
        super(Service.class);
    }


}
