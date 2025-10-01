package SPRService.SPRService.viewModels.tablas;

import SPRService.SPRService.entities.Cliente;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClienteViewModelTabla {

    private final Cliente clienteEntity;
    private final StringProperty dni;
    private final StringProperty apellido;
    private final StringProperty nombre;

    public ClienteViewModelTabla(Cliente c) {
        this.clienteEntity = c;
        this.dni = new SimpleStringProperty(c.getDni());
        this.apellido = new SimpleStringProperty(c.getApellido());
        this.nombre = new SimpleStringProperty(c.getNombre());
    }

    public void updateFrom(Cliente clienteActualizado) {
        this.dni.set(clienteActualizado.getDni());
        this.apellido.set(clienteActualizado.getApellido());
        this.nombre.set(clienteActualizado.getNombre());
    }

    public Cliente getClienteEntity() {
        return clienteEntity;
    }

    public String getDni() {
        return dni.get();
    }

    public StringProperty dniProperty() {
        return dni;
    }

    public String getApellido() {
        return apellido.get();
    }

    public StringProperty apellidoProperty() {
        return apellido;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }
}
