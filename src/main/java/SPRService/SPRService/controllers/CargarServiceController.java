package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.TicketRetiroServiceDTO;
import SPRService.SPRService.components.ItemCellFactory;
import SPRService.SPRService.entities.*;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorTXT;
import SPRService.SPRService.util.generadores.Impresor;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CargarServiceController implements Initializable, ModalController<Service>, DataReceiver<Vehiculo> {

    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private ObservableList<ItemDetalleViewModel> items = FXCollections.observableArrayList();

    private ValidationSupport valSupp;
    private Cliente cliente;
    private Vehiculo vehiculo;
    private Orden orden;
    private Service service;
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
    private DatePicker dpFechaEntrega;
    @FXML
    private ImageView imgLogo;
    @FXML
    private Slider sliCombustible;
    @FXML
    private ComboBox<PrioridadService> cbPrioridad;
    @FXML
    private CheckBox cbRutaPredeterminada, cbImprimir;
    @FXML
    private Button btnCliente, btnVehiculo;

    @Inject
    public CargarServiceController(AppCoordinator coordinator, ServiceServ serviceServ) {
        this.navigator = coordinator.getMainNavigator();
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configControles();
    }

    @Override
    public Optional<Service> getResult() {
        return Optional.ofNullable(this.service);
    }


    @Override
    public void receiveData(Vehiculo data) {
        if (data != null) {
            cargarVehiculo(data);
            btnVehiculo.setDisable(true);
        }
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
            NotificationHelper.mostrarAdvertencia("Agregar trabajo", e.getMessage());
        }
    }

    @FXML
    private void irAgregarRepuesto() {
        Optional<DetalleRetiro> result = navigator.openModal(Views.AGREGAR_REPUESTO,
                "Agregar repuesto", obtenerDetalles());
        result.ifPresent(r -> {
            items.addFirst(new ItemDetalleRetiroViewModel(result.get()));
            agregarTotal(result.get().getSubTotal());
        });
    }

    @FXML
    private void asignarCliente() {
        Optional<Cliente> result = navigator.openModal(Views.AGREGAR_CLIENTE_SERVICE,
                "Asignar cliente a service", null);
        result.ifPresent(this::cargarCliente);
    }

    @FXML
    private void asignarVehiculo() {
        Optional<Vehiculo> result = navigator.openModal(Views.AGREGAR_VEHICULO_SERVICE,
                "Agregar vehículo al service", null);
        result.ifPresent(this::cargarVehiculo);
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

            this.cliente.asociarVehiculo(this.vehiculo);

            this.orden = new Orden(motivo, null, estadoIngreso);
            this.orden.asignarNota(new NotaRetiro(NotaRetiro.TipoUsoRetiro.SERVICE, obtenerDetalles()));
            this.orden.setVehiculo(vehiculo);
            this.orden.agregarTrabajos(obtenerTrabajos());

            //todo: hacer que tome la fecha del control datepicker
            Service service = new Service(LocalDateTime.now().plusDays(1), cbPrioridad.getValue());
            service.asignarCliente(this.cliente);
            service.asignarOrden(this.orden);

            if (!SimpleDialogs.confirmacion("Cargar service", "¿Está seguro que desea crear el service?")) return;
            this.service = serviceServ.cargarService(service);

            if (SimpleDialogs.confirmacion("Generar ticket de service", "¿Quiere generar un ticket?")) {
                gestionarTicket(event, this.service);
            }

            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
            NotificationHelper.mostrarExito("Nuevo service", "Se guardado el service con éxito.");
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Nuevo service", e.getMessage());
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Nuevo service", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarVehiculo(Vehiculo v) {
        this.vehiculo = v;
        InputStream stream = getClass().getResourceAsStream(
                v.getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
        if (stream != null) {
            Image img = new Image(stream);
            imgLogo.setImage(img);
        }
        lblVehiculo.setText(v.getModeloVehiculo().getMarcaVehiculo().getNombreMarca() + " "
                + v.getModeloVehiculo().getNombreModelo());
        lblVehiculo.setTextFill(Color.BLACK);
    }

    private void cargarCliente(Cliente c) {
        this.cliente = c;
        lblCliente.setText(c.getNombre() + " " + c.getApellido() +
                " DNI: " + c.getDni());
        lblCliente.setTextFill(Color.BLACK);
    }

    private void gestionarTicket(ActionEvent event, Service s) {
        File file;
        if (cbRutaPredeterminada.isSelected()) {
            file = new File("C:\\Users\\Usuario\\Desktop\\ticket-service.txt");
        } else {
            file = SimpleDialogs.selectorRuta(event, "Seleccione donde quiere guardar el ticket",
                    "ticket-service.txt",
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        }
        if (file == null) return;
        GeneradorTXT.generarTicketRetiroService(new TicketRetiroServiceDTO(s), file);

        if (cbImprimir.isSelected())
            Impresor.imprimirConSistema(file);
    }

    private boolean validar() {
        if (this.cliente == null) {
            NotificationHelper.mostrarAdvertencia("Nuevo service", "Se debe asignar un cliente para el service.");
            return false;
        }
        if (this.vehiculo == null) {
            NotificationHelper.mostrarAdvertencia("Nuevo service", "Se debe asignar un vehículo para el service.");
            return false;
        }
        if (obtenerTrabajos().isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Nuevo service", "Deben haber al menos 1 trabajo asignado para poder crear el service.");
            return false;
        }
        if (dpFechaEntrega.getValue() != null && dpFechaEntrega.getValue().isBefore(LocalDate.now())) {
            NotificationHelper.mostrarAdvertencia("Nuevo service", "La fecha de entrega ya ha pasado.");
            return false;
        }
        return true;
    }

    private void configControles() {
        dpFechaEntrega.setConverter(new SafeLocalDateConverter());

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
        lblTotal.setText("TOTAL: $ " + totalService);
    }

    private void restarTotal(BigDecimal b) {
        totalService = totalService.subtract(b);
        lblTotal.setText("TOTAL: $ " + totalService);
    }

    private Set<DetalleRetiro> obtenerDetalles() {
        List<ItemDetalleRetiroViewModel> items = this.items.stream().filter(i -> i instanceof ItemDetalleRetiroViewModel)
                .map(i -> (ItemDetalleRetiroViewModel) i)
                .toList();
        if (!items.isEmpty()) {
            Set<DetalleRetiro> detalles = new HashSet<>();
            for (ItemDetalleRetiroViewModel i : items) {
                detalles.add(i.getDetalleRetiro());
            }
            return detalles;
        }
        return new HashSet<>();
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
