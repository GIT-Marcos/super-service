package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaCliente;
import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.alertas.NotificationHelper;
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

public class AgregarClienteServiceController implements Initializable, ModalController<Cliente> {

    private final ClienteServ clienteServ;
    private final Navigator navigator;
    private ObservableList<Cliente> obsListCliente = FXCollections.observableArrayList();
    private FilteredList<Cliente> filteredList;
    private Cliente clienteSeleccionado;

    @FXML
    private ListView<Cliente> lvClientes;
    @FXML
    private TextField tfDni;

    @Inject
    public AgregarClienteServiceController(ClienteServ clienteServ, AppCoordinator coordinator) {
        this.clienteServ = clienteServ;
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();
        configControles();
    }

    private void configControles() {
        filteredList = new FilteredList<>(obsListCliente, p -> true);
        lvClientes.setItems(filteredList);
        lvClientes.setCellFactory(cell -> new CeldaCliente());
        obsListCliente.setAll(clienteServ.verTodosActivos());

        String css = getClass().getResource("/styles/celdaCliente.css").toExternalForm();
        lvClientes.getStylesheets().add(css);
    }

    @Override
    public Optional<Cliente> getResult() {
        return Optional.ofNullable(this.clienteSeleccionado);
    }

    @FXML
    private void cargarNuevo() {
        Optional<Cliente> result = navigator.openModal(Views.CARGAR_CLIENTE, "Cargar nuevo cliente", null);
        if (result.isPresent()) {
            obsListCliente.addFirst(result.get());
            tfDni.setText(result.get().getDni());
            lvClientes.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void asignarCliente() {
        if (lvClientes.getSelectionModel().getSelectedItem() == null) {
            NotificationHelper.mostrarAdvertencia("Asignar cliente",
                    "Debe seleccionar un cliente para continuar.");
            return;
        }

        clienteServ.verOperacionesConVehiculos(lvClientes.getSelectionModel().getSelectedItem().getId())
                .ifPresent(cliente -> {
                    this.clienteSeleccionado = cliente;
                    Stage s = (Stage) lvClientes.getScene().getWindow();
                    s.close();
                });
    }

    private void textFieldListener() {
        tfDni.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = newVal == null ? "" : newVal.toLowerCase(Locale.ROOT);
            filteredList.setPredicate(u -> {
                if (q.isEmpty()) {
                    return true;
                }
                String dni = u.getDni().toLowerCase(Locale.ROOT);
                String nombre = u.getNombre().toLowerCase(Locale.ROOT);
                String apellido = u.getApellido().toLowerCase(Locale.ROOT);
                return dni.contains(q) || nombre.contains(q) || apellido.contains(q);
            });
        });
    }
}
