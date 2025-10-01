package SPRService.SPRService.services;

import SPRService.SPRService.entities.MarcaVehiculo;

import java.util.List;

public interface MarcaVehiculoServ {

    List<MarcaVehiculo> verTodas();

    void cargarMarca(MarcaVehiculo m);
}
