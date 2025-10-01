package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.Vehiculo;

import java.time.LocalDate;
import java.util.List;

public interface VehiculoDAO extends GenericDAO<Vehiculo, Long>{

    List<Vehiculo> getAllActive();

    List<Vehiculo> buscarPor(String patente, String modelo, String marca);

    /**
     * Consulta si existen vehículos con la misma patente que el que se intenta persistir.
     * @return NULL si no existe ninguno; TRUE si existe uno ACTIVO; FALSE si existe uno peo INACTIVO.
     */
    List<Boolean> consultarEstado(String patente);

    //todo: ver si esto va en el servicio de modelos.
    List<ModelosMasRegistradosDTO> reporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                LocalDate fechaMax);

    /**
     * Usar esto parece redundante, en su lugar usa "update" del GenericDAO.
     */
    void borradoLogico(Vehiculo vehiculo);
}
