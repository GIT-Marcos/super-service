package SPRService.SPRService.controllers;

import SPRService.SPRService.viewModels.VehiculoVM;
import SPRService.SPRService.components.CeldaMarcaVehiculo;
import SPRService.SPRService.entities.MarcaVehiculo;
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

public class StepDatosMarcaController implements Initializable, WizardStepController {

    private final VehiculoVM vehiculoVM;
    private FilteredList<MarcaVehiculo> filteredList;

    @FXML
    private TextField tfBuscar;
    @FXML
    private ListView<MarcaVehiculo> listViewMarcas;

    @Inject
    public StepDatosMarcaController(WizardStateProvider wsp) {
        this.vehiculoVM = wsp.getViewModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();
        if (vehiculoVM.getMarcasDisponibles().isEmpty()) {
            vehiculoVM.cargarListasOpciones();
        }
        // NUNCA MODIFICAR LA FilteredList DIRECTAMENTE
        // 'p -> true' predicado por defecto que muestra todos
        filteredList = new FilteredList<>(vehiculoVM.getMarcasDisponibles(), p -> true);
        listViewMarcas.setItems(this.filteredList);

        // Vía 1: La Vista actualiza el ViewModel
        listViewMarcas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Usamos un 'if' para evitar bucles infinitos si el cambio vino del ViewModel
            if (newVal != vehiculoVM.marcaSeleccionadaProperty().get()) {
                vehiculoVM.marcaSeleccionadaProperty().set(newVal);
            }
        });

        // Vía 2: El ViewModel actualiza la Vista
        vehiculoVM.marcaSeleccionadaProperty().addListener((obs, oldVal, newVal) -> {
            // Usamos un 'if' para evitar bucles infinitos si el cambio vino de la Vista
            if (newVal != listViewMarcas.getSelectionModel().getSelectedItem()) {
                listViewMarcas.getSelectionModel().select(newVal);
            }
        });

        listViewMarcas.setCellFactory(param -> new CeldaMarcaVehiculo());
    }

    @Override
    public BooleanSupplier getValidador() {
        return () -> listViewMarcas.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public void mostrarErrores() {
        Alertas.aviso("Selección de marca", "Debe seleccionar una marca para el vehículo.");
    }

    private void textFieldListener() {
        tfBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase(Locale.ROOT);
            filteredList.setPredicate(u ->
                    u.getNombreMarca().toLowerCase(Locale.ROOT).contains(q)
            );
        });
    }


}
