package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.Vehiculo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehiculoServ {

    List<Vehiculo> verTodosActivos();
    Optional<Vehiculo> verDetalle(Long id);
    List<Vehiculo> buscarPor(String patente, String modelo, String marca);
    List<ModelosMasRegistradosDTO> generarReporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                       LocalDate fechaMax);

    Vehiculo cargarVehiculo(Vehiculo vehiculoDTO);
    Vehiculo modificarVehiculo(Vehiculo vehiculo);
    void borradoLogico(Vehiculo vehiculo);

}
