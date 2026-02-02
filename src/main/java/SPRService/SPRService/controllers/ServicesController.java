package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.FacturaServiceDTO;
import SPRService.SPRService.DTOs.TicketRetiroServiceDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorFacturasPDF;
import SPRService.SPRService.util.generadores.GeneradorTXT;
import SPRService.SPRService.util.generadores.Impresor;
import SPRService.SPRService.viewModels.tablas.ServiceRowViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServicesController implements Initializable {

    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private ObservableList<ServiceRowViewModel> obsListServiceVM = FXCollections.observableArrayList();

    @FXML
    private TextField tfCodigo, tfDniCliente;
    @FXML
    private CheckComboBox<PrioridadService> ccbPrioridades;
    @FXML
    private CheckComboBox<EstadoService> ccbEstados;
    @FXML
    private DatePicker dpMinimaCarga, dpMaximaCarga, dpMinimaRetiro, dpMaximaRetiro;
    @FXML
    private TableView<ServiceRowViewModel> tablaServices;
    @FXML
    private TableColumn<Long, Long> colCodigo;
    @FXML
    private TableColumn<String, String> colFechaCarga, colFechaEntrega, colEstado, colPrioridad, colMontoFaltante,
            colMontoTotal, colCliente;

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
                tfDniCliente.getText(),
                dpMinimaCarga.getValue(), dpMaximaCarga.getValue(),
                dpMinimaRetiro.getValue(), dpMaximaRetiro.getValue(),
                ccbEstados.getCheckModel().getCheckedItems(), ccbPrioridades.getCheckModel().getCheckedItems());
        cargarTabla(serviceServ.buscarConFiltros(filtros));
    }

    @FXML
    private void nuevoService() {
        Optional<Service> result = navigator.openModal(Views.CARGAR_SERVICE, "Nuevo service", null);
        result.ifPresent(service -> obsListServiceVM.addFirst(new ServiceRowViewModel(service)));
    }

    @FXML
    private void modificarService() {
        ServiceRowViewModel dto = tablaServices.getSelectionModel().getSelectedItem();
        if (dto == null) {
            NotificationHelper.mostrarAdvertencia("Detalles de service", "Debes seleccionar un service se la tabla para " +
                    "ver sus detalles.");
            return;
        }

        serviceServ.datosParaModificar(dto.getCodigo())
                .ifPresent(s -> {
                    Optional<Service> mod = navigator.openModal(Views.MODIFICAR_SERVICE, "Detalles del service", s);
                    // todo: que refresque la página

//                    mod.ifPresent(dto::updateFromService);
                });
    }

    @FXML
    private void agregarPago() {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Agregar pago", "Debe seleccionar un service para agregarle el pago.");
            return;
        }
        if (vm.getService().getEstadoService() == EstadoService.CANCELADO ||
                vm.getService().getEstadoService() == EstadoService.PAGADO) {
            NotificationHelper.mostrarAdvertencia("Agregar pago", "No se pueden agregar pagos a las ventas que están canceladas" +
                    " o pagadas");
            return;
        }

        serviceServ.datosPagos(vm.getCodigo()).ifPresent(s -> {
            Optional<Service> conPago = navigator.openModal(Views.PAGO, "Agregar pago", s);
            // todo: que refresque la página
//            conPago.ifPresent(vm::updateFromService);
        });


//        Optional<Service> result = navigator.openModal(Views.PAGO, "Agregar pago", vm.getService());
//        result.ifPresent(service -> obsListServiceVM.set(obsListServiceVM.indexOf(vm), new ServiceRowViewModel(service)));
    }

    @FXML
    private void verPagos() {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Agregar pago", "Debe seleccionar un service para ver sus pagos.");
            return;
        }

        serviceServ.datosPagos(vm.getCodigo()).ifPresent(s -> {
            Optional<Service> conPago = navigator.openModal(Views.VER_PAGOS, "Agregar pago", s);
            // todo: que refresque la página

//            conPago.ifPresent(vm::updateFromService);
        });

