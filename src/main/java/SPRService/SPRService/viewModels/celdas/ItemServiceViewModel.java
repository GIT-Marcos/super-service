package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ItemServiceViewModel extends ItemOperacionViewModel{

    private Service service;

    private final ObjectProperty<EstadoService> estado = new SimpleObjectProperty<>();
    private final StringProperty patente = new SimpleStringProperty();

    public ItemServiceViewModel(Service s) {
        super("Service", s.getId(), s.getMontoTotal(), s.getMontoFaltante(), s.getFechaCarga());
        this.service = s;
        this.estado.set(s.getEstadoService());
        this.patente.set(s.getOrden().getVehiculo().getPatente());
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public EstadoService getEstado() {
        return estado.get();
    }

    public ObjectProperty<EstadoService> estadoProperty() {
        return estado;
    }

    public String getPatente() {
        return patente.get();
    }

    public StringProperty patenteProperty() {
        return patente;
    }
}
