package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.PagoDAO;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Transaccion;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.services.PagoServ;
import SPRService.SPRService.services.VentaRepuestoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class PagoServImpl implements PagoServ {

    private final PagoDAO daoPago;
    private final VentaRepuestoServ servVenta;

    @Inject
    public PagoServImpl(PagoDAO daoPago, VentaRepuestoServ servVenta) {
        this.daoPago = daoPago;
        this.servVenta = servVenta;
    }

    @Transactional
    @Override
    public Transaccion agregarPagoTransaccion(Pago p, Transaccion t) {
        daoPago.save(p);
        if (t instanceof VentaRepuesto) {
            t.asociarPago(p);
            servVenta.modificarVenta((VentaRepuesto) t);
        } else if (t instanceof Service) {

        }
        return t;
    }
}
