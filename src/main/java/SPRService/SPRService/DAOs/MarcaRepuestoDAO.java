package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.MarcaRepuesto;

import java.util.List;
import java.util.Set;

public interface MarcaRepuestoDAO extends GenericDAO<MarcaRepuesto, Long> {

    Set<MarcaRepuesto> verTodas();

    List<MarcaRepuesto> validarUnicidadNombre(MarcaRepuesto m);
}
