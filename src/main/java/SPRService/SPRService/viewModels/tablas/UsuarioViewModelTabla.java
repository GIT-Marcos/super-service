package SPRService.SPRService.viewModels.tablas;

import SPRService.SPRService.entities.Usuario;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UsuarioViewModelTabla {

    private Usuario usuario;

    private final StringProperty nombre;
    private final StringProperty correo;
    private final StringProperty rol;
    private final StringProperty estado;
    private final BooleanProperty esAnulada; // Para lógica de colores/botones sin comparar strings

    public UsuarioViewModelTabla(Usuario u) {
        this.usuario = u;
        this.nombre = new SimpleStringProperty(u.getNombre());
        this.correo = new SimpleStringProperty(u.getCorreo());
        this.rol = new SimpleStringProperty(u.getRol().toString());

        // Lógica de presentación del estado
        boolean activa = u.getActivo() != null && u.getActivo();
        this.estado = new SimpleStringProperty(activa ? "ACTIVO" : "ANULADO");
        this.esAnulada = new SimpleBooleanProperty(!activa);
    }

    public void actualizarDatos(Usuario u) {
        this.usuario = u;
        this.nombre.set(u.getNombre());
        this.correo.set(u.getCorreo());
        this.rol.set(u.getRol().toString());
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getCorreo() {
        return correo.get();
    }

    public StringProperty correoProperty() {
        return correo;
    }

    public String getRol() {
        return rol.get();
    }

    public StringProperty rolProperty() {
        return rol;
    }

    public String getEstado() {
        return estado.get();
    }

    public StringProperty estadoProperty() {
        return estado;
    }

    public boolean isEsAnulada() {
        return esAnulada.get();
    }

    public BooleanProperty esAnuladaProperty() {
        return esAnulada;
    }
}
