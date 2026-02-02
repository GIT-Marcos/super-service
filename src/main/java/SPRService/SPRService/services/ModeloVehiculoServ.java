package SPRService.SPRService.services;

import SPRService.SPRService.entities.ModeloVehiculo;

import java.util.List;
import java.util.Optional;

public interface ModeloVehiculoServ {

    List<ModeloVehiculo> verTodos();

    Optional<ModeloVehiculo> verVehiculosDeModelo(Long id);

    void cargarModelo(ModeloVehiculo m);

    ModeloVehiculo modificarModelo(ModeloVehiculo m);
}
