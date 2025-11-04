package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Service;

import java.util.Set;

public interface ServiceDAO extends GenericDAO<Service, Long> {

    Set<Service> verTodos();

}
