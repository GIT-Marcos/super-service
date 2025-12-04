package SPRService.SPRService.services;

import SPRService.SPRService.entities.Ubicacion;

import java.util.List;
import java.util.Optional;

public interface UbicacionServ {

    List<Ubicacion> verTodas();

    Optional<Ubicacion> cargarNueva(Ubicacion u);

    Optional<Ubicacion> modificar(Ubicacion u);

}
