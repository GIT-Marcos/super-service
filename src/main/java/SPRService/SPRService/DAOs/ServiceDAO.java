package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Service;

import java.util.List;
import java.util.Set;

public interface ServiceDAO extends GenericDAO<Service, Long> {

    List<Service> verTodos();

}
