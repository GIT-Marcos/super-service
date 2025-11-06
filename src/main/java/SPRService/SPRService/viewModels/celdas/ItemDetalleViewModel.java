package SPRService.SPRService.viewModels.celdas;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

public abstract class ItemDetalleViewModel {

    private final StringProperty tipo = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> subTotal = new SimpleObjectProperty<>();

    public ItemDetalleViewModel(String tipo, BigDecimal subTotal) {
        this.tipo.set(tipo);
        this.subTotal.set(subTotal);
    }

    public String getTipo() {
        return tipo.get();
    }

    public StringProperty tipoProperty() {
        return tipo;
    }

    public BigDecimal getSubTotal() {
        return subTotal.get();
    }

    public ObjectProperty<BigDecimal> subTotalProperty() {
        return subTotal;
    }

}
