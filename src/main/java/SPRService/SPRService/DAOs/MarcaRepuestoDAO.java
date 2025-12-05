package SPRService.SPRService.DAOs;

import SPRService.SPRService.entities.MarcaRepuesto;

import java.util.List;

public interface MarcaRepuestoDAO extends GenericDAO<MarcaRepuesto, Long> {

    List<MarcaRepuesto> verTodas();

    List<MarcaRepuesto> validarUnicidadNombre(MarcaRepuesto m);
}
