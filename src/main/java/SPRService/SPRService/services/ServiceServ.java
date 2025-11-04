package SPRService.SPRService.services;

import SPRService.SPRService.entities.Service;

import java.util.List;
import java.util.Set;

public interface ServiceServ {

    Set<Service> verTodos();

    List<Service> buscarConFiltros();

    Service cargarService(Service s);

    Service modificarService(Service s);

    void borrarService(Service s);
}
