package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.Repuesto;
import javafx.beans.property.*;

import java.math.BigDecimal;

public class ItemRepuestoViewModel {

    private Repuesto repuesto;

    private final StringProperty codBarras = new SimpleStringProperty();
    private final StringProperty nombreRepuesto = new SimpleStringProperty();
    private final StringProperty marcaRepuesto = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> precioUnitario = new SimpleObjectProperty<>();
    private final DoubleProperty cantidadExistente = new SimpleDoubleProperty();
    private final DoubleProperty cantidadMinima = new SimpleDoubleProperty();
    private final StringProperty unidadMedida = new SimpleStringProperty();
    private final BooleanProperty activo = new SimpleBooleanProperty();

    public ItemRepuestoViewModel(Repuesto r) {
        this.repuesto = r;
        this.codBarras.set(r.getCodBarra());
        this.nombreRepuesto.set(r.getDetalle());
        this.marcaRepuesto.set(r.getMarcaRepuesto().getNombreMarca());
        this.precioUnitario.set(r.getPrecio());
        this.cantidadExistente.set(r.getStock().getCantidadExistente());
        this.cantidadMinima.set(r.getStock().getCantMinima());
        this.unidadMedida.set(r.getStock().getUnidadMedida());
        this.activo.set(r.getActivo());
    }

    public Repuesto getRepuesto() {
        return repuesto;
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

    public String getMarcaRepuesto() {
        return marcaRepuesto.get();
    }

    public StringProperty marcaRepuestoProperty() {
        return marcaRepuesto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario.get();
    }

    public ObjectProperty<BigDecimal> precioUnitarioProperty() {
        return precioUnitario;
    }

    public double getCantidadExistente() {
        return cantidadExistente.get();
    }

    public DoubleProperty cantidadExistenteProperty() {
        return cantidadExistente;
    }

    public double getCantidadMinima() {
        return cantidadMinima.get();
    }

    public DoubleProperty cantidadMinimaProperty() {
        return cantidadMinima;
    }

    public String getUnidadMedida() {
        return unidadMedida.get();
    }

    public StringProperty unidadMedidaProperty() {
        return unidadMedida;
    }

    public boolean isActivo() {
        return activo.get();
    }

    public BooleanProperty activoProperty() {
        return activo;
    }
}
