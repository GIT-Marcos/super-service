package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaCliente;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.navigation.WizardStateProvider;
import SPRService.SPRService.navigation.WizardStepController;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.VehiculoVM;
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

public class StepClienteController implements Initializable, WizardStepController {

    private final VehiculoVM vehiculoVM;
    private FilteredList<Cliente> filteredList;

    @FXML
    private TextField tfDni;
    @FXML
    private ListView<Cliente> lvClientes;

    @Inject
    public StepClienteController(WizardStateProvider wsp) {
        this.vehiculoVM = wsp.getViewModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();
        if (vehiculoVM.getClientesDisponibles().isEmpty()) {
            vehiculoVM.cargarListasOpciones();
        }
        filteredList = new FilteredList<>(vehiculoVM.getClientesDisponibles(), p -> true);
        lvClientes.setItems(filteredList);
        lvClientes.setCellFactory(param -> new CeldaCliente());

        lvClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != vehiculoVM.clienteSeleccionadoProperty().get()) {
                        vehiculoVM.clienteSeleccionadoProperty().set(newVal);
                    }
                });

        vehiculoVM.clienteSeleccionadoProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != lvClientes.getSelectionModel().getSelectedItem()) {
                        lvClientes.getSelectionModel().select(newVal);
                    }
                });
    }

    @Override
    public BooleanSupplier getValidador() {
        return () -> lvClientes.getSelectionModel().getSelectedItem() != null;
    }

    @Override
    public void mostrarErrores() {
        Alertas.aviso("Seleccionar cliente", "Debe seleccionar el cliente al que pertenece el vehículo.");
    }

    @FXML
    private void cargarNuevo() {

    }

    private void textFieldListener() {
        tfDni.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase(Locale.ROOT);
            filteredList.setPredicate(u -> {
                if (q.isEmpty()) {
                    return true;
                }
                String dni = u.getDni().toLowerCase(Locale.ROOT);
                String nombre = u.getNombre();
                String apellido = u.getApellido();
                return dni.contains(q) || nombre.contains(q) || apellido.contains(q);
            });
        });
    }
}
