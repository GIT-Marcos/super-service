package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

//todo: bug no cambia cliente y vehículo al guardar
public class ModificarServiceController implements Initializable, DataReceiver<Service>, ModalController<Service> {

    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private ValidationSupport valSupp;
    private Service service;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Orden orden;
    private BigDecimal totalService = BigDecimal.ZERO;
    private NotaRetiro notaRetiro;
    private Set<Trabajo> trabajos;
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();
    private Service paraDevolver;

    @FXML
    private ComboBox<PrioridadService> cbPrioridad;
    @FXML
    private ComboBox<EstadoService> cbEstado;
    @FXML
    private DatePicker dpFechaEntrega, dpFechaCarga;
    @FXML
    private CustomTextField ctfDesc, ctfPrecio;
    @FXML
    private ListView<ItemDetalleViewModel> lista;
    @FXML
    private Label lblTotal, lblCliente, lblAuto;
    @FXML
    private ImageView imgMarca;
    @FXML
    private TextArea tfMotivos;
    @FXML
    private Button btnGuardar;

    @Inject
    public ModificarServiceController(ServiceServ serviceServ, AppCoordinator coordinator) {
        this.navigator = coordinator.getMainNavigator();
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();
        configCamposTexto();
    }

    @Override
    public void receiveData(Service data) {
        if (data != null) {
            this.service = data;
            this.orden = data.getOrden();
            this.cliente = data.getCliente();
            this.vehiculo = data.getOrden().getVehiculo();
            if (orden.getNotaRetiro() != null) {
                this.notaRetiro = orden.getNotaRetiro();
            } else {
                this.notaRetiro = new NotaRetiro(null, NotaRetiro.TipoUsoRetiro.SERVICE, new ArrayList<>());
                this.orden.setNotaRetiro(this.notaRetiro);
            }
            this.trabajos = orden.getTrabajos();
            this.totalService = service.getMontoTotal();
            cargarCampos();
        }
    }

    private void cargarCampos() {
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

        lblTotal.setText("TOTAL: $ " + service.getMontoTotal());
        lblCliente.setText(cliente.getNombre() + " " + cliente.getApellido() + " DNI: " + cliente.getDni());
        lblAuto.setText(vehiculo.getModeloVehiculo().getMarcaVehiculo().getNombreMarca() + " "
                + vehiculo.getModeloVehiculo().getNombreModelo());
        tfMotivos.setText(orden.getMotivoIngreso());

        InputStream stream = getClass().getResourceAsStream(
                vehiculo.getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
        if (stream != null) {
            Image img = new Image(stream);
            imgMarca.setImage(img);
        }

        EstadoService estado = service.getEstadoService();
        if (List.of(EstadoService.PAGADO, EstadoService.CANCELADO, EstadoService.PAGO_PENDIENTE).contains(estado)) {
            btnGuardar.setDisable(true);
            cbEstado.setDisable(true);
        }
    }

    @Override
    public Optional<Service> getResult() {
        return Optional.ofNullable(this.paraDevolver);
    }

    @FXML
    private void verOrden() {
        Optional<Service> result = navigator.openModal(Views.DETALLE_ORDEN, "Detalle de orden", this.service);
        result.ifPresent(this::receiveData);
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (!validar()) return;
        try {
            String motivo = ManejadorInputs.textoGenerico(tfMotivos.getText(), false,
                    "Motivo de ingreso", 500);

            this.orden.setMotivoIngreso(motivo);

            LocalDateTime fe = dpFechaEntrega.getValue() == null ? LocalDateTime.now().plusDays(1) :
                    dpFechaEntrega.getValue().atStartOfDay();
            service.setFechaEntrega(fe);

            if (!SimpleDialogs.confirmacion("Modificar service", "¿Está seguro que desea guardar el service?"))
                return;
            this.paraDevolver = serviceServ.modificarService(service);
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT)
                    .title("Modificar service")
                    .text("Se ha guardado el service con éxito.")
                    .showInformation();
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } catch (IllegalArgumentException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar service")
                    .text(e.getMessage())
                    .showWarning();
        } catch (RuntimeException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar service")
                    .text(e.getMessage())
                    .showError();
            e.printStackTrace();
        }
    }

    @FXML
    private void cambiarCliente() {
        Optional<Cliente> result = navigator.openModal(Views.AGREGAR_CLIENTE_SERVICE,
                "Asignar cliente a service", null);
        if (result.isPresent()) {
            cliente = result.get();
            lblCliente.setText(result.get().getNombre() + " " + result.get().getApellido() +
                    " DNI: " + result.get().getDni());
            lblCliente.setTextFill(Color.BLACK);
        }
    }

