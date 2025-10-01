package SPRService.SPRService.controllers;

import SPRService.SPRService.services.VentaRepuestoServ;
import com.google.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.Operador;
import SPRService.SPRService.util.alertas.Alertas;
import org.hibernate.HibernateException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class PagoController implements Initializable, DataReceiver<VentaRepuesto>, ModalController<VentaRepuesto> {

    private VentaRepuesto venta;
    // Esta venta solo será inicializada si se concreta alguna operación, carga o modificación.
    private VentaRepuesto ventaParaDevolver;
//    private Pago pago;
    private final VentaRepuestoServ ventaRepuestoServ;
    //para indicar cuando se agrega un pago a una venta ya hecha o es una venta nueva.
    private boolean flagAgregarPago = false;

    @FXML
    private Label labelTotal;
    @FXML
    private Spinner<Integer> spinDescuento;
    @FXML
    private ToggleGroup radiosFormaPago;
    @FXML
    private RadioButton radTarjCredito, radTarjDebito, radEfectivo, radTransferencia;
    @FXML
    private TextField tfMonto, tfUltimos4, tfNroReferencia;
    @FXML
    private ComboBox<String> comboMarcaTarjeta, comboBancoTarjeta;

    @Inject
    public PagoController(VentaRepuestoServ ventaRepuestoServ) {
        this.ventaRepuestoServ = ventaRepuestoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarCombos();
        seteaSpinner();
        listenerGrupoRadios();
    }

    /**
     * Nunca pasar nulo.
     */
    @Override
    public void receiveData(VentaRepuesto data) {
        if (data != null) {
            this.venta = data;
            labelTotal.setText("TOTAL: $ " + data.getMontoFaltante());
            // las ventas para cargar tiene siempre id nulo.
            if (venta.getId() != null) {
                this.flagAgregarPago = true;
            }
        }
    }

    @Override
    public Optional<VentaRepuesto> getResult() {
        return Optional.ofNullable(this.ventaParaDevolver);
    }

    @FXML
    private void pagar(ActionEvent event) {
        Pago pagoParaCargar;

        MetodosPago metodosPago = tomaMetodoPago();
        String inputMonto = tfMonto.getText().trim();
        Integer inputDescuento = spinDescuento.getValue();
        String marcaTarjeta = comboMarcaTarjeta.getSelectionModel().getSelectedItem();
        String bancoTarjeta = comboBancoTarjeta.getSelectionModel().getSelectedItem();
        String ultimos4 = tfUltimos4.getText().trim();
        String nroReferencia = tfNroReferencia.getText().trim();

        BigDecimal monto;
        BigDecimal porcentajeDescuento;
        BigDecimal montoPagar;
        try {
            monto = ManejadorInputs.dinero(inputMonto, true);
            porcentajeDescuento = ManejadorInputs.porcentaje(inputDescuento, false);
            if (metodosPago != MetodosPago.EFECTIVO) {
                ManejadorInputs.comboBox(marcaTarjeta, true, null, 30);
                ManejadorInputs.comboBox(bancoTarjeta, true, null, 30);
                ManejadorInputs.ultimos4(ultimos4, true);
                ManejadorInputs.referenciaTarjeta(nroReferencia, true);
            }
            if (monto.compareTo(this.venta.getMontoFaltante()) == 1) {
                Alertas.aviso("Pago", "El monto ingresado es mayor al que se debe pagar.");
                return;
            }
            if (inputDescuento > 0) {
                montoPagar = Operador.aplicarDescuento(monto, porcentajeDescuento);
            } else {
                montoPagar = monto;
            }
        } catch (NullPointerException | NumberFormatException npe) {
            Alertas.aviso("Pago", npe.getMessage());
            return;
        } catch (IllegalArgumentException iae) {
            Alertas.aviso("pago", iae.getMessage());
            return;
        }

        boolean confirmacion = Alertas.confirmacion("¿Pagar?", "¿Continuar con el pago?\n" +
                "El total a pagar con descuentos incluidos serán: $ " + montoPagar);
        if (!confirmacion) return;

        //todo: hacer bien esto con patrón diseño y herencia de pago y los métodos de pago
        pagoParaCargar = new Pago(null, null, montoPagar, marcaTarjeta, bancoTarjeta, nroReferencia,
                porcentajeDescuento, ultimos4, metodosPago, this.venta);
        this.venta.asociarPago(pagoParaCargar);

        try {
            if (!this.flagAgregarPago) {
                this.ventaParaDevolver = ventaRepuestoServ.cargarVenta(this.venta);
                Alertas.exito("Pago", "Venta y pago cargados con éxito.\n" +
                        "Se ha actualizado el stock.");
            } else {
                this.ventaParaDevolver = ventaRepuestoServ.modificarVenta(this.venta);
                Alertas.exito("Pago", "Pago cargado a venta correctamente.");
            }
            volver(event);
        } catch (HibernateException e) {
            Alertas.error("Pago", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void volver(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void listenerGrupoRadios() {
        radiosFormaPago.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (tomaMetodoPago() == MetodosPago.EFECTIVO) {
                    comboMarcaTarjeta.setDisable(true);
                    comboBancoTarjeta.setDisable(true);
                    tfUltimos4.setDisable(true);
                    tfNroReferencia.setDisable(true);
                } else {
                    comboMarcaTarjeta.setDisable(false);
                    comboBancoTarjeta.setDisable(false);
                    tfUltimos4.setDisable(false);
                    tfNroReferencia.setDisable(false);
                }
            }
        });
    }

    private MetodosPago tomaMetodoPago() {
        if (radTarjCredito.isSelected()) {
            return MetodosPago.TARJETA_CREDITO;
        } else if (radTarjDebito.isSelected()) {
            return MetodosPago.TARJETA_DEBITO;
        } else if (radEfectivo.isSelected()) {
            return MetodosPago.EFECTIVO;
        } else if (radTransferencia.isSelected()) {
            return MetodosPago.TRANSFERENCIA;
        }
        return null;
    }

    private void llenarCombos() {
        ObservableList<String> listaMarcas = FXCollections.observableArrayList();
        listaMarcas.add("Visa");
        listaMarcas.add("Mastercard");
        listaMarcas.add("Tarjeta Naranja");
        listaMarcas.add("Kadicard");
        listaMarcas.add("American Express");
        comboMarcaTarjeta.setItems(listaMarcas);
        /*************************/
        ObservableList<String> listaBancos = FXCollections.observableArrayList();
        listaBancos.add("Galicia");
        listaBancos.add("Santander Río");
        listaBancos.add("Nación");
        listaBancos.add("Macro");
        listaBancos.add("American Express");
        listaBancos.add("Hipotecario");
        listaBancos.add("Supervielle");
        listaBancos.add("BBVA");
        comboBancoTarjeta.setItems(listaBancos);
    }

    private void seteaSpinner() {
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 100, 0, 1);
        spinDescuento.setValueFactory(spinnerValueFactory);
    }

}
