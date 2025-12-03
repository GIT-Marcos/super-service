package SPRService.SPRService.services;

import SPRService.SPRService.entities.MarcaRepuesto;

import java.util.Optional;
import java.util.Set;

public interface MarcaRepuestoServ {

    Set<MarcaRepuesto> verTodas();

    Optional<MarcaRepuesto> cargarMarca(MarcaRepuesto m);

}
