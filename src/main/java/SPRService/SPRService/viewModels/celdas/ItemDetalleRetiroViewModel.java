package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.DetalleRetiro;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class ItemDetalleRetiroViewModel extends ItemDetalleViewModel {

    private DetalleRetiro detalleRetiro;

    private final StringProperty codBarras = new SimpleStringProperty();
    private final StringProperty nombreRepuesto = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> precioUnitario = new SimpleObjectProperty<>();
    private final DoubleProperty cantidad = new SimpleDoubleProperty();

    public ItemDetalleRetiroViewModel(DetalleRetiro d) {
        super("Repuesto", d.getSubTotal());
        this.codBarras.set(d.getRepuesto().getCodBarra());
        this.nombreRepuesto.set(d.getRepuesto().getDetalle());
        this.precioUnitario.set(d.getRepuesto().getPrecio());
        this.cantidad.set(d.getCantidadRetirada());
        this.detalleRetiro = d;
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

    public String getNombreRepuesto() {
        return nombreRepuesto.get();
    }

    public StringProperty nombreRepuestoProperty() {
        return nombreRepuesto;
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
