package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.MarcaVehiculo;
import org.hibernate.Session;

import java.util.List;

public interface MarcaVehiculoDAO extends GenericDAO<MarcaVehiculo, Long>{

    List<MarcaVehiculo> getAllAlphabetically();

}
