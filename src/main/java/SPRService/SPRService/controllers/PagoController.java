package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Transaccion;
import SPRService.SPRService.services.PagoServ;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import SPRService.SPRService.entities.Pago;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.MetodosPago;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.Operador;
import org.hibernate.HibernateException;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PagoController implements Initializable, DataReceiver<Transaccion>, ModalController<Transaccion> {

    private Transaccion transaccion;
    // Esta venta solo será inicializada si se concreta alguna operación, carga o modificación.
    private Transaccion transaccionParaDevolver;
    private final VentaRepuestoServ ventaRepuestoServ;
    private final ServiceServ serviceServ;
    private final PagoServ pagoServ;
    private String rutaComprobante;
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
    public void receiveData(Transaccion data) {
        if (data != null) {
            this.transaccion = data;
            labelTotal.setText("$ " + data.getMontoFaltante());
            // Usa el de la interfaz
            if (transaccion.yaPersistida()) {
                this.flagAgregarPago = true;
            }
        }
    }

    @Override
    public Optional<Transaccion> getResult() {
        return Optional.ofNullable(this.transaccionParaDevolver);
    }

    @FXML
    private void pagar(ActionEvent event) {
        // 1. CAPTURA Y VALIDACIÓN DE INPUTS

        // Obtener el tipo de pago seleccionado
        MetodosPago metodosPago = tomaMetodoPago();
        String rutaComprobante = null;
        if (metodosPago == MetodosPago.TRANSFERENCIA) {
            if (this.rutaComprobante == null) {
                NotificationHelper.mostrarAdvertencia("Pago",
                        "Si se paga por transferencia se debe adjuntar un comprobante.");
                return;
            }
            rutaComprobante = this.rutaComprobante;
        }

        // Capturar los valores de los controles
        String inputMonto = tfMonto.getText();
        Integer inputDescuento = spinDescuento.getValue();
        String marcaTarjeta = comboMarcaTarjeta.getSelectionModel().getSelectedItem();
        String bancoTarjeta = comboBancoTarjeta.getSelectionModel().getSelectedItem();
        String ultimos4 = tfUltimos4.getText();
        String nroReferencia = tfNroReferencia.getText();
        String dniCliente = tfDniCliente.getText();

        BigDecimal monto;
        BigDecimal porcentajeDescuento;
        BigDecimal montoPagar;

        try {
            // Validación de Monto y Descuento
            monto = ManejadorInputs.dinero(inputMonto, true, false);
            porcentajeDescuento = ManejadorInputs.porcentaje(inputDescuento.toString(), false);
            ManejadorInputs.dni(dniCliente, false);

            // Validación de campos de Tarjeta/Transferencia si aplica
            if (metodosPago != MetodosPago.EFECTIVO) {
                ManejadorInputs.marcaTarjetaYBanco(marcaTarjeta, true, null, 30);
                ManejadorInputs.marcaTarjetaYBanco(bancoTarjeta, true, null, 30);
                ManejadorInputs.ultimos4(ultimos4, true);
                ManejadorInputs.referenciaTarjeta(nroReferencia, true);
                ManejadorInputs.dni(dniCliente, true);
            }

            // Validación de Monto vs Monto Faltante
            if (monto.compareTo(this.transaccion.getMontoFaltante()) > 0) {
                NotificationHelper.mostrarAdvertencia("Pago", "El monto ingresado ($" + monto + ") es mayor al que se " +
                        "debe pagar ($" + this.transaccion.getMontoFaltante() + ").");
                return;
            }

            // 2. CÁLCULO DEL MONTO A PAGAR CON DESCUENTO
            if (inputDescuento > 0) {
                montoPagar = Operador.aplicarDescuento(monto, porcentajeDescuento);
            } else {
                montoPagar = monto;
            }
        } catch (NullPointerException | NumberFormatException e) {
            NotificationHelper.mostrarAdvertencia("Pago", e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarError("Pago", e.getMessage());
            return;
        }

        // 3. CONFIRMACIÓN DEL USUARIO
        boolean confirmacion = SimpleDialogs.confirmacion("¿Confirmar Pago?",
                "El total a pagar con descuentos incluidos será: $ " + montoPagar);
        if (!confirmacion) return;

        // 4. CREACIÓN DEL PAGO Y ASOCIACIÓN A LA TRANSACCIÓN
        Pago pagoParaCargar = new Pago(dniCliente, montoPagar, marcaTarjeta, bancoTarjeta, nroReferencia,
                porcentajeDescuento, ultimos4, rutaComprobante, metodosPago);

        // Asocia el pago. El 'asociarPago' de la entidad se encargará de actualizar su montoFaltante.
//        this.transaccion.asociarPago(pagoParaCargar);

        // 5. PERSISTENCIA DE LA TRANSACCIÓN (DELEGACIÓN)
        try {
            Transaccion transaccionGuardada = null;
            String nombreTransaccion = (this.transaccion instanceof VentaRepuesto) ? "Venta" : "Service";

            if (!this.flagAgregarPago) {
                // Lógica para CARGAR una transacción NUEVA
                if (this.transaccion instanceof VentaRepuesto) {
                    transaccionGuardada = ventaRepuestoServ.cargarVenta((VentaRepuesto) this.transaccion, pagoParaCargar);
                    NotificationHelper.mostrarExito("Pago", nombreTransaccion + " y pago cargados con éxito.\nSe ha actualizado el stock.");
                } else if (this.transaccion instanceof Service) {
                    transaccionGuardada = serviceServ.cargarService((Service) this.transaccion);
                    NotificationHelper.mostrarExito("Pago", nombreTransaccion + " y pago cargados con éxito.");
                } else {
                    throw new IllegalArgumentException("Tipo de transacción no soportado para carga.");
                }
            } else {
                // Lógica para MODIFICAR una transacción EXISTENTE (solo agregando un pago)
                if (this.transaccion instanceof VentaRepuesto) {
                    transaccionGuardada = pagoServ.agregarPagoTransaccion(pagoParaCargar, this.transaccion);
                    NotificationHelper.mostrarExito("Pago", "Pago cargado a " + nombreTransaccion + " correctamente.");
                } else if (this.transaccion instanceof Service) {
                    transaccionGuardada = serviceServ.modificarService((Service) this.transaccion);
                    NotificationHelper.mostrarExito("Pago", "Pago cargado a " + nombreTransaccion + " correctamente.");
                } else {
                    throw new IllegalArgumentException("Tipo de transacción no soportado para modificación.");
                }
            }

            // Almacena la transacción actualizada/guardada para devolverla al modal
            this.transaccionParaDevolver = transaccionGuardada;
            volver(event);
        } catch (HibernateException | IllegalArgumentException e) {
            NotificationHelper.mostrarError("Error de Persistencia", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void adjuntar(ActionEvent event) {
        // 1. Crear el FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar Imagen");

        // 2. Agregar filtros para facilitar la búsqueda
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        // 3. Obtener la ventana (Stage) actual para bloquearla mientras se abre el diálogo
        // Obtenemos el Stage desde el evento del botón presionado
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        // 4. Mostrar el diálogo de abrir
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            rutaComprobante = file.getAbsolutePath();
            btnAdjuntar.setText("CAMBIAR COMPROBANTE");

            // Limpias estilos anteriores si es necesario y agregas la clase nueva
            btnAdjuntar.getStyleClass().clear();
            // Ojo: al hacer clear() borras también estilos base como "button",
            // a veces es mejor solo agregar la nueva o remover la vieja especifica.
            btnAdjuntar.getStyleClass().add("button"); // Añadir estilo base de JavaFX
            btnAdjuntar.getStyleClass().add("btn-primary"); // Añadir tu estilo verde
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
