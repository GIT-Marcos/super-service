package SPRService.SPRService.viewModels.tablas;

import javafx.beans.property.*;
import SPRService.SPRService.entities.VentaRepuesto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class VentaRepuestoVMtabla {

    private final VentaRepuesto ventaRepuesto;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final LongProperty codVenta;
    private final StringProperty estadoVenta;
    private final StringProperty fechaVenta;
    private final ObjectProperty<BigDecimal> montoVenta;

    public VentaRepuestoVMtabla(VentaRepuesto v) {
        this.ventaRepuesto = v;
        this.codVenta = new SimpleLongProperty(v.getId());
        this.estadoVenta = new SimpleStringProperty(v.getEstadoVenta().toString());
        this.fechaVenta = new SimpleStringProperty(v.getFechaVenta().format(DATE_FORMATTER));
        this.montoVenta = new SimpleObjectProperty<>(v.getMontoTotal());
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

    public BigDecimal getMontoVenta() {
        return montoVenta.get();
    }

    public ObjectProperty<BigDecimal> montoVentaProperty() {
        return montoVenta;
    }
}
