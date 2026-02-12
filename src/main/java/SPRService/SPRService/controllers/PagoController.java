package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Transaccion;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.PagoServ;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.Operador;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.HibernateException;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PagoController implements Initializable, DataReceiver<Transaccion>, ModalController<Transaccion> {

    private Transaccion transaccion;
    private Transaccion transaccionParaDevolver;
    private final VentaRepuestoServ ventaRepuestoServ;
    private final ServiceServ serviceServ;
    private final PagoServ pagoServ;
    private String rutaComprobante;
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
    private TextField tfMonto, tfUltimos4, tfNroReferencia, tfDniCliente;
    @FXML
    private ComboBox<String> comboMarcaTarjeta, comboBancoTarjeta;
    @FXML
    private Button btnAdjuntar;

    @Inject
    public PagoController(VentaRepuestoServ ventaRepuestoServ, ServiceServ serviceServ, PagoServ pagoServ) {
        this.ventaRepuestoServ = ventaRepuestoServ;
        this.serviceServ = serviceServ;
        this.pagoServ = pagoServ;
    }

    // ================= INIT =================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarCombos();
        seteaSpinner();
        listenerGrupoRadios();
    }

    @Override
    public void receiveData(Transaccion data) {
        if (data != null) {
            this.transaccion = data;
            labelTotal.setText("$ " + data.getMontoFaltante());
            this.flagAgregarPago = data.yaPersistida();
        }
    }

    @Override
    public Optional<Transaccion> getResult() {
        return Optional.ofNullable(transaccionParaDevolver);
    }

    // ================= ACCIÓN PRINCIPAL =================

    @FXML
    private void pagar(ActionEvent event) {

        MetodosPago metodo = tomaMetodoPago();

        Optional<PagoFormData> datosValidados = validarFormulario(metodo);
        if (datosValidados.isEmpty()) return;

        PagoFormData data = datosValidados.get();

        BigDecimal montoFinal = calcularMontoFinal(data.monto, data.descuento);

        boolean confirmacion = SimpleDialogs.confirmacion(
                "¿Confirmar Pago?",
                "El total a pagar con descuentos incluidos será: $ " + montoFinal
        );

        if (!confirmacion) return;

        Pago pago = construirPago(data, montoFinal, metodo);

        persistirPago(pago, event);
    }

    // ================= VALIDACIÓN =================

    private Optional<PagoFormData> validarFormulario(MetodosPago metodo) {

        PagoFormData data = new PagoFormData();
        StringBuilder errores = new StringBuilder("Por favor corrija los siguientes errores:\n");
        boolean hayErrores = false;

        boolean esTarjeta = esTarjeta(metodo);

        try {
            data.dni = ManejadorInputs.dni(tfDniCliente.getText(), esTarjeta);
            marcarCampoError(tfDniCliente, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfDniCliente, true);
            errores.append("- DNI: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        try {
            data.monto = ManejadorInputs.dinero(tfMonto.getText(), true, false);

            if (data.monto.compareTo(transaccion.getMontoFaltante()) > 0) {
                throw new IllegalArgumentException("El monto es mayor a la deuda.");
            }

            marcarCampoError(tfMonto, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfMonto, true);
            errores.append("- Monto: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        try {
            data.descuento = ManejadorInputs.porcentaje(
                    spinDescuento.getValue().toString(), false);
            marcarCampoError(spinDescuento, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(spinDescuento, true);
            errores.append("- Descuento: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        if (esTarjeta) {
            hayErrores |= validarDatosTarjeta(data, errores);
        } else {
            limpiarErroresTarjeta();
        }

        if (metodo == MetodosPago.TRANSFERENCIA) {
            if (rutaComprobante == null) {
                btnAdjuntar.getStyleClass().add("error-border");
                errores.append("- Debe adjuntar comprobante.\n");
                hayErrores = true;
            } else {
                data.rutaComprobante = rutaComprobante;
                btnAdjuntar.getStyleClass().remove("error-border");
            }
        }

        if (hayErrores) {
            NotificationHelper.mostrarError("Error de Validación", errores.toString());
            return Optional.empty();
        }

        return Optional.of(data);
    }

    private boolean validarDatosTarjeta(PagoFormData data, StringBuilder errores) {
        boolean error = false;

        try {
            data.marca = ManejadorInputs.marcaTarjetaYBanco(
                    comboMarcaTarjeta.getValue(), true, null, 30);
            marcarCampoError(comboMarcaTarjeta, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(comboMarcaTarjeta, true);
            errores.append("- Marca Tarjeta: ").append(e.getMessage()).append("\n");
            error = true;
        }

        try {
            data.banco = ManejadorInputs.marcaTarjetaYBanco(
                    comboBancoTarjeta.getValue(), true, null, 30);
            marcarCampoError(comboBancoTarjeta, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(comboBancoTarjeta, true);
            errores.append("- Banco: ").append(e.getMessage()).append("\n");
            error = true;
        }

        try {
            data.ultimos4 = ManejadorInputs.ultimos4(tfUltimos4.getText(), true);
            marcarCampoError(tfUltimos4, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfUltimos4, true);
            errores.append("- Últimos 4: ").append(e.getMessage()).append("\n");
            error = true;
        }

        try {
            data.referencia = ManejadorInputs.referenciaTarjeta(
                    tfNroReferencia.getText(), true);
            marcarCampoError(tfNroReferencia, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfNroReferencia, true);
            errores.append("- Referencia: ").append(e.getMessage()).append("\n");
            error = true;
        }

        return error;
    }

    private boolean esTarjeta(MetodosPago metodo) {
        return metodo == MetodosPago.TARJETA_CREDITO ||
                metodo == MetodosPago.TARJETA_DEBITO;
    }

    private void limpiarErroresTarjeta() {
        marcarCampoError(comboMarcaTarjeta, false);
        marcarCampoError(comboBancoTarjeta, false);
        marcarCampoError(tfUltimos4, false);
        marcarCampoError(tfNroReferencia, false);
    }

    // ================= NEGOCIO =================

    private BigDecimal calcularMontoFinal(BigDecimal monto, BigDecimal descuento) {
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            return Operador.aplicarDescuento(monto, descuento);
        }
        return monto;
    }

    private Pago construirPago(PagoFormData data, BigDecimal montoFinal, MetodosPago metodo) {
        return new Pago(data.dni, montoFinal, data.marca, data.banco, data.referencia, data.descuento,
                data.ultimos4, data.rutaComprobante, metodo);
    }

    private void persistirPago(Pago pago, ActionEvent event) {
        try {

            Transaccion transaccionGuardada;
            String nombre = (transaccion instanceof VentaRepuesto) ? "Venta" : "Service";

            if (!flagAgregarPago) {

                if (transaccion instanceof VentaRepuesto) {
                    transaccionGuardada =
                            ventaRepuestoServ.cargarVenta((VentaRepuesto) transaccion, pago);

                    NotificationHelper.mostrarExito("Pago",
                            nombre + " y pago cargados con éxito.\nStock actualizado.");
                } else {
                    transaccionGuardada =
                            serviceServ.cargarService((Service) transaccion);

                    NotificationHelper.mostrarExito("Pago",
                            nombre + " y pago cargados con éxito.");
                }

            } else {
                transaccionGuardada =
                        pagoServ.agregarPagoTransaccion(pago, transaccion);

                NotificationHelper.mostrarExito("Pago",
                        "Pago cargado correctamente.");
            }

            transaccionParaDevolver = transaccionGuardada;
            volver(event);

        } catch (HibernateException | IllegalArgumentException e) {
            NotificationHelper.mostrarError("Error de Persistencia", e.getMessage());
        }
    }

    // ================= UI =================

    @FXML
    private void adjuntar(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar Imagen");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            rutaComprobante = file.getAbsolutePath();
            btnAdjuntar.setText("CAMBIAR COMPROBANTE");
        }
    }

    @FXML
    private void volver(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void listenerGrupoRadios() {
        radiosFormaPago.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            boolean esEfectivo = tomaMetodoPago() == MetodosPago.EFECTIVO;

            tfDniCliente.setDisable(esEfectivo);
            comboMarcaTarjeta.setDisable(esEfectivo);
            comboBancoTarjeta.setDisable(esEfectivo);
            tfUltimos4.setDisable(esEfectivo);
            tfNroReferencia.setDisable(esEfectivo);

            if (esEfectivo) limpiarErroresTarjeta();
        });
    }

    private void marcarCampoError(Node node, boolean esError) {
        if (esError) {
            if (!node.getStyleClass().contains("error-border")) {
                node.getStyleClass().add("error-border");
            }
        } else {
            node.getStyleClass().remove("error-border");
        }
    }

    private MetodosPago tomaMetodoPago() {
        if (radTarjCredito.isSelected()) return MetodosPago.TARJETA_CREDITO;
        if (radTarjDebito.isSelected()) return MetodosPago.TARJETA_DEBITO;
        if (radTransferencia.isSelected()) return MetodosPago.TRANSFERENCIA;
        return MetodosPago.EFECTIVO;
    }

    private void llenarCombos() {
        comboMarcaTarjeta.setItems(FXCollections.observableArrayList(
                "Visa", "Mastercard", "Tarjeta Naranja",
                "Kadicard", "American Express"
        ));
        comboBancoTarjeta.setItems(FXCollections.observableArrayList(
                "Galicia", "Santander Río", "Nación",
                "Macro", "American Express",
                "Hipotecario", "Supervielle", "BBVA"
        ));
    }

    private void seteaSpinner() {
        spinDescuento.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0, 1)
        );
    }

    // ================= DTO INTERNO =================

    private static class PagoFormData {
        BigDecimal monto;
        BigDecimal descuento;
        String dni;
        String marca;
        String banco;
        String ultimos4;
        String referencia;
        String rutaComprobante;
    }
}
