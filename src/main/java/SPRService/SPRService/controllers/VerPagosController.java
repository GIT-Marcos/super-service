package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaPago;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.navigation.*;
import SPRService.SPRService.viewModels.celdas.ItemPagoViewModel;
import SPRService.SPRService.viewModels.tablas.ServiceRowViewModel;
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

public class VerPagosController implements Initializable, DataReceiver<ServiceRowViewModel>,
        ModalController<ServiceRowViewModel> {

    private final Navigator navigator;
    private ServiceRowViewModel serviceRVM;
    private ServiceRowViewModel paraDevolver;
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
    }

    @Override
    public void receiveData(ServiceRowViewModel data) {
        if (data != null) {
            this.serviceRVM = data;
            if (data.getService().getEstadoService().equals(EstadoService.CANCELADO) ||
                    data.getService().getEstadoService().equals(EstadoService.PAGADO)) {
                btnAgregarPago.setDisable(true);
            }
            cargarPagos();
        }
    }

    @Override
    public Optional<ServiceRowViewModel> getResult() {
        return Optional.ofNullable(this.paraDevolver);
    }

    @FXML
    private void agregarPago() {
        Optional<Service> result = navigator.openModal(Views.PAGO, "Agregar pago", this.serviceRVM.getService());
        result.ifPresent(service -> {
            receiveData(new ServiceRowViewModel(service));
            this.paraDevolver = this.serviceRVM;
        });
    }

    private void cargarPagos() {
        items.clear();
        this.serviceRVM.getService().getPagos().forEach(p -> items.add(new ItemPagoViewModel(p)));
    }
}
