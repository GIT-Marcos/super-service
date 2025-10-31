package SPRService.SPRService.services;

import SPRService.SPRService.entities.Service;

import java.util.List;

public interface ServiceServ {

    List<Service> verTodos();

    List<Service> buscarConFiltros();

    Service cargarService(Service s);

    Service modificarService(Service s);

    void borrarService(Service s);
}
