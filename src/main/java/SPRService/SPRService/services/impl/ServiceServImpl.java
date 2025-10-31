package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.services.ServiceServ;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.util.List;


public class ServiceServImpl implements ServiceServ {

    private final ServiceDAO daoService;

    @Inject
    public ServiceServImpl(ServiceDAO daoService) {
        this.daoService = daoService;
    }


    @Transactional
    @Override
    public List<Service> verTodos() {
        return List.of();
    }

    @Transactional
    @Override
    public List<Service> buscarConFiltros() {
        return List.of();
    }

    @Transactional
    @Override
    public Service cargarService(Service s) {
        daoService.save(s);
        return s;
    }

    @Transactional
    @Override
    public Service modificarService(Service s) {
        return null;
    }

    @Transactional
    @Override
    public void borrarService(Service s) {

    }
}
