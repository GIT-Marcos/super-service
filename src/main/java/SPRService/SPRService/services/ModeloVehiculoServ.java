package SPRService.SPRService.services;

import SPRService.SPRService.entities.ModeloVehiculo;

import java.util.List;

public interface ModeloVehiculoServ {

    List<ModeloVehiculo> verTodos();

    void cargarModelo(ModeloVehiculo m);

    ModeloVehiculo modificarModelo(ModeloVehiculo m);
}
