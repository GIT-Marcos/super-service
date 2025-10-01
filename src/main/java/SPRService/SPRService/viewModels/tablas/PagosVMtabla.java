package SPRService.SPRService.viewModels.tablas;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import SPRService.SPRService.entities.Pago;


public class PagosVMtabla {

    private final StringProperty fechaPago;
    private final StringProperty montoPago;
    private final StringProperty metodoPago;
    private final StringProperty tarjetaPago;
    private final StringProperty bancoPago;
    private final StringProperty descuentoPago;
    private final StringProperty nroRefPago;
    private final StringProperty ultimos4Pago;

    public PagosVMtabla(Pago p) {
        this.fechaPago = new SimpleStringProperty(String.valueOf(p.getFechaPago()));
        this.montoPago = new SimpleStringProperty("$ " + p.getMontoPagado());
        this.metodoPago = new SimpleStringProperty(p.getMetodoPago().toString());
        this.tarjetaPago = new SimpleStringProperty(p.getMarcaTarjeta());
        this.bancoPago = new SimpleStringProperty(p.getBanco());
        this.descuentoPago = new SimpleStringProperty("% " + p.getDescuento());
        this.nroRefPago = new SimpleStringProperty(p.getReferencia());
        this.ultimos4Pago = new SimpleStringProperty(p.getUltimos4());
    }

    public String getFechaPago() {
        return fechaPago.get();
    }

    public StringProperty fechaPagoProperty() {
        return fechaPago;
    }

    public String getMontoPago() {
        return montoPago.get();
    }

    public StringProperty montoPagoProperty() {
        return montoPago;
    }

    public String getMetodoPago() {
        return metodoPago.get();
    }

    public StringProperty metodoPagoProperty() {
        return metodoPago;
    }

    public String getTarjetaPago() {
        return tarjetaPago.get();
    }

    public StringProperty tarjetaPagoProperty() {
        return tarjetaPago;
    }

    public String getBancoPago() {
        return bancoPago.get();
    }

    public StringProperty bancoPagoProperty() {
        return bancoPago;
    }

    public String getDescuentoPago() {
        return descuentoPago.get();
    }

    public StringProperty descuentoPagoProperty() {
        return descuentoPago;
    }

    public String getNroRefPago() {
        return nroRefPago.get();
    }

    public StringProperty nroRefPagoProperty() {
        return nroRefPago;
    }

    public String getUltimos4Pago() {
        return ultimos4Pago.get();
    }

    public StringProperty ultimos4PagoProperty() {
        return ultimos4Pago;
    }
}
