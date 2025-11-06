package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.Trabajo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ItemTrabajoViewModel extends ItemDetalleViewModel {

    private Trabajo trabajo;

    private final StringProperty descripcionTrabajo = new SimpleStringProperty();

    public ItemTrabajoViewModel(Trabajo t) {
        super("Trabajo", t.getPrecio());
        this.descripcionTrabajo.set(t.getDescripcion());
        this.trabajo = t;
    }

    public Trabajo getTrabajo() {
        return trabajo;
    }

    public String getDescripcionTrabajo() {
        return descripcionTrabajo.get();
    }

    public StringProperty descripcionTrabajoProperty() {
        return descripcionTrabajo;
    }
}