    @FXML
    private void cambiarVehiculo() {
        Optional<Vehiculo> result = navigator.openModal(Views.AGREGAR_VEHICULO_SERVICE,
                "Agregar vehículo al service", null);
        if (result.isPresent()) {
            vehiculo = result.get();
            InputStream stream = getClass().getResourceAsStream(
                    result.get().getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
            if (stream != null) {
                Image img = new Image(stream);
                imgMarca.setImage(img);
            }
            lblAuto.setText(result.get().getModeloVehiculo().getMarcaVehiculo().getNombreMarca() + " "
                    + result.get().getModeloVehiculo().getNombreModelo());
        }
    }

    @FXML
    private void agregarTrabajo() {
        String detalle;
        BigDecimal precio;
        try {
            detalle = ManejadorInputs.textoGenerico(ctfDesc.getText(), true,
                    "Agregar trabajo", 100);
            precio = ManejadorInputs.dinero(ctfPrecio.getText(), true, false);
            Trabajo t = new Trabajo(null, detalle, precio);
            if (!trabajos.contains(t)) {
                orden.agregarTrabajos(t);
                ItemTrabajoViewModel itvm = new ItemTrabajoViewModel(t);
                items.addFirst(itvm);
            }
            agregarTotal(precio);
            ctfDesc.setText("");
            ctfPrecio.setText("");
        } catch (RuntimeException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Agregar trabajo")
                    .text(e.getMessage())
                    .showWarning();
        }
    }

    @FXML
    private void agregarRepuesto() {
        Optional<DetalleRetiro> result = navigator.openModal(Views.AGREGAR_REPUESTO,
                "Agregar repuesto", this.notaRetiro.getDetallesRetiroList());
        result.ifPresent(detalle -> {
            orden.agregarRepuestos(List.of(detalle));
            items.addFirst(new ItemDetalleRetiroViewModel(detalle));
            agregarTotal(detalle.getSubTotal());
        });
    }

    private void eliminarItem(ItemDetalleViewModel item) {
        this.items.remove(item);

        if (item instanceof ItemDetalleRetiroViewModel) {
            DetalleRetiro detalle = ((ItemDetalleRetiroViewModel) item).getDetalleRetiro();
            orden.quitarRepuesto(detalle);

        } else if (item instanceof ItemTrabajoViewModel) {
            Trabajo trabajo = ((ItemTrabajoViewModel) item).getTrabajo();
            orden.quitarTrabajo(trabajo);
        }

        //todo: método que actualize precio
        restarTotal(item.getSubTotal());
    }

    private void configCamposTexto() {
        valSupp = new ValidationSupport();
        valSupp.registerValidator(ctfDesc, true,
                Validator.createEmptyValidator("El campo del detalle de trabajo no puede quedar vacío.",
                        Severity.WARNING));
        valSupp.registerValidator(ctfPrecio, Validator.createRegexValidator(
                "Formato inválido", "^\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?$|^\\d+(\\.\\d{1,2})?$",
                Severity.WARNING
        ));
    }

    private void agregarTotal(BigDecimal b) {
        totalService = totalService.add(b);
        lblTotal.setText("TOTAL: $ " + totalService);
    }

    private void restarTotal(BigDecimal b) {
        totalService = totalService.subtract(b);
        lblTotal.setText("TOTAL: $ " + totalService);
    }

    private boolean validar() {
        if (this.cliente == null) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar service")
                    .text("Se debe asociar un cliente para el service.")
                    .showWarning();
            return false;
        }
        if (this.vehiculo == null) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar service")
                    .text("Se debe asociar un vehículo para el service.")
                    .showWarning();
            return false;
        }
        if (dpFechaEntrega.getValue() != null && dpFechaEntrega.getValue().isBefore(LocalDate.now())) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar service")
                    .text("La fecha de entrega ya ha pasado.")
                    .showWarning();
            return false;
        }
        if (trabajos.isEmpty()) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .title("Modificar service")
                    .text("Debe agregar al menos 1 trabajo para modificar el service.")
                    .showWarning();
            return false;
        }
        return true;
    }

    private void configurarControles() {
        dpFechaEntrega.setConverter(new SafeLocalDateConverter());
        lista.setItems(items);
        lista.setCellFactory(new ItemCellFactory(this::eliminarItem));

        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        lista.getStylesheets().add(css);

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
    }
}
