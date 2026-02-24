package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.FacturaServiceDTO;
import SPRService.SPRService.DTOs.TicketRetiroServiceDTO;
import SPRService.SPRService.DTOs.filtros.FiltroServiceDTO;
import SPRService.SPRService.entities.Service;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.EstadoService;
import SPRService.SPRService.enums.PrioridadService;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ServiceServ;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.ResultadoPaginado;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServicesController implements Initializable {

    private static final int ITEMS_POR_PAGINA = 30;
    private final Navigator navigator;
    private final ServiceServ serviceServ;
    private final ObservableList<ServiceRowViewModel> obsListServiceVM = FXCollections.observableArrayList();
    private int paginaActual = 0;
    private int totalPaginas = 1;

    @FXML
    private TextField tfCodigo, tfDniCliente;
    @FXML
    private TextField tfMontoMinimo, tfMontoMaximo;
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
    private TableColumn<ServiceRowViewModel, String> colFechaCarga, colFechaEntrega, colEstado, colPrioridad,
            colMontoFaltante, colMontoTotal, colCliente;
    @FXML
    private Pagination paginacion;
    @FXML
    private Button btnCancelarServ, btnAgregarPago, btnRepAnual, btnRepComp;

    @Inject
    public ServicesController(AppCoordinator coordinator, ServiceServ serviceServ) {
        this.navigator = coordinator.getMainNavigator();
        this.serviceServ = serviceServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();
        configurarPaginacion();
        tablaServices.setItems(obsListServiceVM);

        cargarPagina(0);

        configPermisos();
    }

    private void configPermisos() {
        RolUsuario rol = SessionManager.getRolUsuario();
        if (rol == RolUsuario.OPERATIVO_TALLER) {
            btnCancelarServ.setDisable(true);
            btnRepAnual.setDisable(true);
            btnRepComp.setDisable(true);
            btnAgregarPago.setDisable(true);
        } else if (rol == RolUsuario.OPERATIVO_RECEPCION) {
            btnRepAnual.setDisable(true);
            btnRepComp.setDisable(true);
        }
    }

    // ==================== PAGINACIÓN ====================

    private void configurarPaginacion() {
        paginacion.setPageCount(1);
        paginacion.setCurrentPageIndex(0);
        paginacion.setMaxPageIndicatorCount(10);

        paginacion.currentPageIndexProperty().addListener((obs, oldPage, newPage) -> {
            if (newPage != null && !newPage.equals(oldPage)) {
                cargarPagina(newPage.intValue());
            }
        });
    }

    private void cargarPagina(int numeroPagina) {
        if (ccbPrioridades.getCheckModel().getCheckedItems().isEmpty() ||
                ccbEstados.getCheckModel().getCheckedItems().isEmpty()) {
            obsListServiceVM.clear();
            paginacion.setPageCount(1);
            return;
        }

        FiltroServiceDTO filtros = construirFiltro();

        ResultadoPaginado<Service> resultado = serviceServ.buscarPaginado(
                filtros, numeroPagina, ITEMS_POR_PAGINA);

        int paginas = (int) Math.ceil((double) resultado.getCantidadResultados() / ITEMS_POR_PAGINA);
        totalPaginas = Math.max(1, paginas);
        paginaActual = numeroPagina;

        paginacion.setPageCount(totalPaginas);

        obsListServiceVM.clear();
        for (Service s : resultado.getLista()) {
            obsListServiceVM.add(new ServiceRowViewModel(s));
        }
    }

    private FiltroServiceDTO construirFiltro() {
        return new FiltroServiceDTO(
                ManejadorInputs.codigoVenta(tfCodigo.getText().strip(), false),
                tfDniCliente.getText(),
                dpMinimaCarga.getValue(),
                dpMaximaCarga.getValue(),
                dpMinimaRetiro.getValue(),
                dpMaximaRetiro.getValue(),
                ccbEstados.getCheckModel().getCheckedItems(),
                ccbPrioridades.getCheckModel().getCheckedItems(),
                parsearMonto(tfMontoMinimo.getText()),
                parsearMonto(tfMontoMaximo.getText())
        );
    }

    /**
     * Convierte el texto de un campo de monto a BigDecimal.
     * Devuelve null si el campo está vacío o no es un número válido.
     */
    private BigDecimal parsearMonto(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            String limpio = texto.replace(",", ".").strip();
            BigDecimal valor = new BigDecimal(limpio);
            return valor.compareTo(BigDecimal.ZERO) >= 0 ? valor : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void recargarPaginaActual() {
        cargarPagina(paginaActual);
    }

    // ==================== ACCIONES ====================

    @FXML
    private void verTodos() {
        tfCodigo.clear();
        tfDniCliente.clear();
        tfMontoMinimo.clear();
        tfMontoMaximo.clear();
        dpMinimaCarga.setValue(null);
        dpMaximaCarga.setValue(null);
        dpMinimaRetiro.setValue(null);
        dpMaximaRetiro.setValue(null);
        ccbEstados.getCheckModel().checkAll();
        ccbPrioridades.getCheckModel().checkAll();

        paginacion.setCurrentPageIndex(0);
        cargarPagina(0);
    }

    @FXML
    private void buscarConFiltros() {
        if (ccbPrioridades.getCheckModel().getCheckedItems().isEmpty() ||
                ccbEstados.getCheckModel().getCheckedItems().isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Buscar",
                    "Debe seleccionar al menos un estado y una prioridad.");
            return;
        }

        // ========== VALIDACIÓN DE MONTOS ==========
        BigDecimal montoMin = parsearMonto(tfMontoMinimo.getText());
        BigDecimal montoMax = parsearMonto(tfMontoMaximo.getText());

        if (!tfMontoMinimo.getText().isBlank() && montoMin == null) {
            NotificationHelper.mostrarAdvertencia("Buscar",
                    "El monto mínimo ingresado no es un número válido.");
            tfMontoMinimo.requestFocus();
            return;
        }
        if (!tfMontoMaximo.getText().isBlank() && montoMax == null) {
            NotificationHelper.mostrarAdvertencia("Buscar",
                    "El monto máximo ingresado no es un número válido.");
            tfMontoMaximo.requestFocus();
            return;
        }
        if (montoMin != null && montoMax != null && montoMin.compareTo(montoMax) > 0) {
            NotificationHelper.mostrarAdvertencia("Buscar",
                    "El monto mínimo no puede ser mayor al monto máximo.");
            tfMontoMinimo.requestFocus();
            return;
        }
        // ==================================================

        paginacion.setCurrentPageIndex(0);
        cargarPagina(0);
    }

    @FXML
    private void nuevoService() {
        Optional<Service> result = navigator.openModal(Views.CARGAR_SERVICE, "Nuevo service", null);
        if (result.isPresent()) {
            paginacion.setCurrentPageIndex(0);
            cargarPagina(0);
        }
    }

    @FXML
    private void modificarService() {
        ServiceRowViewModel dto = tablaServices.getSelectionModel().getSelectedItem();
        if (dto == null) {
            NotificationHelper.mostrarAdvertencia("Detalles de service",
                    "Debes seleccionar un service de la tabla para ver sus detalles.");
            return;
        }

        serviceServ.datosParaModificar(dto.getCodigo())
                .ifPresent(s -> {
                    Optional<Service> mod = navigator.openModal(Views.MODIFICAR_SERVICE,
                            "Detalles del service", s);
                    if (mod.isPresent()) {
                        recargarPaginaActual();
                    }
                });
    }

    @FXML
    private void agregarPago() {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Agregar pago",
                    "Debe seleccionar un service para agregarle el pago.");
            return;
        }
        if (vm.getService().getEstadoService() == EstadoService.CANCELADO ||
                vm.getService().getEstadoService() == EstadoService.PAGADO) {
            NotificationHelper.mostrarAdvertencia("Agregar pago",
                    "No se pueden agregar pagos a los services que están cancelados o pagados.");
            return;
        }

        serviceServ.datosPagos(vm.getCodigo()).ifPresent(s -> {
            Optional<Service> conPago = navigator.openModal(Views.PAGO, "Agregar pago", s);
            if (conPago.isPresent()) {
                recargarPaginaActual();
            }
        });
    }

    @FXML
    private void verPagos() {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Ver pagos",
                    "Debe seleccionar un service para ver sus pagos.");
            return;
        }

        serviceServ.datosPagos(vm.getCodigo()).ifPresent(s -> {
            Optional<Service> conPago = navigator.openModal(Views.VER_PAGOS, "Ver pagos", s);
            if (conPago.isPresent()) {
                recargarPaginaActual();
            }
        });
    }

    @FXML
    private void generarFactura(ActionEvent event) {
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Factura service",
                    "Debe seleccionar un service para imprimir su factura.");
            return;
        }
        if (vm.getService().getEstadoService() == EstadoService.CANCELADO) {
            NotificationHelper.mostrarAdvertencia("Factura service",
                    "No es posible generar facturas de services cancelados.");
            return;
        }

        File file = SimpleDialogs.selectorRuta(event, "Seleccione donde guardar la factura",
                "Factura service nro. " + vm.getCodigo(),
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        if (file == null) return;

        serviceServ.datosParaModificar(vm.getCodigo())
                .ifPresent(s -> GeneradorFacturasPDF.generaPDFService(new FacturaServiceDTO(s), file));
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
            NotificationHelper.mostrarAdvertencia("Generar ticket",
                    "No es posible generar el ticket de un service en estado 'Pagado' o 'Cancelado'.");
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
        ServiceRowViewModel vm = tablaServices.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Cancelar service",
                    "Debe seleccionar un service para cancelarlo.");
            return;
        }

        if (vm.getService().getEstadoService() == EstadoService.PAGADO ||
                vm.getService().getEstadoService() == EstadoService.CANCELADO) {
            NotificationHelper.mostrarAdvertencia("Cancelar service",
                    "No es posible cancelar services en estado 'Pagado' o 'Cancelado'.");
            return;
        }

        Usuario usuarioCancelador = SessionManager.getUsuarioSesion();
        if (usuarioCancelador == null) {
            NotificationHelper.mostrarError("Cancelación de service",
                    "No hay usuario en la sesión activa.");
            return;
        }

        if (!SimpleDialogs.confirmacion("Cancelar service",
                "¿Está seguro de que desea cancelar el service?"))
            return;

        String motivo = SimpleDialogs.motivoBorrado();
        if (motivo == null) return;

        boolean confirmacion2 = SimpleDialogs.confirmacion("Cancelar service",
                "Esta acción es irreversible.\n¿Confirmar la cancelación del service?");
        if (!confirmacion2) return;

        Boolean restablecerStock = SimpleDialogs.confirmacionRestablecerStocks();
        if (restablecerStock == null) return;

        try {
            serviceServ.cancelarService(vm.getCodigo(), restablecerStock, motivo, usuarioCancelador);
            recargarPaginaActual();
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

    // ==================== CONFIGURACIÓN ====================

    private void configurarControles() {
        configColumnas();

        dpMinimaCarga.setConverter(new SafeLocalDateConverter());
        dpMaximaCarga.setConverter(new SafeLocalDateConverter());
        dpMinimaRetiro.setConverter(new SafeLocalDateConverter());
        dpMaximaRetiro.setConverter(new SafeLocalDateConverter());

        ccbEstados.getItems().setAll(EstadoService.values());
        ccbEstados.getCheckModel().checkAll();
        ccbPrioridades.getItems().setAll(PrioridadService.values());
        ccbPrioridades.getCheckModel().checkAll();

        // ========== RESTRICCIÓN: solo números y punto/coma ==========
        restringirAMoneda(tfMontoMinimo);
        restringirAMoneda(tfMontoMaximo);
        // =============================================================

        colFechaEntrega.setCellFactory(column -> new TableCell<ServiceRowViewModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setStyle("");
                if (empty || item == null) {
                    return;
                }

                setText(item);
                ServiceRowViewModel vm = getTableView().getItems().get(getIndex());

                if (vm.getService().getFechaEntrega().isBefore(LocalDateTime.now())
                        && vm.getService().getEstadoService() != EstadoService.PAGADO
                        && vm.getService().getEstadoService() != EstadoService.CANCELADO
                        && vm.getService().getEstadoService() != EstadoService.PAGO_PENDIENTE
                        && vm.getService().getEstadoService() != EstadoService.FINALIZADO) {

                    setStyle("-fx-text-fill: #952122; -fx-font-weight: bold;");
                }
            }
        });

        colEstado.setCellFactory(column -> new TableCell<ServiceRowViewModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setStyle("");
                if (empty || item == null) {
                    return;
                }
                setText(item);
                ServiceRowViewModel vm = getTableView().getItems().get(getIndex());
                if (vm.getService().getEstadoService().equals(EstadoService.CANCELADO)) {
                    setStyle("-fx-text-fill: #952122; -fx-font-weight: bold;");
                } else if (vm.getService().getEstadoService().equals(EstadoService.PAGADO)) {
                    setStyle("-fx-text-fill: #366140; -fx-font-weight: bold;");
                }
            }
        });

        colPrioridad.setCellFactory(column -> new TableCell<ServiceRowViewModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                setStyle("");
                if (empty || item == null) {
                    return;
                }

                setText(item);
                ServiceRowViewModel vm = getTableView().getItems().get(getIndex());
                switch (vm.getService().getPrioridad()) {
                    case MUY_ALTA -> setStyle("-fx-text-fill: #912121; -fx-font-weight: bold;");
                    case ALTA -> setStyle("-fx-text-fill: #b6aa21; -fx-font-weight: bold;");
                    case MEDIA -> setStyle("-fx-text-fill: #0b0b0b; -fx-font-weight: bold;");
                    case BAJA -> setStyle("-fx-text-fill: #1e9a74; -fx-font-weight: bold;");
                    case MUY_BAJA -> setStyle("-fx-text-fill: #236b9e; -fx-font-weight: bold;");
                }
            }
        });
    }

    /**
     * Restringe un TextField para que solo acepte dígitos, punto y coma (moneda).
     */
    private void restringirAMoneda(TextField tf) {
        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9.,]*")) {
                tf.setText(oldVal);
            }
        });
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