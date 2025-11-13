package SPRService.SPRService.controllers;

import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.celdas.ItemDetalleRetiroViewModel;
import SPRService.SPRService.viewModels.celdas.ItemDetalleViewModel;
import SPRService.SPRService.viewModels.celdas.ItemTrabajoViewModel;
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
import java.util.stream.Collectors;

public class CargarServiceController implements Initializable {

    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();

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
    private ListView<ItemDetalleViewModel> lvDetalles;
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
        configControles();
    }

    @FXML
    private void agregarTrabajo() {
        String detalle;
        BigDecimal precio;
        try {
            detalle = ManejadorInputs.textoGenerico(tfTrabajo.getText(), true,
                    "Agregar trabajo", 100);
            precio = ManejadorInputs.dinero(tfPrecioTrabajo.getText(), true, false);
            ItemTrabajoViewModel itvm = new ItemTrabajoViewModel(new Trabajo(null, detalle, precio));
            //todo: decidir si conviene o no validad repetido en este caso
//            for (ItemDetalleViewModel i : this.items) {
//                if (i.equals(itvm)) {
//                    Alertas.aviso("Agregar trabajo", "Ya has cargado el trabajo: " + detalle);
//                    return;
//                }
//            }
            items.addFirst(itvm);
            agregarTotal(precio);
            tfTrabajo.setText("");
            tfPrecioTrabajo.setText("");
        } catch (RuntimeException e) {
            Alertas.aviso("Agregar trabajo", e.getMessage());
        }
    }

    @FXML
    private void irAgregarRepuesto() {
        Optional<DetalleRetiro> result = navigator.openModal(Views.AGREGAR_REPUESTO_SERVICE,
                "Agregar repuesto", obtenerDetalles());
        if (result.isPresent()) {
            items.addFirst(new ItemDetalleRetiroViewModel(result.get()));
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

    private boolean validar() {
        if (this.cliente == null) {
            Alertas.aviso("Cargar service", "Se debe asociar un cliente para el service.");
            return false;
        }
        if (this.vehiculo == null) {
            Alertas.aviso("Cargar service", "Se debe asociar un vehículo para el service.");
            return false;
        }
        if (this.items.isEmpty()) {
            Alertas.aviso("Cargar service", "Deben haber repuestos o trabajos" +
                    " asignados para poder cargar el service.");
            return false;
        }
        return true;
    }

    @FXML
    private void cargarService(ActionEvent event) {
        if (!validar()) return;
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
            if (obtenerDetalles().isEmpty()) {
                orden.setNotaRetiro(null);
            } else {
                orden.setNotaRetiro(new NotaRetiro(null, obtenerDetalles()));
            }
            if (obtenerTrabajos().isEmpty()) {
                Alertas.aviso("Cargar service", "Debe agregar al menos 1 trabajo para " +
                        "cargar el service.");
                return;
            }
            orden.agregarTrabajos(obtenerTrabajos());

            Service service = new Service(LocalDateTime.now().plusDays(2), cbPrioridad.getValue(), this.cliente,
                    orden);

            if (!Alertas.confirmacion("Cargar service", "¿Está seguro que desea cargar?")) return;
            serviceServ.cargarService(service);
            Alertas.exito("Cargar service", "Se ha cargado el service con éxito.");
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } catch (IllegalArgumentException e) {
            Alertas.aviso("Cargar service", e.getMessage());
        } catch (RuntimeException e) {
            Alertas.error("Cargar service", e.getMessage());
            e.printStackTrace();
        }
    }

    private void configControles() {
        lvDetalles.setItems(items);
        lvDetalles.setCellFactory(new ItemCellFactory(this::eliminarItem));

        cbPrioridad.getItems().setAll(PrioridadService.values());
        cbPrioridad.getSelectionModel().select(2);
        sliCombustible.setValue(50);
        configCamposTexto();

        String css = getClass().getResource("/styles/celdasDetalles.css").toExternalForm();
        lvDetalles.getStylesheets().add(css);
    }

    private void eliminarItem(ItemDetalleViewModel item) {
        this.items.remove(item);
        restarTotal(item.getSubTotal());
    }

    private void configCamposTexto() {
        valSupp = new ValidationSupport();
        valSupp.registerValidator(tfTrabajo, true,
                Validator.createEmptyValidator("El campo del detalle de trabajo no puede quedar vacío.",
                        Severity.WARNING));
        valSupp.registerValidator(tfPrecioTrabajo, Validator.createRegexValidator(
                "Formato inválido", "^\\d{1,3}(,\\d{3})*(\\.\\d{1,2})?$|^\\d+(\\.\\d{1,2})?$",
                Severity.WARNING
        ));
        valSupp.registerValidator(tfKilometros, Validator.createRegexValidator(
                "Formato inválido", "^\\d{1,7}$", Severity.WARNING
        ));
    }

    private void agregarTotal(BigDecimal b) {
        totalService = totalService.add(b);
        lblTotal.setText("$ " + totalService);
    }

    private void restarTotal(BigDecimal b) {
        totalService = totalService.subtract(b);
        lblTotal.setText("$ " + totalService);
    }

    private List<DetalleRetiro> obtenerDetalles() {
        List<ItemDetalleRetiroViewModel> items = this.items.stream().filter(i -> i instanceof ItemDetalleRetiroViewModel)
                .map(i -> (ItemDetalleRetiroViewModel) i)
                .toList();
        if (!items.isEmpty()) {
            List<DetalleRetiro> detalles = new ArrayList<>();
            for (ItemDetalleRetiroViewModel i : items) {
                detalles.add(i.getDetalleRetiro());
            }
            return detalles;
        }
        return new ArrayList<>();
    }

    private Set<Trabajo> obtenerTrabajos() {
        Set<ItemTrabajoViewModel> items = this.items.stream().filter(i -> i instanceof ItemTrabajoViewModel)
                .map(i -> (ItemTrabajoViewModel) i)
                .collect(Collectors.toSet());
        if (!items.isEmpty()) {
            Set<Trabajo> trabajos = new HashSet<>();
            for (ItemTrabajoViewModel i : items) {
                trabajos.add(i.getTrabajo());
            }
            return trabajos;
        }
        return new HashSet<>();
    }
}
