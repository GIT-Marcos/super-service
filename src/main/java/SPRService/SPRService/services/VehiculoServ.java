package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.Vehiculo;

import java.time.LocalDate;
import java.util.List;

public interface VehiculoServ {

    List<Vehiculo> verTodosActivos();
    List<Vehiculo> buscarPor(String patente, String modelo, String marca);
    List<ModelosMasRegistradosDTO> generarReporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                       LocalDate fechaMax);

    Vehiculo cargarVehiculo(Vehiculo vehiculo);
    Vehiculo modificarVehiculo(Vehiculo vehiculo);
    void borradoLogico(Vehiculo vehiculo);

}
