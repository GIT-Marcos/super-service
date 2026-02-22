package SPRService.SPRService.services;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehiculoServ {

    List<Vehiculo> verTodosActivos();
    Optional<Vehiculo> verDetalle(Long id);
    List<Vehiculo> buscarPor(String patente, String modelo, String marca, boolean activos, boolean baja);
    ResultadoPaginado<Vehiculo> buscarPaginado(String patente, String modelo, String marca,
                                               boolean activos, boolean baja, int pagina, int itemsPorPagina);
    ResultadoPaginado<Vehiculo> buscarParaExportar(String patente, String modelo, String marca,
                                      boolean activos, boolean baja);
    List<ModelosMasRegistradosDTO> generarReporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                       LocalDate fechaMax);

    Vehiculo cargarVehiculo(Vehiculo vehiculoDTO);
    Vehiculo modificarVehiculo(Vehiculo vehiculo);
    void darDeBaja(Vehiculo vehiculo);
    void reactivar(Vehiculo v);
}
