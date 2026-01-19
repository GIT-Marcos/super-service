package SPRService.SPRService.viewModels.celdas;

import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.enums.MetodosPago;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ItemPagoViewModel {

    private Pago pago;

    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty dni = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> fechaPago = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> montoPagado = new SimpleObjectProperty<>();
    private final StringProperty marcaTarjeta = new SimpleStringProperty();
    private final StringProperty banco = new SimpleStringProperty();
    private final StringProperty referencia = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> descuento = new SimpleObjectProperty<>();
    private final StringProperty ultimos4 = new SimpleStringProperty();
    private final BooleanProperty activo = new SimpleBooleanProperty();
    private final ObjectProperty<MetodosPago> metodoPago = new SimpleObjectProperty<>();

    public ItemPagoViewModel(Pago pago) {
        this.pago = pago;
        this.id.set(pago.getId());
        this.dni.set(pago.getDni());
        this.fechaPago.set(pago.getFechaPago());
        this.montoPagado.set(pago.getMontoPagado());
        this.marcaTarjeta.set(pago.getMarcaTarjeta());
        this.banco.set(pago.getBanco());
        this.referencia.set(pago.getReferencia());
        this.descuento.set(pago.getDescuento());
        this.ultimos4.set(pago.getUltimos4());
        this.activo.set(pago.getActivo());
        this.metodoPago.set(pago.getMetodoPago());
    }

    // Getters
    public Pago getPago() { return pago; }
    public UUID getId() {
        return id.get();
    }
    public String getDni() { return dni.get(); }
    public LocalDateTime getFechaPago() { return fechaPago.get(); }
    public BigDecimal getMontoPagado() { return montoPagado.get(); }
    public String getMarcaTarjeta() { return marcaTarjeta.get(); }
    public String getBanco() { return banco.get(); }
    public String getReferencia() { return referencia.get(); }
    public BigDecimal getDescuento() { return descuento.get(); }
    public String getUltimos4() { return ultimos4.get(); }
    public Boolean getActivo() { return activo.get(); }
    public MetodosPago getMetodoPago() { return metodoPago.get(); }

    // Properties

    public ObjectProperty<UUID> idProperty() {
        return id;
    }
    public StringProperty dniProperty() { return dni; }
    public ObjectProperty<LocalDateTime> fechaPagoProperty() { return fechaPago; }
    public ObjectProperty<BigDecimal> montoPagadoProperty() { return montoPagado; }
    public StringProperty marcaTarjetaProperty() { return marcaTarjeta; }
    public StringProperty bancoProperty() { return banco; }
    public StringProperty referenciaProperty() { return referencia; }
    public ObjectProperty<BigDecimal> descuentoProperty() { return descuento; }
    public StringProperty ultimos4Property() { return ultimos4; }
    public BooleanProperty activoProperty() { return activo; }
    public ObjectProperty<MetodosPago> metodoPagoProperty() { return metodoPago; }
}