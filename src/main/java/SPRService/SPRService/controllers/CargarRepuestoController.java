package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.MarcaRepuesto;
import SPRService.SPRService.exceptions.DuplicateProductException;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.viewModels.CargaRepuestoViewModel;
import com.google.inject.Inject;
import jakarta.persistence.PersistenceException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.util.alertas.Alertas;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

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
        configurarFormattersNumericos();
        bindControls();

        Platform.runLater(() -> {
            Stage s = (Stage) tfCodBarra.getScene().getWindow();
            s.setOnCloseRequest(event -> {
                this.viewModel.limpiar();
            });
        });
    }

    private void configurarFormattersNumericos() {
        UnaryOperator<TextFormatter.Change> filtroDecimal = change -> {
            String nuevoTexto = change.getText().replace(',', '.');
            change.setText(nuevoTexto);

            String textoCompleto = change.getControlNewText();
            if (textoCompleto.isEmpty() || textoCompleto.matches("\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        };

        // El TextFormatter actúa como guardián para prevenir texto inválido.
        tfCantidadStock.setTextFormatter(new TextFormatter<>(filtroDecimal));
        tfCantidadStockMin.setTextFormatter(new TextFormatter<>(filtroDecimal));
        tfPrecio.setTextFormatter(new TextFormatter<>(filtroDecimal));
    }

    private void bindControls() {
        // ... otros bindings ...
        tfCodBarra.textProperty().bindBidirectional(viewModel.codBarrasProperty());
        tfNombre.textProperty().bindBidirectional(viewModel.nombreProductoProperty());
        tfLote.textProperty().bindBidirectional(viewModel.loteProperty());
        tfObservaciones.textProperty().bindBidirectional(viewModel.observacionesProperty());

        // --- BINDING PARA DOUBLE ---
        // Se enlaza el String del TextField con el Double del ViewModel.
        // Se usa .asObject() para que el compilador vea la propiedad como Property<Double>
        // y pueda hacer coincidir los tipos con el DoubleStringConverter.
        tfCantidadStock.textProperty().bindBidirectional(
                viewModel.cantidadExistenteProperty().asObject(), new DoubleStringConverter());

        tfCantidadStockMin.textProperty().bindBidirectional(
                viewModel.cantidadMinimaProperty().asObject(), new DoubleStringConverter());

        // --- BINDING PARA BIGDECIMAL ---
        // Aquí no se necesita .asObject() porque viewModel.precioProperty()
        // ya es de tipo ObjectProperty<BigDecimal>, que implementa Property<BigDecimal>.
        // Los tipos ya coinciden de forma natural.
        tfPrecio.textProperty().bindBidirectional(
                viewModel.precioProperty(), new BigDecimalStringConverter());

        // ... bindings para los ComboBox ...
        comboMarcas.setItems(viewModel.getMarcasDisponibles());
        comboMarcas.valueProperty().bindBidirectional(viewModel.marcaSeleccionadaProperty());
        comboUniMedidas.setItems(viewModel.getUnidadesDeMedida());
        comboUniMedidas.valueProperty().bindBidirectional(viewModel.uniMedidaSeleccionadaProperty());
        comboUbicaciones.setItems(viewModel.getUbicacionesDisponibles());
        comboUbicaciones.valueProperty().bindBidirectional(viewModel.ubicacionSeleccionadaProperty());
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
            Alertas.error("Crear nueva marca de repuestos", e.getMessage());
        }
    }

    @FXML
    private void cargarRepuesto(ActionEvent event) {
        boolean resultado = Alertas.confirmacion("Guardar repuesto",
                "¿Está seguro que desea guardar el repuesto?");
        if (!resultado) return;

        try {
            this.resultado = viewModel.guardarRepuesto();
            Alertas.exito("Guardar repuesto", "Se a guardado con éxito el repuesto: " +
                    viewModel.nombreProductoProperty().getValue());
            cerrar(event);
        } catch (IllegalArgumentException | DuplicateProductException e) {
            Alertas.aviso("Error de validación", e.getMessage());
        } catch (PersistenceException e) {
            Alertas.error("Error de Base de Datos", "Ocurrió un error al intentar guardar el repuesto.");
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