//        Optional<ServiceRowViewModel> result = navigator.openModal(Views.VER_PAGOS, "Ver pagos", vm);
//        if (result.isPresent()) {
//            obsListServiceVM.set(obsListServiceVM.indexOf(vm), result.get());
//            tablaServices.getSelectionModel().select(result.get());
//        }
    }

    @FXML
    private void generarFactura(ActionEvent event) {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Factura service", "Debe seleccionar un service para imprimir su factura.");
            return;
        }
        if (vm.getService().getEstadoService() == EstadoService.CANCELADO) {
            NotificationHelper.mostrarAdvertencia("Factura service", "No es posible generar facturas de services cancelados.");
            return;
        }

        File file = SimpleDialogs.selectorRuta(event, "Seleccione donde guardar la factura",
                "Factura service nro. " + vm.getCodigo(),
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        if (file == null) return;

        serviceServ.datosParaModificar(vm.getCodigo())
                .ifPresent(s -> {
                    GeneradorFacturasPDF.generaPDFService(new FacturaServiceDTO(s), file);
                });
    }

    @FXML
    private void generarTicket(ActionEvent event) {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Generar ticket",
                    "Debe seleccionar un service para poder generar su ticket.");
            return;
        }
        if (vm.getService().getEstadoService() == EstadoService.CANCELADO ||
                vm.getService().getEstadoService() == EstadoService.PAGADO) {
            NotificationHelper.mostrarAdvertencia("generar ticket",
                    "No es posible generar el ticket de una factura en estado 'Pagado' o 'Cancelado'.");
            return;
        }

        File file = SimpleDialogs.selectorRuta(event, "Seleccione donde quiere guardar el ticket",
                "ticket-service.txt",
                new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        if (file == null) return;

        serviceServ.datosTicket(vm.getCodigo())
                .ifPresent(s -> {
                    GeneradorTXT.generarTicketRetiroService(new TicketRetiroServiceDTO(s), file);
                    if (SimpleDialogs.confirmacion("Generar ticket", "¿Desea imprimir el ticket?"))
                        Impresor.imprimirConSistema(file);
                });
    }

    @FXML
    private void darDeBaja() {
        SPRService.SPRService.viewModels.tablas.ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Cancelar service", "Debe seleccionar un service para cancelarlo.");
            return;
        }

        if (vm.getService().getEstadoService() == EstadoService.PAGADO ||
                vm.getService().getEstadoService() == EstadoService.CANCELADO) {
            NotificationHelper.mostrarAdvertencia("Cancelar service", "No es posible cancelar services en estado 'Pagado' o " +
                    "'Cancelado'.");
            return;
        }

        Usuario usuarioCancelador = SessionManager.getUsuarioSesion();
        if (usuarioCancelador == null) {
            NotificationHelper.mostrarError("Cancelación de venta", "No hay usuario en la sesión activa.");
            return;
        }

        if (!SimpleDialogs.confirmacion("Cancelar service", "¿Está seguro de que desea cancelar el service?"))
            return;

        String motivo = SimpleDialogs.motivoBorrado();
        if (motivo == null) return;

        boolean confirmacion2 = SimpleDialogs.confirmacion("Cancelar service", "Esta acción es " +
                "irreversible.\n ¿Confirmar el borrado de service?");
        if (!confirmacion2) return;

        Boolean restablecerStock = SimpleDialogs.confirmacionRestablecerStocks();
        if (restablecerStock == null) return;

        try {
            serviceServ.cancelarService(vm.getCodigo(), restablecerStock, motivo, usuarioCancelador);
            // todo refrescar página
            NotificationHelper.mostrarExito("Cancelar service", "Se ha cancelado el service con éxito.");
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Cancelar service", e.getMessage());
            throw e;
        }
    }

    @FXML
    private void reportesAnuales() {
        navigator.openModal(Views.CHART_ANUAL_SERVICE, "Generar reportes anuales", null);
    }

    @FXML
    private void comparacionIngresos() {
        navigator.openModal(Views.CHART_COMPARACION_INGRESOS, "Comparación de ingresos", null);
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

        dpMinimaCarga.setConverter(new SafeLocalDateConverter());
        dpMaximaCarga.setConverter(new SafeLocalDateConverter());
        dpMinimaRetiro.setConverter(new SafeLocalDateConverter());
        dpMaximaRetiro.setConverter(new SafeLocalDateConverter());

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
        colMontoFaltante.setCellValueFactory(new PropertyValueFactory<>("montoFaltante"));
        colMontoTotal.setCellValueFactory(new PropertyValueFactory<>("montoTotal"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("dniCliente"));
    }
}
