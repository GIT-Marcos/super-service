package SPRService.SPRService.services.impl;

import SPRService.SPRService.DAOs.PagoDAO;
import SPRService.SPRService.DAOs.ServiceDAO;
import SPRService.SPRService.DAOs.VentaRepuestoDAO;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Transaccion;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.services.PagoServ;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

import java.util.Optional;

@Singleton
public class PagoServImpl implements PagoServ {

    private final PagoDAO daoPago;
    private final ServiceDAO daoService;
    private final VentaRepuestoDAO daoVenta;

    @Inject
    public PagoServImpl(PagoDAO daoPago, ServiceDAO daoService, VentaRepuestoDAO daoVenta) {
        this.daoPago = daoPago;
        this.daoService = daoService;
        this.daoVenta = daoVenta;
    }

    @Transactional
    @Override
    public Transaccion agregarPagoTransaccion(Pago p, Transaccion tDTO) {
        daoPago.save(p);
        tDTO.asociarPago(p);
        Transaccion managedTransaccion = null;

        // TODO: mejorar todo esto que es un asco
        if (tDTO instanceof VentaRepuesto) {
            Optional<VentaRepuesto> result = daoVenta.fetchParaEdicion(tDTO.getId());
            if (result.isPresent())
                managedTransaccion = result.get();
        } else if (tDTO instanceof Service) {
            Optional<Service> result = daoService.datosPagos(tDTO.getId());
            if (result.isPresent())
                managedTransaccion = result.get();
        }

        tDTO.traerPagos().forEach(managedTransaccion::asociarPago);
        managedTransaccion.recalcularMontos();

        return managedTransaccion;
    }
}
