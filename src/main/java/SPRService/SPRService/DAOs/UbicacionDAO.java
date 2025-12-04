package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.Ubicacion;

import java.util.List;

public interface UbicacionDAO extends GenericDAO<Ubicacion, Long> {

    List<Ubicacion> verTodas();

    List<Ubicacion> validarUnicidadNombre(Ubicacion u);
}
