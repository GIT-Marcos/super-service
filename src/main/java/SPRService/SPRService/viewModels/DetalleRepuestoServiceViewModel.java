package SPRService.SPRService.viewModels;

import SPRService.SPRService.entities.DetalleRetiro;
import javafx.beans.property.*;

import java.math.BigDecimal;

/**
 * View model de celda que representa un REPUESTO para la órden.
 */
public class DetalleRepuestoServiceViewModel extends TrabajoViewModelRepuesto {

    private DetalleRetiro detalleRetiro;

    private final StringProperty codBarras = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> precioUnitario = new SimpleObjectProperty<>();
    private final DoubleProperty cantidad = new SimpleDoubleProperty();

    public DetalleRepuestoServiceViewModel(DetalleRetiro d) {
        super(d.getRepuesto().getDetalle(), d.getSubTotal());
        this.codBarras.set(d.getRepuesto().getCodBarra());
        this.precioUnitario.set(d.getRepuesto().getPrecio());
        this.cantidad.set(d.getCantidadRetirada());
    }

    @Override
    public String getCeldaExtra() {
        return "Código: " + getCodBarras() + "\nCantidad: " + getCantidad();
    }

    public DetalleRetiro getDetalleRetiro() {
        return detalleRetiro;
    }

    public String getCodBarras() {
        return codBarras.get();
    }

    public StringProperty codBarrasProperty() {
        return codBarras;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario.get();
    }

    public ObjectProperty<BigDecimal> precioUnitarioProperty() {
        return precioUnitario;
    }

    public double getCantidad() {
        return cantidad.get();
    }

    public DoubleProperty cantidadProperty() {
        return cantidad;
    }
}
