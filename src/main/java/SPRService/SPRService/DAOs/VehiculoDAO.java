package SPRService.SPRService.DAOs;

import SPRService.SPRService.DTOs.ModelosMasRegistradosDTO;
import SPRService.SPRService.entities.Vehiculo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehiculoDAO extends GenericDAO<Vehiculo, Long>{

    List<Vehiculo> verTodos();
    Optional<Vehiculo> verDetalle(Long id);

    List<Vehiculo> buscarPor(String patente, String modelo, String marca);

    //todo: ver si esto va en el servicio de modelos.
    List<ModelosMasRegistradosDTO> reporteModelosMasRegistrados(Integer cantidad, LocalDate fechaMin,
                                                                LocalDate fechaMax);

    /**
     * Usar esto parece redundante, en su lugar usa "update" del GenericDAO.
     */
    void borradoLogico(Vehiculo vehiculo);
}
