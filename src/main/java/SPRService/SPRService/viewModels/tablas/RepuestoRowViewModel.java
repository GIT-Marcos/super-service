package SPRService.SPRService.viewModels.tablas;

import javafx.beans.property.*;
import SPRService.SPRService.entities.Repuesto;

public class RepuestoRowViewModel {
    private final Repuesto repuestoOriginal; // Guardamos una referencia al modelo original

    private final LongProperty id;
    private final StringProperty coBarra;
    private final StringProperty nombre;
    private final StringProperty marca;
    private final StringProperty precio;
    private final DoubleProperty cantidad;
    private final DoubleProperty cantidadMinima;
    private final StringProperty uniMedida;

    public RepuestoRowViewModel(Repuesto r) {
        this.repuestoOriginal = r; // Muy útil para operaciones como 'modificar' o 'borrar'
        this.id = new SimpleLongProperty(r.getId());
        this.coBarra = new SimpleStringProperty(r.getCodBarra());
        this.nombre = new SimpleStringProperty(r.getDetalle());
        this.marca = new SimpleStringProperty(r.getMarca());
        this.precio = new SimpleStringProperty("$ " + r.getPrecio());
        this.cantidad = new SimpleDoubleProperty(r.getStock().getCantidadExistente());
        this.cantidadMinima = new SimpleDoubleProperty(r.getStock().getCantMinima());
        this.uniMedida = new SimpleStringProperty(r.getStock().getUnidadMedida());
    }

    // para acceder al modelo subyacente
    public Repuesto getRepuestoOriginal() {
        return repuestoOriginal;
    }

    // Actualiza las propiedades del ViewModel a partir del modelo
    public void updateFrom(Repuesto repuestoActualizado) {
        // Asumiendo que las propiedades no son finales, si necesitas actualizarlas in-situ
        // - propiedades de la entidad
        this.repuestoOriginal.setId(repuestoActualizado.getId());
        this.repuestoOriginal.setCodBarra(repuestoActualizado.getCodBarra());
        this.repuestoOriginal.setMarca(repuestoActualizado.getMarca());
        this.repuestoOriginal.setDetalle(repuestoActualizado.getDetalle());
        this.repuestoOriginal.setPrecio(repuestoActualizado.getPrecio());
        this.repuestoOriginal.setStock(repuestoActualizado.getStock());
        this.repuestoOriginal.getStock().setId(repuestoActualizado.getId());
        this.repuestoOriginal.getStock().setCantidadExistente(repuestoActualizado.getStock().getCantidadExistente());
        this.repuestoOriginal.getStock().setCantMinima(repuestoActualizado.getStock().getCantMinima());
        this.repuestoOriginal.getStock().setUbicacion(repuestoActualizado.getStock().getUbicacion());
        this.repuestoOriginal.getStock().setUnidadMedida(repuestoActualizado.getStock().getUnidadMedida());
        this.repuestoOriginal.getStock().setLote(repuestoActualizado.getStock().getLote());
        this.repuestoOriginal.getStock().setObservaciones(repuestoActualizado.getStock().getObservaciones());
        // - propiedades del VM
        this.id.set(repuestoActualizado.getId());
        this.coBarra.set(repuestoActualizado.getCodBarra());
        this.nombre.set(repuestoActualizado.getDetalle());
        this.marca.set(repuestoActualizado.getMarca());
        this.precio.set("$ " + repuestoActualizado.getPrecio());
        this.cantidad.set(repuestoActualizado.getStock().getCantidadExistente());
        this.cantidadMinima.set(repuestoActualizado.getStock().getCantMinima());
        this.uniMedida.set(repuestoActualizado.getStock().getUnidadMedida());
    }

    // Getters y métodos de propiedad (tu código original aquí está perfecto)
    public double getCantidad() {
        return cantidad.get();
    }

    public double getCantidadMinima() {
        return cantidadMinima.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty coBarraProperty() {
        return coBarra;
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public String getCoBarra() {
        return coBarra.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public String getMarca() {
        return marca.get();
    }

    public StringProperty marcaProperty() {
        return marca;
    }

    public String getPrecio() {
        return precio.get();
    }

    public StringProperty precioProperty() {
        return precio;
    }

    public DoubleProperty cantidadProperty() {
        return cantidad;
    }

    public DoubleProperty cantidadMinimaProperty() {
        return cantidadMinima;
    }

    public String getUniMedida() {
        return uniMedida.get();
    }

    public StringProperty uniMedidaProperty() {
        return uniMedida;
    }
}
