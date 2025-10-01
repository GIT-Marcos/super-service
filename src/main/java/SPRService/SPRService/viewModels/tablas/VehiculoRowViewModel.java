package SPRService.SPRService.viewModels.tablas;

import SPRService.SPRService.entities.Vehiculo;
import javafx.beans.property.*;

public class VehiculoRowViewModel {

    private final Vehiculo vehiculo;

    private final StringProperty patente;
    private final StringProperty marca;
    private final StringProperty modelo;
    private final DoubleProperty cilindrada;
    private final IntegerProperty anio;
    private final StringProperty color;

    public VehiculoRowViewModel(Vehiculo v) {
        this.vehiculo = v;
        this.patente = new SimpleStringProperty(v.getPatente());
        this.marca = new SimpleStringProperty(v.getModeloVehiculo().getMarcaVehiculo().getNombreMarca());
        this.modelo = new SimpleStringProperty(v.getModeloVehiculo().getNombreModelo());
        this.cilindrada = new SimpleDoubleProperty(v.getModeloVehiculo().getCilindrada());
        this.anio = new SimpleIntegerProperty(v.getModeloVehiculo().getAnio().getValue());
        this.color = new SimpleStringProperty(v.getColor());
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public String getPatente() {
        return patente.get();
    }

    public StringProperty patenteProperty() {
        return patente;
    }

    public String getMarca() {
        return marca.get();
    }

    public StringProperty marcaProperty() {
        return marca;
    }

    public String getModelo() {
        return modelo.get();
    }

    public StringProperty modeloProperty() {
        return modelo;
    }

    public double getCilindrada() {
        return cilindrada.get();
    }

    public DoubleProperty cilindradaProperty() {
        return cilindrada;
    }

    public int getAnio() {
        return anio.get();
    }

    public IntegerProperty anioProperty() {
        return anio;
    }

    public String getColor() {
        return color.get();
    }

    public StringProperty colorProperty() {
        return color;
    }
}
