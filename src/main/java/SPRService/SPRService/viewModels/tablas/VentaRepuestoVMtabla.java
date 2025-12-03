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

}
