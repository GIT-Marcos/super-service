package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.viewModels.tablas.ClienteViewModelTabla;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {

    private final ClienteServ clienteServ;
    private final Navigator navigator;
    private ObservableList<ClienteViewModelTabla> obsListClientes = FXCollections.observableArrayList();

    @FXML
    private TextField tfDNI;
    @FXML
    private TextField tfApellido;
    @FXML
    private TextField tfNombre;
    @FXML
    private TableView<ClienteViewModelTabla> tablaClientes;
    @FXML
    private TableColumn<ClienteViewModelTabla, String> colDNI;
    @FXML
    private TableColumn<ClienteViewModelTabla, String> colApellido;
    @FXML
    private TableColumn<ClienteViewModelTabla, String> colNombre;

    @Inject
    public ClienteController(ClienteServ clienteServ, AppCoordinator appCoordinator) {
        this.clienteServ = clienteServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        obsListClientes.setAll();
        configColumnas();
        llenarFilas(clienteServ.getAllActive());
    }

    @FXML
    private void todosLosClientes() {
        llenarFilas(clienteServ.getAllActive());
    }

    @FXML
    private void buscarConFiltros() {
        llenarFilas(clienteServ.filteredSearch(tfDNI.getText().strip(), tfApellido.getText().strip(),
                tfNombre.getText().strip()));
    }

    @FXML
    private void nuevoCliente() {
        Optional<Cliente> optional = navigator.openModal(Views.CARGAR_CLIENTE, "Cargar nuevo cliente", null);
        if (optional.isPresent()) obsListClientes.addFirst(new ClienteViewModelTabla(optional.get()));
    }

    @FXML
    private void modificar() {
        ClienteViewModelTabla cvmt = tablaClientes.getSelectionModel().getSelectedItem();
        if (cvmt == null) {
            Alertas.aviso("Modificar cliente", "Debe seleccionar un cliente para modificarlo.");
            return;
        }
        Optional<Cliente> optional = navigator.openModal(Views.CARGAR_CLIENTE, "Modificar cliente",
                cvmt.getClienteEntity());
        if (optional.isPresent()) {
            if (!optional.get().equals(cvmt.getClienteEntity()))
                obsListClientes.set(obsListClientes.indexOf(cvmt), new ClienteViewModelTabla(optional.get()));
        }
    }

    @FXML
    private void darDeBaja() {
        ClienteViewModelTabla cvmt = tablaClientes.getSelectionModel().getSelectedItem();
        if (cvmt == null) {
            Alertas.aviso("Dar de baja cliente", "Debe seleccionar un cliente para darlo de baja.");
            return;
        }
        if (!Alertas.confirmacion("Dar de baja cliente", "¿Confirmar baja de cliente?")) return;

        try {
            clienteServ.softDeleteClient(cvmt.getClienteEntity());
            Alertas.exito("Dar de baja cliente", "Se ha dado de baja el cliente con éxito.");
            obsListClientes.remove(cvmt);
        } catch (Exception e) {
            Alertas.error("Dar de baja cliente", "Ha ocurrido un error inesperado.");
            throw new RuntimeException(e);
        }
    }

    private void llenarFilas(List<Cliente> clienteList) {
        obsListClientes.clear();
        for (Cliente c : clienteList) obsListClientes.add(new ClienteViewModelTabla(c));
    }

    private void configColumnas() {
        tablaClientes.setItems(obsListClientes);
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }
}
