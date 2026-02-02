package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.ModeloVehiculo;

import java.util.List;
import java.util.Optional;

public interface ModeloVehiculoDAO extends GenericDAO<ModeloVehiculo, Long>{
    List<ModeloVehiculo> traerModelosConVehiculos();
    Optional<ModeloVehiculo> traerVehiculosDeModelo(Long id);
}
