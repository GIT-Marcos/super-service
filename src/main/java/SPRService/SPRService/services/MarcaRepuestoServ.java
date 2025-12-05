package SPRService.SPRService.services;

import SPRService.SPRService.entities.MarcaRepuesto;

import java.util.List;
import java.util.Optional;

public interface MarcaRepuestoServ {

    List<MarcaRepuesto> verTodas();

    Optional<MarcaRepuesto> cargarMarca(MarcaRepuesto m);

}
