package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.google.inject.Inject;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalleOrdenController implements Initializable, DataReceiver<Service>, ModalController<Service> {

    private final ServiceServ serviceServ;
    private Service service;
    private Orden orden;
    private Service paraDevolver;
    private EstadoIngreso estadoIngreso;
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();

    @FXML private ComboBox<PrioridadService> cbPrioridad;
    @FXML private ComboBox<EstadoService> cbEstado;
    @FXML private DatePicker dpFechaCarga, dpFechaEntrega;
    @FXML private ListView<ItemDetalleViewModel> lista;
    @FXML private TextArea tfMotivo, tfObservaciones, tfInventario;
    @FXML private Label lblModelo, lblKilometraje, lblCombustible;
    @FXML private ImageView imgLogo;
    @FXML private Button btnGuardar;

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

        if (service.getEstadoService().equals(EstadoService.PAGADO) ||
                service.getEstadoService().equals(EstadoService.CANCELADO)) {
            btnGuardar.setDisable(true);
            cbEstado.setDisable(true);
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (estaPagado()) return;
        if (!validar()) return;
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

            if (!Alertas.confirmacion("Modificar orden", "¿Está seguro que desea guardar la orden?"))
                return;
            this.paraDevolver = serviceServ.modificarService(service);

            Alertas.exito("Modificar orden", "Se ha guardado la orden con éxito.");
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } catch (IllegalArgumentException e) {
            Alertas.aviso("Modificar orden", e.getMessage());
        } catch (RuntimeException e) {
            Alertas.error("Modificar orden", e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean estaPagado() {
        if (service.getEstadoService() == EstadoService.PAGADO) {
            Alertas.error("Guardar Orden", "No es posible modificar services que ya han sido pagados.");
            return true;
        }
        if (cbEstado.getValue() == EstadoService.PAGADO) {
            Alertas.error("Guardar Orden", "No es posible guardar un service con estado: 'Pagado'.");
            return true;
        }
        if (cbEstado.getValue() == EstadoService.CANCELADO) {
            Alertas.error("Guardar orden", "No es posible guardar un service con estado: 'Cancelado'.");
            return true;
        }
        return false;
    }

    private boolean validar() {
        if (this.service.getCliente() == null) {
            Alertas.aviso("Guardar orden", "Se debe asociar un cliente para el service.");
            return false;
        }
        if (this.orden.getVehiculo() == null) {
            Alertas.aviso("Guardar orden", "Se debe asociar un vehículo para el service.");
            return false;
        }
        if (this.items.isEmpty()) {
            Alertas.aviso("Guardar orden", "Deben haber repuestos o trabajos" +
                    " asignados para poder cargar el service.");
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
