package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaPago;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.viewModels.celdas.ItemPagoViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class VerPagosController implements Initializable, DataReceiver<Service>,
        ModalController<Service> {

    private final Navigator navigator;
    private Service service;
    private Service paraDevolver;
    private ObservableList<ItemPagoViewModel> items = FXCollections.observableArrayList();

    @FXML
    private ListView<ItemPagoViewModel> lista;
    @FXML
    private Button btnAgregarPago;

    @Inject
    public VerPagosController(AppCoordinator coordinator) {
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lista.setItems(items);
        lista.setCellFactory(f -> new CeldaPago());
        String css = getClass().getResource("/styles/celdaPago.css").toExternalForm();
        lista.getStylesheets().add(css);
        configPermisos();
    }

    private void configPermisos() {
        RolUsuario rol = SessionManager.getRolUsuario();
        if (rol == RolUsuario.OPERATIVO_TALLER) {
            btnAgregarPago.setDisable(true);
        }
    }

    @Override
    public void receiveData(Service data) {
        if (data != null) {
            this.service = data;
            if (data.getEstadoService().equals(EstadoService.CANCELADO) ||
                    data.getEstadoService().equals(EstadoService.PAGADO)) {
                btnAgregarPago.setDisable(true);
            }
            cargarPagos();
        }
    }

    @Override
    public Optional<Service> getResult() {
        return Optional.ofNullable(this.paraDevolver);
    }

    @FXML
    private void agregarPago() {
        Optional<Service> result = navigator.openModal(Views.PAGO, "Agregar pago", this.service);
        result.ifPresent(service -> {
            receiveData(service);
            this.paraDevolver = this.service;
        });
    }

    private void cargarPagos() {
        items.clear();
        this.service.getPagos().forEach(p -> items.add(new ItemPagoViewModel(p)));
    }
}
