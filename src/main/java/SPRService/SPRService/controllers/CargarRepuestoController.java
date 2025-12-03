package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.CargaRepuestoViewModel;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarRepuestoController implements Initializable, DataReceiver<Repuesto>, ModalController<Repuesto> {

    private final CargaRepuestoViewModel viewModel;
    private Repuesto resultado;

    @FXML
    private TextField tfCodBarra, tfNombre, tfPrecio, tfCantidadStock, tfCantidadStockMin, tfLote,
            tfObservaciones;
    @FXML
    private ComboBox<MarcaRepuesto> comboMarcas;
    @FXML
    private ComboBox<String> comboUniMedidas, comboUbicaciones;

    @Inject
    public CargarRepuestoController(CargaRepuestoViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.inicializar();
        // REMOVIDO: configurarFormattersNumericos();
        bindControls();
        configurarValidacionesDeColor(); // NUEVO: Configura las validaciones visuales

        Platform.runLater(() -> {
            Stage s = (Stage) tfCodBarra.getScene().getWindow();
            s.setOnCloseRequest(event -> {
                this.viewModel.limpiar();
            });
        });
    }

    // REMOVIDO: configurarFormattersNumericos()

    private void bindControls() {
        // Mejorado: Ahora todas las propiedades de texto en el ViewModel son StringProperty
        // No se necesitan StringConverters ni .asObject() para los TextFields.

        tfCodBarra.textProperty().bindBidirectional(viewModel.codBarrasProperty());
        tfNombre.textProperty().bindBidirectional(viewModel.nombreProductoProperty());
        tfLote.textProperty().bindBidirectional(viewModel.loteProperty());
        tfObservaciones.textProperty().bindBidirectional(viewModel.observacionesProperty());

        // Campos numéricos (ahora StringProperty en el ViewModel)
        tfCantidadStock.textProperty().bindBidirectional(viewModel.cantidadExistenteProperty());
        tfCantidadStockMin.textProperty().bindBidirectional(viewModel.cantidadMinimaProperty());
        tfPrecio.textProperty().bindBidirectional(viewModel.precioProperty());

        // ComboBox bindings
        comboMarcas.setItems(viewModel.getMarcasDisponibles());
        comboMarcas.valueProperty().bindBidirectional(viewModel.marcaSeleccionadaProperty());
        comboUniMedidas.setItems(viewModel.getUnidadesDeMedida());
        comboUniMedidas.valueProperty().bindBidirectional(viewModel.uniMedidaSeleccionadaProperty());
        comboUbicaciones.setItems(viewModel.getUbicacionesDisponibles());
        comboUbicaciones.valueProperty().bindBidirectional(viewModel.ubicacionSeleccionadaProperty());
    }

    /**
     * Establece listeners para la validación visual de los campos.
     */
    private void configurarValidacionesDeColor() {
        // VALIDACIONES OBLIGATORIAS y DE FORMATO
        // Escucha cambios en el foco (para validar al salir del campo)
        tfCodBarra.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfCodBarra,
                    () -> ManejadorInputs.codBarras(tfCodBarra.getText(), true));
        });

        tfNombre.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfNombre,
                    () -> ManejadorInputs.textoGenerico(tfNombre.getText(), true, "Nombre de repuesto", 60));
        });

        // Se valida el precio y stock como numérico al perder el foco
        tfPrecio.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfPrecio,
                    () -> ManejadorInputs.dinero(tfPrecio.getText(), true, false));
        });

        tfCantidadStock.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfCantidadStock,
                    () -> ManejadorInputs.cantidadStock(tfCantidadStock.getText(), true));
        });

        tfCantidadStockMin.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfCantidadStockMin,
                    () -> ManejadorInputs.cantidadStock(tfCantidadStockMin.getText(), true));
        });

        // VALIDACIONES OPCIONALES (solo para cambiar el color si no cumplen el formato/largo)
        tfLote.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfLote,
                    () -> ManejadorInputs.textoGenerico(tfLote.getText(), false, null, 40));
        });
        tfObservaciones.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) validarCampo(tfObservaciones,
                    () -> ManejadorInputs.textoGenerico(tfObservaciones.getText(), false, null, 100));
        });

        // Validación para ComboBox (al cambiar la selección)
        comboMarcas.valueProperty().addListener((obs, oldValue, newValue) ->
                setValidationStyle(comboMarcas, newValue != null, "Debe seleccionar una marca."));
    }

    /**
     * Función genérica para ejecutar una validación y cambiar el estilo del control.
     * @param control El control a validar.
     * @param validator La lógica de validación (un Runnable que lanza IllegalArgumentException si falla).
     */
    private void validarCampo(Control control, Runnable validator) {
        try {
            validator.run();
            setValidationStyle(control, true, null); // Éxito
        } catch (IllegalArgumentException e) {
            setValidationStyle(control, false, e.getMessage()); // Error
        }
    }

    /**
     * Aplica el estilo de validación (borde rojo/normal) al control.
     * @param control El control (TextField/ComboBox) a estilizar.
     * @param isValid Si la validación fue exitosa.
     * @param tooltipText El mensaje de error a mostrar en el tooltip (solo si es inválido).
     */
    private void setValidationStyle(Control control, boolean isValid, String tooltipText) {
        if (!isValid) {
            // Estilo para indicar error (borde rojo)
            control.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            // Aquí podrías añadir un Tooltip con el tooltipText si lo deseas.
        } else {
            // Estilo normal (eliminar borde rojo)
            control.setStyle("");
        }
    }

    @Override
    public void receiveData(Repuesto data) {
        if (data != null) {
            viewModel.poblarParaModificacion(data);
        }
    }

    @Override
    public Optional<Repuesto> getResult() {
        return Optional.ofNullable(this.resultado);
    }

    @FXML
    private void nuevaMarca() {
        String nombreMarca = SimpleDialogs.nombreMarcaRepuesto();
        if (nombreMarca == null) return;
        try {
            viewModel.crearNuevaMarca(nombreMarca);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Crear nueva marca de repuestos", "Ha ocurrido un error inesperado.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarRepuesto(ActionEvent event) {
        if (!SimpleDialogs.confirmacion("Guardar repuesto",
                "¿Está seguro que desea guardar el repuesto?")) return;

        //        resetearEstilosDeError();
        try {
            this.resultado = viewModel.guardarRepuesto();
            NotificationHelper.mostrarExito("Guardar repuesto", "Se a guardado con éxito el repuesto: " +
                    viewModel.nombreProductoProperty().getValue());
            cerrar(event);
        } catch (IllegalArgumentException | DuplicateProductException e) {
            NotificationHelper.mostrarAdvertencia("Error de validación", e.getMessage());
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Error de Base de Datos", "Ocurrió un error al intentar guardar el repuesto.");
            e.printStackTrace();
        }
    }

    /**
     * Restablece los estilos de todos los campos que puedan tener error.
     */
    private void resetearEstilosDeError() {
        setValidationStyle(tfCodBarra, true, null);
        setValidationStyle(tfNombre, true, null);
        setValidationStyle(tfPrecio, true, null);
        setValidationStyle(tfCantidadStock, true, null);
        setValidationStyle(tfCantidadStockMin, true, null);
        setValidationStyle(tfLote, true, null);
        setValidationStyle(tfObservaciones, true, null);
        setValidationStyle(comboMarcas, true, null);
        setValidationStyle(comboUniMedidas, true, null);
        setValidationStyle(comboUbicaciones, true, null);
    }

    @FXML
    private void cerrar(ActionEvent event) {
        this.viewModel.limpiar();
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }
}