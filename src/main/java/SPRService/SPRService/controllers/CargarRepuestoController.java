package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Ubicacion;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CargarRepuestoController implements Initializable, DataReceiver<Repuesto>, ModalController<Repuesto> {

    private final CargaRepuestoViewModel viewModel;
    private Repuesto resultado;

    @FXML
    private TextField tfCodBarra, tfNombre, tfPrecio, tfCantidadStock, tfCantidadStockMin, tfLote, tfObservaciones;
    @FXML
    private ComboBox<MarcaRepuesto> comboMarcas;
    @FXML
    private ComboBox<String> comboUniMedidas;
    @FXML
    private ComboBox<Ubicacion> comboUbicaciones;
    @FXML
    private Button btnReactivar, btnGuardarRepuesto;

    @Inject
    public CargarRepuestoController(CargaRepuestoViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.inicializar();
        bindControls();
        configurarValidacionesDeColor();

        Platform.runLater(() -> {
            Stage s = (Stage) tfCodBarra.getScene().getWindow();
            s.setOnCloseRequest(event -> this.viewModel.limpiar());
        });
    }

    private void bindControls() {
        tfCodBarra.textProperty().bindBidirectional(viewModel.codBarrasProperty());
        tfNombre.textProperty().bindBidirectional(viewModel.nombreProductoProperty());
        tfLote.textProperty().bindBidirectional(viewModel.loteProperty());
        tfObservaciones.textProperty().bindBidirectional(viewModel.observacionesProperty());
        tfCantidadStock.textProperty().bindBidirectional(viewModel.cantidadExistenteProperty());
        tfCantidadStockMin.textProperty().bindBidirectional(viewModel.cantidadMinimaProperty());
        tfPrecio.textProperty().bindBidirectional(viewModel.precioProperty());

        comboMarcas.setItems(viewModel.getMarcasDisponibles());
        comboMarcas.valueProperty().bindBidirectional(viewModel.marcaSeleccionadaProperty());

        comboUniMedidas.setItems(viewModel.getUnidadesDeMedida());
        comboUniMedidas.valueProperty().bindBidirectional(viewModel.uniMedidaSeleccionadaProperty());

        comboUbicaciones.setItems(viewModel.getUbicacionesDisponibles());
        comboUbicaciones.valueProperty().bindBidirectional(viewModel.ubicacionSeleccionadaProperty());

        // Converter para mostrar solo el nombre de la ubicación
        comboUbicaciones.setConverter(new StringConverter<Ubicacion>() {
            @Override
            public String toString(Ubicacion ubicacion) {
                return (ubicacion != null) ? ubicacion.getUbicacion() : null;
            }
            @Override
            public Ubicacion fromString(String string) {
                return comboUbicaciones.getItems().stream()
                        .filter(u -> u.getUbicacion().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void configurarValidacionesDeColor() {
        tfCodBarra.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfCodBarra, () -> ManejadorInputs.codBarras(tfCodBarra.getText(), true)); });
        tfNombre.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfNombre, () -> ManejadorInputs.textoGenerico(tfNombre.getText(), true, "Nombre", 60)); });
        tfPrecio.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfPrecio, () -> ManejadorInputs.dinero(tfPrecio.getText(), true, false)); });
        tfCantidadStock.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfCantidadStock, () -> ManejadorInputs.cantidadStock(tfCantidadStock.getText(), true)); });
        tfCantidadStockMin.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfCantidadStockMin, () -> ManejadorInputs.cantidadStock(tfCantidadStockMin.getText(), true)); });

        tfLote.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfLote, () -> ManejadorInputs.textoGenerico(tfLote.getText(), false, null, 40)); });
        tfObservaciones.focusedProperty().addListener((obs, ov, nv) -> { if (!nv) validarCampo(tfObservaciones, () -> ManejadorInputs.textoGenerico(tfObservaciones.getText(), false, null, 100)); });

        comboMarcas.valueProperty().addListener((obs, ov, nv) -> setValidationStyle(comboMarcas, nv != null, "Seleccione marca."));
        comboUbicaciones.valueProperty().addListener((obs, ov, nv) -> setValidationStyle(comboUbicaciones, nv != null, "Seleccione ubicación."));
    }

    private void validarCampo(Control control, Runnable validator) {
        try { validator.run(); setValidationStyle(control, true, null); }
        catch (IllegalArgumentException e) { setValidationStyle(control, false, e.getMessage()); }
    }

    private void setValidationStyle(Control control, boolean isValid, String tooltipText) {
        if (!isValid) control.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        else control.setStyle("");
    }

    /**
     * Solo se ejecuta cuando se modifica un repuesto.
     */
    @Override
    public void receiveData(Repuesto data) {
        if (data != null) {
            viewModel.poblarParaModificacion(data);

            btnReactivar.setVisible(true);
            if (!data.getActivo()) {
                btnGuardarRepuesto.setDisable(true);
                btnReactivar.setDisable(false);
            }
        }
    }

    @Override
    public Optional<Repuesto> getResult() {
        return Optional.ofNullable(this.resultado);
    }

    @FXML
    private void nuevaMarca() {
        try {
            String nombreMarca = SimpleDialogs.nombreMarcaRepuesto();
            if (nombreMarca == null) return;
            viewModel.crearNuevaMarca(nombreMarca);
        } catch (Exception e) {
            NotificationHelper.mostrarAdvertencia("Nueva Marca", e.getMessage());
        }
    }

    @FXML
    private void nuevaUbicacion() {
        String nombreUbicacion = SimpleDialogs.nombreUbicacion();
        if (nombreUbicacion == null) return;
        viewModel.crearNuevaUbicacion(nombreUbicacion);
    }

    @FXML
    private void cargarRepuesto(ActionEvent event) {
        if (!SimpleDialogs.confirmacion("Guardar repuesto", "¿Está seguro que desea guardar el repuesto?")) return;
        try {
            viewModel.guardarRepuesto().ifPresent(r -> this.resultado = r);
            NotificationHelper.mostrarExito("Guardar", "Repuesto guardado con éxito.");
            cerrar(event);
        } catch (IllegalArgumentException | DuplicateProductException e) {
            NotificationHelper.mostrarAdvertencia("Validación", e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            NotificationHelper.mostrarError("Error BD", "Error al guardar repuesto.");
        }
    }

    @FXML
    private void reactivarRepuesto() {
        if (!SimpleDialogs.confirmacion("Reactivar repuesto",
                "¿Está seguro que quiere reactivar el repuesto?")) return;

        try {
            viewModel.reactivar();

            btnReactivar.setDisable(true);
            btnGuardarRepuesto.setDisable(false);
            // No hace falta llevar datos, solo avisar que hubo cambios.
            this.resultado = new Repuesto();

            NotificationHelper.mostrarExito("Reactivar", "Repuesto reactivado con éxito.");
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Error al reactivar",
                    "Ha ocurrido un erro inesperado al reactivar el repuesto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        this.viewModel.limpiar();
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }
}