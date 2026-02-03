package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.DTOs.filtros.FiltroVehiculoDTO;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.util.ResultadoPaginado;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehiculoDAO extends GenericDAO<Vehiculo, Long>{

    /**
     * Trae todos los vehículos solo con los datos mínimos para llenar la tabla principal.
     */
    List<Vehiculo> verTodos();

    /**
     * Trae órdenes, trabajos, detalles de retiro de un vehículo.
     */
    Optional<Vehiculo> verDetalle(Long id);

    List<Vehiculo> buscarPor(String patente, String modelo, String marca);

    ResultadoPaginado<Vehiculo> buscarPaginado(FiltroVehiculoDTO filtro);

    //todo: ver si esto va en el servicio de modelos.
    List<ModelosMasRegistradosDTO> reporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                LocalDate fechaMax);
}
