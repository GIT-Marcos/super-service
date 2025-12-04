package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.google.inject.Inject;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalleOrdenController implements Initializable, DataReceiver<Service>, ModalController<Service> {

    private final ServiceServ serviceServ;
    private Service service;
    private Orden orden;
    private Service paraDevolver;
    private EstadoIngreso estadoIngreso;
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();

    @FXML
    private ComboBox<PrioridadService> cbPrioridad;
    @FXML
    private ComboBox<EstadoService> cbEstado;
    @FXML
    private DatePicker dpFechaCarga, dpFechaEntrega;
    @FXML
    private ListView<ItemDetalleViewModel> lista;
    @FXML
    private TextArea tfMotivo, tfObservaciones, tfInventario;
    @FXML
    private Label lblModelo, lblKilometraje, lblCombustible;
    @FXML
    private ImageView imgLogo;
    @FXML
    private Button btnGuardar;

    @Inject
    public DetalleOrdenController(ServiceServ serviceServ) {
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();
    }

    @Override
    public Optional<Service> getResult() {
        return Optional.ofNullable(this.paraDevolver);
    }

    @Override
    public void receiveData(Service data) {
        if (data != null) {
            this.service = data;
            this.orden = data.getOrden();
            this.estadoIngreso = data.getOrden().getEstadoIngreso();
            actualizarCampos();
        }
    }

    private void actualizarCampos() {
        cbPrioridad.getSelectionModel().select(service.getPrioridad());
        cbEstado.getSelectionModel().select(service.getEstadoService());
        dpFechaCarga.setValue(service.getFechaCarga().toLocalDate());
        dpFechaEntrega.setValue(service.getFechaEntrega().toLocalDate());

        items.clear();
        for (Trabajo t : orden.getTrabajos()) {
            items.add(new ItemTrabajoViewModel(t));
        }
        if (orden.getNotaRetiro() != null) {
            for (DetalleRetiro d : orden.getNotaRetiro().getDetallesRetiroList()) {
                items.add(new ItemDetalleRetiroViewModel(d));
            }
        }

        tfMotivo.setText(orden.getMotivoIngreso());
        tfObservaciones.setText(estadoIngreso.getObservaciones());
        tfInventario.setText(estadoIngreso.getInventario());

        lblCombustible.setText("Nivel de combustible: " + estadoIngreso.getCombustible() + "%");
        lblKilometraje.setText("Kilometraje: " + estadoIngreso.getKilometraje() + " Kmts.");
        lblModelo.setText(orden.getVehiculo().getModeloVehiculo().getMarcaVehiculo().getNombreMarca() + " "
                + orden.getVehiculo().getModeloVehiculo().getNombreModelo());
        InputStream stream = getClass().getResourceAsStream(
                orden.getVehiculo().getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
        if (stream != null) {
            Image img = new Image(stream);
            imgLogo.setImage(img);
        }

        EstadoService estado = service.getEstadoService();
        if (List.of(EstadoService.PAGADO, EstadoService.CANCELADO, EstadoService.PAGO_PENDIENTE).contains(estado)) {
            btnGuardar.setDisable(true);
            cbEstado.setDisable(true);
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (!validarFecha()) return;
        try {
            String motivo = ManejadorInputs.textoGenerico(tfMotivo.getText(), false,
                    "Motivo de ingreso", 500);
            String inventario = ManejadorInputs.textoGenerico(tfInventario.getText(), false,
                    "Inventario de vehículo", 500);
            String observaciones = ManejadorInputs.textoGenerico(tfObservaciones.getText(), false,
                    "Observaciones del vehículo", 500);
            estadoIngreso.setInventario(inventario);
            estadoIngreso.setObservaciones(observaciones);

            orden.setMotivoIngreso(motivo);

            LocalDateTime fe = dpFechaEntrega.getValue() == null ? LocalDateTime.now().plusDays(1) :
                    dpFechaEntrega.getValue().atStartOfDay();
            service.setFechaEntrega(fe);

            if (!SimpleDialogs.confirmacion("Modificar orden", "¿Está seguro que desea guardar la orden?"))
                return;
            this.paraDevolver = serviceServ.modificarService(service);
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT)
                    .title("Modificar orden")
                    .text("Se ha guardado la orden con éxito.")
                    .showInformation();
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } catch (IllegalArgumentException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar orden")
                    .text(e.getMessage())
                    .showWarning();
        } catch (RuntimeException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar orden")
                    .text(e.getMessage())
                    .showError();
            e.printStackTrace();
        }
    }

    private boolean validarFecha() {
        if (dpFechaEntrega.getValue() != null && dpFechaEntrega.getValue().isBefore(LocalDate.now())) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar orden")
                    .text("La fecha de entrega ya ha pasado.")
                    .showWarning();
            return false;
        }
        return true;
    }

    private void configurarControles() {
        cbEstado.getItems().addAll(EstadoService.values());
        cbPrioridad.getItems().addAll(PrioridadService.values());

        cbPrioridad.getItems().setAll(PrioridadService.values());
        cbEstado.getItems().setAll(EstadoService.values());
        cbEstado.getItems().remove(EstadoService.CANCELADO);
        cbEstado.getItems().remove(EstadoService.PAGADO);
        cbEstado.getItems().remove(EstadoService.PAGO_PENDIENTE);

        cbPrioridad.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                this.service.setPrioridad(newValue);
            }
        });

        cbEstado.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                this.service.setEstadoService(newValue);
            }
        });

        lista.setItems(items);
        lista.setCellFactory(new ItemCellFactory().setMostrarBotonEliminar(false));
        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        lista.getStylesheets().add(css);
    }
}
