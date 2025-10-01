package SPRService.SPRService.viewModels.tablas;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import SPRService.SPRService.entities.VentaRepuesto;

public class VentaRepuestoVMtabla {

    private final VentaRepuesto ventaRepuesto;

    private final LongProperty codVenta;
    private final StringProperty estadoVenta;
    private final StringProperty fechaVenta;
    private final StringProperty montoVenta;

    public VentaRepuestoVMtabla(VentaRepuesto v) {
        this.ventaRepuesto = v;
        this.codVenta = new SimpleLongProperty(v.getId());
        this.estadoVenta = new SimpleStringProperty(v.getEstadoVenta().toString());
        this.fechaVenta = new SimpleStringProperty(v.getFechaVenta().toString());
        this.montoVenta = new SimpleStringProperty("$ "+ v.getMontoTotal());
    }

    public VentaRepuesto getVentaRepuesto() {
        return ventaRepuesto;
    }

    public long getCodVenta() {
        return codVenta.get();
    }

    public LongProperty codVentaProperty() {
        return codVenta;
    }

    public String getEstadoVenta() {
        return estadoVenta.get();
    }

    public StringProperty estadoVentaProperty() {
        return estadoVenta;
    }

    public String getFechaVenta() {
        return fechaVenta.get();
    }

    public StringProperty fechaVentaProperty() {
        return fechaVenta;
    }

    public String getMontoVenta() {
        return montoVenta.get();
    }

    public StringProperty montoVentaProperty() {
        return montoVenta;
    }

    public void actualizarDesdeEntidad(VentaRepuesto v) {
        // - Propiedades de entidad original
        this.ventaRepuesto.setId(v.getId());
        this.ventaRepuesto.setActivo(v.getActivo());
        this.ventaRepuesto.setEstadoVenta(v.getEstadoVenta());
        this.ventaRepuesto.setFechaVenta(v.getFechaVenta());
        this.ventaRepuesto.setMontoFaltante(v.getMontoFaltante());
        this.ventaRepuesto.setMontoTotal(v.getMontoTotal());
        // -- Nota de retiro
        this.ventaRepuesto.setNotaRetiro(v.getNotaRetiro());
//        this.ventaRepuesto.getNotaRetiro().setId(v.getNotaRetiro().getId());
        // -- Pagos
        this.ventaRepuesto.setPagosList(v.getPagosList());

        // - Propiedades de VM
        this.codVenta.set(v.getId());
        this.estadoVenta.set(v.getEstadoVenta().toString());
        this.fechaVenta.set(v.getFechaVenta().toString());
        this.montoVenta.set("$ " + v.getMontoTotal());
    }
}
