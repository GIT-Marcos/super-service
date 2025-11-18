package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.StockServ;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import java.util.List;

public class ServiceServImpl implements ServiceServ {

    private final ServiceDAO daoService;
    private final StockServ stockServ;

    @Inject
    public ServiceServImpl(ServiceDAO daoService, StockServ stockServ) {
        this.daoService = daoService;
        this.stockServ = stockServ;
    }


    @Transactional
    @Override
    public List<Service> verTodos() {
        return daoService.verTodos();
    }

    @Transactional
    @Override
    public List<Service> buscarConFiltros(FiltroServiceDTO filtros) {
        return daoService.buscarConFiltros(filtros);
    }

    @Transactional
    @Override
    public Service cargarService(Service s) {
        if (s.getOrden().getNotaRetiro() != null) {
            for (DetalleRetiro d : s.getOrden().getNotaRetiro().getDetallesRetiroList()) {
                stockServ.quitarExistente(d.getRepuesto().getStock(), d.getCantidadRetirada());
            }
        }

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
