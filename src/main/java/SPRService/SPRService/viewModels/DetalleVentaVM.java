package SPRService.SPRService.viewModels;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import SPRService.SPRService.entities.DetalleRetiro;

/**
 * Los detalles de las ventas representan lo mismo que los detalles de los retiros, puesto que lo que se vende
 * es retirado de los depósitos. Este View Model intenta representar los detalles de las ventas a partir de los
 * detalles de retiro.
 */
public class DetalleVentaVM {

    private final DetalleRetiro detalleRetiro;

    private final StringProperty nombre;
    private final StringProperty marca;
    private final StringProperty precioUnitario;
    private final DoubleProperty cantidadVendida;
    private final StringProperty subTotal;

    public DetalleVentaVM(DetalleRetiro d) {
        this.detalleRetiro = d;
        this.nombre = new SimpleStringProperty(d.getRepuesto().getDetalle());
        this.marca = new SimpleStringProperty(d.getRepuesto().getMarca());
        this.precioUnitario = new SimpleStringProperty("$ " + d.getRepuesto().getPrecio());
        this.cantidadVendida = new SimpleDoubleProperty(d.getCantidadRetirada());
        this.subTotal = new SimpleStringProperty("$ "+ d.getSubTotal());
    }

    public DetalleRetiro getDetalleRetiro() {
        return detalleRetiro;
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getMarca() {
        return marca.get();
    }

    public StringProperty marcaProperty() {
        return marca;
    }

    public String getPrecioUnitario() {
        return precioUnitario.get();
    }

    public StringProperty precioUnitarioProperty() {
        return precioUnitario;
    }

    public double getCantidadVendida() {
        return cantidadVendida.get();
    }

    public DoubleProperty cantidadVendidaProperty() {
        return cantidadVendida;
    }

    public String getSubTotal() {
        return subTotal.get();
    }

    public StringProperty subTotalProperty() {
        return subTotal;
    }

    public void actualizarDesdeEntidad(DetalleRetiro d) {
        // - Propiedades de la entidad
        this.detalleRetiro.setId(d.getId());
        this.detalleRetiro.setCantidadRetirada(d.getCantidadRetirada());
        this.detalleRetiro.setRepuesto(d.getRepuesto());
        this.detalleRetiro.setSubTotal(d.getSubTotal());
        // - Propiedades del VM
        this.nombre.set(d.getRepuesto().getDetalle());
        this.marca.set(d.getRepuesto().getMarca());
        this.precioUnitario.set(d.getRepuesto().getPrecio().toString());
        this.cantidadVendida.set(d.getCantidadRetirada());
        this.subTotal.set(d.getSubTotal().toString());
    }

}
