package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaVehiculo;
import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.VehiculoVM;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgregarVehiculoServiceController implements Initializable, ModalController<Vehiculo> {

    private final VehiculoServ vehiculoServ;
    private final Navigator navigator;
    private Vehiculo vehiculoSeleccionado;
    private ObservableList<Vehiculo> obsListVehiculo = FXCollections.observableArrayList();
    private FilteredList<Vehiculo> filteredList;

    @FXML
    private ListView<Vehiculo> lvVehiculos;
    @FXML
    private TextField tfPatente;

    @Inject
    public AgregarVehiculoServiceController(VehiculoServ vehiculoServ, AppCoordinator coordinator) {
        this.vehiculoServ = vehiculoServ;
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();
        filteredList = new FilteredList<>(obsListVehiculo, p -> true);
        lvVehiculos.setItems(obsListVehiculo);
        lvVehiculos.setCellFactory(cell -> new CeldaVehiculo());
        obsListVehiculo.addAll(vehiculoServ.verTodosActivos());

        String css = getClass().getResource("/styles/celdaVehiculo.css").toExternalForm();
        lvVehiculos.getStylesheets().add(css);
    }

    @Override
    public Optional<Vehiculo> getResult() {
        return Optional.ofNullable(this.vehiculoSeleccionado);
    }

    @FXML
    private void cargarNuevoVehiculo() {
        Optional<VehiculoVM> result = navigator.openModal(Views.CARGAR_VEHICULO,
                "Cargar nuevo vehículo", null);
        if (result.isPresent()) {
            obsListVehiculo.addFirst(result.get().obtenerEntidadActualizada());
            tfPatente.setText(result.get().obtenerEntidadActualizada().getPatente());
            lvVehiculos.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void asignarVehiculo() {
        this.vehiculoSeleccionado = lvVehiculos.getSelectionModel().getSelectedItem();
        if (this.vehiculoSeleccionado == null) {
            Alertas.aviso("Asignar vehículo",
                    "Debe seleccionar un vehículo de la lista para asignalo al service.");
            return;
        }
        Stage s = (Stage) lvVehiculos.getScene().getWindow();
        s.close();
    }

    private void textFieldListener() {
        tfPatente.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase(Locale.ROOT);
            filteredList.setPredicate(u -> {
                if (q.isEmpty()) {
                    return true;
                }
                String patente = u.getPatente().toLowerCase(Locale.ROOT);
                String modelo = u.getModeloVehiculo().getNombreModelo();
                String marca = u.getModeloVehiculo().getMarcaVehiculo().getNombreMarca();
                return patente.contains(q) || modelo.contains(q) || marca.contains(q);
            });
        });
    }
}
