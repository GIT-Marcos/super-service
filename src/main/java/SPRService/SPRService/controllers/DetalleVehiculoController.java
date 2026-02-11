package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaOperacionUniversal;
import SPRService.SPRService.entities.Orden;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import SPRService.SPRService.viewModels.celdas.ItemServiceViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class DetalleVehiculoController implements Initializable, DataReceiver<Vehiculo> {

    private final VehiculoServ vehiculoServ;
    private final ServiceServ serviceServ;
    private final Navigator navigator;
    private Vehiculo vehiculo;
    private ObservableList<ItemOperacionViewModel> items = FXCollections.observableArrayList();

    @FXML
    private Label lblPatente, lblColor, lblNroChasis, lblNroMotor, lblMarca, lblModelo, lblAnio, lblCilindrada,
            lblFechaRegistro;
    @FXML
    private ImageView imgMarca;
    @FXML
    private ListView<ItemOperacionViewModel> lvServices;
    @FXML
    private Button btnNuevo;

    @Inject
    public DetalleVehiculoController(VehiculoServ vehiculoServ, ServiceServ serviceServ, AppCoordinator coordinator) {
        this.vehiculoServ = vehiculoServ;
        this.serviceServ = serviceServ;
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarLista();
        configPermisos();
    }

    private void configPermisos() {
        RolUsuario rol = SessionManager.getRolUsuario();
        if (rol == RolUsuario.OPERATIVO_TALLER) {
            btnNuevo.setDisable(true);
        }
    }

    @Override
    public void receiveData(Vehiculo data) {
        if (data != null) {
            this.vehiculo = data;
            cargarLabels(data);
            cargarLista(data);

            if (!data.getEstado()) btnNuevo.setDisable(true);
        }
    }

    @FXML
    private void nuevoService() {
        Optional<Service> result = navigator.openModal(Views.CARGAR_SERVICE, "Nuevo service para este vehículo",
                this.vehiculo);
        result.ifPresent(s -> {
            s.getOrden().asociarVehiculo(this.vehiculo);
            items.add(new ItemServiceViewModel(s));
        });
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void configurarLista() {
        lvServices.setItems(items);
        lvServices.setCellFactory(f -> new CeldaOperacionUniversal(this::detallesOperacion));
        String css = getClass().getResource("/styles/celda-operacion.css").toExternalForm();
        lvServices.getStylesheets().add(css);
    }

    private void detallesOperacion(ItemOperacionViewModel item) {
        if (item == null) return;

        if (item instanceof ItemServiceViewModel) {
            serviceServ.datosParaModificar(item.getCodigo())
                    .ifPresent(s -> {
                        Optional<Service> huboCambios = navigator.openModal(Views.MODIFICAR_SERVICE,
                                "Detalles del service", s);
                        huboCambios.ifPresent(sm -> recargarVista());
                    });
        }
    }

    private void recargarVista() {
        vehiculoServ.verDetalle(this.vehiculo.getId())
                .ifPresent(this::receiveData);
    }

    private void cargarLabels(Vehiculo data) {
        lblPatente.setText(data.getPatente());
        lblColor.setText(data.getColor());
        if (!data.getNroChasis().isEmpty()) lblNroChasis.setText(data.getNroChasis());
        if (!data.getNroMotor().isEmpty()) lblNroMotor.setText(data.getNroMotor());
        lblMarca.setText(data.getModeloVehiculo().getMarcaVehiculo().getNombreMarca());
        lblModelo.setText(data.getModeloVehiculo().getNombreModelo());
        lblAnio.setText(data.getModeloVehiculo().getAnio().toString());
        lblCilindrada.setText(data.getModeloVehiculo().getCilindrada() + "cc.");
        lblFechaRegistro.setText(data.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        InputStream stream = getClass().getResourceAsStream(data.getModeloVehiculo().getMarcaVehiculo().getRutaLogo());
        if (stream != null) {
            Image img = new Image(stream);
            imgMarca.setImage(img);
        }
    }

    private void cargarLista(Vehiculo data) {
        items.setAll(
                data.getOrdenes().stream().map(Orden::getService).map(ItemServiceViewModel::new).toList()
        );
    }
}
