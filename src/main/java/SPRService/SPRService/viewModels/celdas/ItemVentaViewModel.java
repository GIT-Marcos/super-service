package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ItemVentaViewModel extends ItemOperacionViewModel{

    private VentaRepuesto venta;

    private final ObjectProperty<EstadoVentaRepuesto> estado = new SimpleObjectProperty<>();

    public ItemVentaViewModel(VentaRepuesto v) {
        super("Venta de repuestos", v.getId(), v.getMontoTotal(), v.getMontoFaltante(), v.getFechaVenta());
        this.venta = v;
        this.estado.set(v.getEstadoVenta());
    }

    public VentaRepuesto getVenta() {
        return venta;
    }

    public void setVenta(VentaRepuesto venta) {
        this.venta = venta;
    }

    public EstadoVentaRepuesto getEstado() {
        return estado.get();
    }

    public ObjectProperty<EstadoVentaRepuesto> estadoProperty() {
        return estado;
    }
}
