package SPRService.SPRService.viewModels;

import SPRService.SPRService.entities.Trabajo;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * View model de celda que representa un TRABAJO para la órden.
 */
public class TrabajoViewModelRepuesto {

    private Trabajo trabajo;

    private final StringProperty descripcion = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> subTotal = new SimpleObjectProperty<>();

    public TrabajoViewModelRepuesto(String descripcion, BigDecimal subTotal) {
        this.trabajo = new Trabajo(null, descripcion, subTotal);
        this.descripcion.set(descripcion);
        this.subTotal.set(subTotal);
    }

    /**
     * Devuelve la información que la celda debería mostrar.
     * Puede ser texto, etiquetas separadas, etc.
     */
    public String getCeldaDescripcion() {
        return getDescripcion(); // por defecto, solo la descripción
    }

    public String getCeldaExtra() {
        return ""; // por defecto vacío
    }

    public Trabajo getTrabajo() {
        return trabajo;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public BigDecimal getSubTotal() {
        return subTotal.get();
    }

    public ObjectProperty<BigDecimal> subTotalProperty() {
        return subTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TrabajoViewModelRepuesto that)) return false;
        return Objects.equals(descripcion, that.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(descripcion);
    }
}
