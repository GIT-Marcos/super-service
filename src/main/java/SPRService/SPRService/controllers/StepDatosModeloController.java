package SPRService.SPRService.controllers;

import SPRService.SPRService.viewModels.VehiculoVM;
import SPRService.SPRService.components.CeldaModeloVehiculo;
import SPRService.SPRService.entities.ModeloVehiculo;
import SPRService.SPRService.navigation.WizardStateProvider;
import SPRService.SPRService.navigation.WizardStepController;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.BooleanSupplier;

public class StepDatosModeloController implements Initializable, WizardStepController {

    private final VehiculoVM vehiculoVM;
    private FilteredList<ModeloVehiculo> filteredList;

    @FXML
    private TextField tfBuscar;
    @FXML
    private ListView<ModeloVehiculo> listViewModelos;

    @Inject
    public StepDatosModeloController(WizardStateProvider wsp) {
        this.vehiculoVM = wsp.getViewModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();

        filteredList = new FilteredList<>(vehiculoVM.getModelosDisponibles(), p -> true);
        listViewModelos.setItems(filteredList);

        // Vía 1: La Vista actualiza el ViewModel
        listViewModelos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != vehiculoVM.modeloSeleccionadoProperty().get()) {
                vehiculoVM.modeloSeleccionadoProperty().set(newVal);
            }
        });

        // Vía 2: El ViewModel actualiza la Vista
        vehiculoVM.modeloSeleccionadoProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != listViewModelos.getSelectionModel().getSelectedItem()) {
                listViewModelos.getSelectionModel().select(newVal);
            }
        });

        listViewModelos.setCellFactory(param -> new CeldaModeloVehiculo());
    }

    @Override
    public BooleanSupplier getValidador() {
        return () -> listViewModelos.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public void mostrarErrores() {
        Alertas.aviso("Selección de modelo", "Debe seleccionar un modelo para el vehículo.");
    }

    private void textFieldListener() {
        tfBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase(Locale.ROOT);
            filteredList.setPredicate(u -> {
                if (q.isEmpty()) {
                    return true;
                }
                String nombreModelo = u.getNombreModelo().toLowerCase(Locale.ROOT);
                String anio = u.getAnio().toString();
                return nombreModelo.contains(q) || anio.contains(q);
            });
        });
    }
}
