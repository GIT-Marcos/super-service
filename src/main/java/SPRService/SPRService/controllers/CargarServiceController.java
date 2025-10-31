package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaTrabajo;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.DetalleRepuestoServiceViewModel;
import SPRService.SPRService.viewModels.TrabajoViewModelRepuesto;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class CargarServiceController implements Initializable {

    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private Set<Trabajo> trabajos = new HashSet<>();
    private List<DetalleRetiro> detalleRetiros = new ArrayList<>();
    private final ObservableList<TrabajoViewModelRepuesto> obsListTrabajos = FXCollections.observableArrayList();
    private ValidationSupport valSupp;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Orden orden;
    private BigDecimal totalService = BigDecimal.ZERO;

    @FXML
    private CustomTextField tfTrabajo, tfPrecioTrabajo, tfKilometros;
    @FXML
    private TextArea tfMotivoIngreso, tfInventario, tfObservaciones;
    @FXML
    private ListView<TrabajoViewModelRepuesto> lvDetalles;
    @FXML
    private Label lblCliente, lblVehiculo, lblTotal;
    @FXML
    private ImageView imgLogo;
    @FXML
    private Slider sliCombustible;
    @FXML
    private ComboBox<PrioridadService> cbPrioridad;

    @Inject
    public CargarServiceController(AppCoordinator coordinator, ServiceServ serviceServ) {
        this.navigator = coordinator.getMainNavigator();
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvDetalles.setCellFactory(param -> new CeldaTrabajo(
                i -> {
                    restarTotal(i.getSubTotal());
                }));
        lvDetalles.setItems(obsListTrabajos);

        valSupp = new ValidationSupport();
        valSupp.registerValidator(tfTrabajo, true,
                Validator.createEmptyValidator("El campo del detalle de trabajo no puede quedar vacío."));
        valSupp.registerValidator(tfPrecioTrabajo, Validator.createRegexValidator(
                "Formato inválido", "^\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?$|^\\d+(\\.\\d{1,2})?$",
                Severity.WARNING
        ));
        valSupp.registerValidator(tfKilometros, Validator.createRegexValidator(
                "Formato inválido", "^\\d{1,7}$\n", Severity.WARNING
        ));

        cbPrioridad.getItems().setAll(PrioridadService.values());
        cbPrioridad.getSelectionModel().selectFirst();
    }

    @FXML
    private void agregarTrabajo() {
        String detalle;
        BigDecimal precio;
        try {
            detalle = ManejadorInputs.textoGenerico(tfTrabajo.getText(), true,
                    "Agregar trabajo", 100);
            precio = ManejadorInputs.dinero(tfPrecioTrabajo.getText(), true, false);
            boolean seRepite = obsListTrabajos.stream().anyMatch(
                    d -> d.getDescripcion().equals(detalle));
            if (seRepite) {
                Alertas.aviso("Agregar trabajo", "Ya has cargado el trabajo: " + detalle);
                return;
            }
            obsListTrabajos.add(new TrabajoViewModelRepuesto(detalle, precio));
            agregarTotal(precio);
            trabajos.add(new Trabajo(null, detalle, precio));
            tfTrabajo.setText("");
            tfPrecioTrabajo.setText("");
        } catch (RuntimeException e) {
            Alertas.aviso("Agregar trabajo", e.getMessage());
        }
    }

    @FXML
    private void irAgregarRepuesto() {
        Optional<DetalleRetiro> result = navigator.openModal(Views.AGREGAR_REPUESTO_SERVICE,
                "Agregar repuesto", null);
        if (result.isPresent()) {
            detalleRetiros.add(result.get());
            obsListTrabajos.add(new DetalleRepuestoServiceViewModel(result.get()));
            agregarTotal(result.get().getSubTotal());
        }
    }

    @FXML
    private void asignarCliente() {
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
    private void asignarVehiculo() {
        Optional<Vehiculo> result = navigator.openModal(Views.AGREGAR_VEHICULO_SERVICE,
                "Agregar vehículo al service", null);
        if (result.isPresent()) {
            vehiculo = result.get();
            InputStream stream = getClass().getResourceAsStream(
                    result.get().getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
            if (stream != null) {
                Image img = new Image(stream);
                imgLogo.setImage(img);
            }
            lblVehiculo.setText(result.get().getModeloVehiculo().getMarcaVehiculo().getNombreMarca() + " "
                    + result.get().getModeloVehiculo().getNombreModelo());
            lblVehiculo.setTextFill(Color.BLACK);
        }
    }

    @FXML
    private void cargarService(ActionEvent event) {
        try {
            String motivo = ManejadorInputs.textoGenerico(tfMotivoIngreso.getText(), false,
                    "Motivo de ingreso", 500);
            Integer kilometraje = ManejadorInputs.kilometraje(tfKilometros.getText(), true);
            Integer combustible = (int) Math.round(sliCombustible.getValue());
            String inventario = ManejadorInputs.textoGenerico(tfInventario.getText(), false,
                    "Inventario de vehículo", 500);
            String observaciones = ManejadorInputs.textoGenerico(tfObservaciones.getText(), false,
                    "Observaciones del vehículo", 500);
            EstadoIngreso estadoIngreso = new EstadoIngreso(null, observaciones, inventario, kilometraje,
                    combustible);

            orden = new Orden();
            orden.setId(null);
            orden.setMotivoIngreso(motivo);
            orden.setInformeTecnico(null);
            orden.setEstadoIngreso(estadoIngreso);
            orden.setVehiculo(vehiculo);
            orden.setNotaRetiro(new NotaRetiro(null, detalleRetiros));
            orden.agregarTrabajos(trabajos);

            Service service = new Service();
            service.setId(null);
            service.setFechaEntrega(LocalDateTime.now());
            service.setFechaCarga(LocalDateTime.now());
            service.setEstadoService(EstadoService.PENDIENTE);
            service.setPrioridad(cbPrioridad.getValue());
            service.asignarCliente(this.cliente);
            service.asignarOrden(orden);

            serviceServ.cargarService(service);
            Alertas.exito("Cargar service", "Se ha cargado el service con éxito.");
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } catch (RuntimeException e) {
            Alertas.error("Cargar service", e.getMessage());
        }
    }

    private void agregarTotal(BigDecimal b) {
        totalService = totalService.add(b);
        lblTotal.setText("$ " + totalService);
    }

    private void restarTotal(BigDecimal b) {
        totalService = totalService.subtract(b);
        lblTotal.setText("$ " + totalService);
    }
}
