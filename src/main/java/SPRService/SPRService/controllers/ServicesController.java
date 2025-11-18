package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.viewModels.tablas.ServiceRowViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServicesController implements Initializable {

    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private ObservableList<ServiceRowViewModel> obsListServiceVM = FXCollections.observableArrayList();

    @FXML
    private TextField tfCodigo;
    @FXML
    private CheckComboBox<PrioridadService> ccbPrioridades;
    @FXML
    private CheckComboBox<EstadoService> ccbEstados;
    @FXML
    private DatePicker dpMinima, dpMaxima;
    @FXML
    private TableView<ServiceRowViewModel> tablaServices;
    @FXML
    private TableColumn<Long, Long> colCodigo;
    @FXML
    private TableColumn<String, String> colFechaCarga, colFechaEntrega, colEstado, colPrioridad;

    @Inject
    public ServicesController(AppCoordinator coordinator, ServiceServ serviceServ) {
        this.navigator = coordinator.getMainNavigator();
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();

        cargarTabla(serviceServ.verTodos());
    }

    @FXML
    private void verTodos() {
        cargarTabla(serviceServ.verTodos());
    }

    @FXML
    private void buscarConFiltros() {
        if (ccbPrioridades.getCheckModel().getItemCount() == 0 &&
                ccbPrioridades.getCheckModel().getItemCount() == 0)
            return;

        FiltroServiceDTO filtros = new FiltroServiceDTO(
                ManejadorInputs.codigoVenta(tfCodigo.getText().strip(), false),
                dpMinima.getValue(), dpMaxima.getValue(),
                ccbEstados.getCheckModel().getCheckedItems(), ccbPrioridades.getCheckModel().getCheckedItems());
        cargarTabla(serviceServ.buscarConFiltros(filtros));
    }

    @FXML
    private void nuevoService() {
        Optional<Service> result = navigator.openModal(Views.CARGAR_SERVICE, "Nuevo service", null);
        result.ifPresent(service -> obsListServiceVM.addFirst(new ServiceRowViewModel(service)));
    }

    private void cargarTabla(List<Service> services) {
        obsListServiceVM.clear();
        for (Service s : services) {
            obsListServiceVM.add(new ServiceRowViewModel(s));
        }
    }

    private void configurarControles() {
        configColumnas();
        tablaServices.setItems(obsListServiceVM);

        dpMaxima.setConverter(new SafeLocalDateConverter());
        dpMinima.setConverter(new SafeLocalDateConverter());

        ccbEstados.getItems().setAll(EstadoService.values());
        ccbEstados.getCheckModel().checkAll();
        ccbPrioridades.getItems().setAll(PrioridadService.values());
        ccbPrioridades.getCheckModel().checkAll();
    }

    private void configColumnas() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colFechaCarga.setCellValueFactory(new PropertyValueFactory<>("fechaCarga"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
    }
}
