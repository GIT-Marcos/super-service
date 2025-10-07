package SPRService.SPRService.services;

import SPRService.SPRService.entities.MarcaRepuesto;

import java.util.Set;

public interface MarcaRepuestoServ {

    Set<MarcaRepuesto> verTodas();

    MarcaRepuesto cargarMarca(MarcaRepuesto m);

}
