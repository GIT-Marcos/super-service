package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.ModeloVehiculo;
import org.hibernate.Session;

import java.util.List;

public interface ModeloVehiculoDAO extends GenericDAO<ModeloVehiculo, Long>{
    List<ModeloVehiculo> getAllModels();
}
