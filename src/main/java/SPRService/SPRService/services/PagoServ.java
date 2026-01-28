package SPRService.SPRService.services;

import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.Transaccion;

public interface PagoServ {

    Transaccion agregarPagoTransaccion(Pago p, Transaccion t);

}
