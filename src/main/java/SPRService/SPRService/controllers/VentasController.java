package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.filtros.FiltroVentaRepuestoDTO;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.VentaRepuestoServ;
import SPRService.SPRService.util.*;
import SPRService.SPRService.util.alertas.NotificationHelper;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import SPRService.SPRService.viewModels.tablas.VentaRepuestoVMtabla;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.entities.VentaRepuesto;
import SPRService.SPRService.enums.EstadoVentaRepuesto;
import SPRService.SPRService.util.generadores.GeneradorFacturasPDF;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VentasController implements Initializable {

    private ObservableList<VentaRepuestoVMtabla> obsListVentasVM = FXCollections.observableArrayList();
    private final VentaRepuestoServ ventaRepuestoServ;
    private final Navigator navigator;
    private static final int ITEMS_POR_PAGINA = 30;

    @FXML
    private TextField tfBuscar, tfMontoMin, tfMontoMax, tfBuscarDni;
    @FXML
    private CheckBox checkPagado, checkPendiente, checkCancelado;
    @FXML
    private ComboBox<String> comboOrdenarPor, comboTipoOrden;
    @FXML
    private TableView<VentaRepuestoVMtabla> tablaVentas;
    @FXML
    private TableColumn<VentaRepuestoVMtabla, Long> colCodVenta;
    @FXML
    private TableColumn<VentaRepuestoVMtabla, String> colEstadoVenta, colFechaVenta, colCliente;
    @FXML
    private TableColumn<VentaRepuestoVMtabla, BigDecimal> colMontoVenta, colFaltante;
    @FXML
    private DatePicker dateFechaMin, dateFechaMax;
    @FXML
    private Pagination paginacion;
    @FXML
    private Button btnCancelar, btnRepMens, btnRepAnu, btnRepMasIng;

    @Inject
    public VentasController(VentaRepuestoServ ventaRepuestoServ, AppCoordinator appCoordinator) {
        this.ventaRepuestoServ = ventaRepuestoServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarCombos();
        dateFechaMin.setConverter(new SafeLocalDateConverter());
        dateFechaMin.setPromptText("dd/MM/yyyy");
        dateFechaMax.setConverter(new SafeLocalDateConverter());
        dateFechaMax.setPromptText("dd/MM/yyyy");

        configColumnas();
        configurarControles();
        tablaVentas.setItems(obsListVentasVM);
        paginacion.setPageFactory(this::cargarPagina);

        configPermisos();
    }

    private void configPermisos() {
        RolUsuario rol = SessionManager.getRolUsuario();
        if (rol == RolUsuario.OPERATIVO_VENTAS) {
            btnRepMens.setDisable(true);
            btnRepAnu.setDisable(true);
            btnRepMasIng.setDisable(true);
        }
    }

    private Node cargarPagina(int indicePagina) {
        // 1. Recolectar todos los filtros de la UI
        FiltroVentaRepuestoDTO filtros = recolectarFiltrosActuales();

        // 2. Llamar al servicio con los filtros y la paginación
        ResultadoPaginado<VentaRepuesto> resultado = ventaRepuestoServ.buscarVentasPaginado(
                filtros, indicePagina, ITEMS_POR_PAGINA);

        // 3. Calcular el número total de páginas y actualizar el control
        long totalItems = resultado.getCantidadResultados();
        long totalPaginas = (totalItems + ITEMS_POR_PAGINA - 1) / ITEMS_POR_PAGINA; // Forma segura de redondear hacia arriba
        paginacion.setPageCount(totalPaginas == 0 ? 1 : (int) totalPaginas);

        // 4. Convertir las entidades a ViewModels
        List<VentaRepuestoVMtabla> viewModels = resultado.getLista()
                .stream()
                .map(VentaRepuestoVMtabla::new)
                .collect(Collectors.toList());

        // 5. ACTUALIZAR el contenido de la lista observable.
        obsListVentasVM.setAll(viewModels);

        return new VBox(); // Devolver un nodo vacío porque la tabla ya está en la escena.
    }

    @FXML
    private void buscarConFiltros() {
        // Al cambiar los filtros, siempre volvemos a la primera página.
        // Establecer el índice de página a 0 NO dispara el pageFactory automáticamente.
        // Por eso, después de establecerlo, llamamos directamente a nuestra carga.
        if (paginacion.getCurrentPageIndex() != 0) {
            paginacion.setCurrentPageIndex(0);
        }
        cargarPagina(0);
    }

    @FXML
    private void todasVentas() {
        limpiarFiltros();
        buscarConFiltros(); // Reutilizamos la lógica de búsqueda para recargar
    }

    @FXML
    private void ventasHoy() {
        limpiarFiltros();
        dateFechaMin.setValue(LocalDate.now());
        dateFechaMax.setValue(LocalDate.now());
        buscarConFiltros();
    }

    @FXML
    private void verDetalles() {
        VentaRepuestoVMtabla vrvm = tablaVentas.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            NotificationHelper.mostrarAdvertencia("Detalles de venta", "Debe seleccionar una venta para ver sus detalles.");
            return;
        }

        Optional<VentaRepuesto> result = ventaRepuestoServ.verDetalle(vrvm.getVentaRepuesto().getId());
        result.flatMap(v -> navigator.openModal(Views.DETALLE_VENTA, "Detalles de venta", v))
                .ifPresent(r -> cargarPagina(paginacion.getCurrentPageIndex()));
    }

    @FXML
    private void imprimirFactura(ActionEvent event) {
        VentaRepuesto ventaParaImpresion;
        VentaRepuestoVMtabla vrvm = tablaVentas.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            NotificationHelper.mostrarAdvertencia("Impresión de venta",
                    "Debe seleccionar una venta para imprimir su factura.");
            return;
        } else if (vrvm.getVentaRepuesto().getEstadoVenta() == EstadoVentaRepuesto.CANCELADO) {
            NotificationHelper.mostrarAdvertencia("Impresión de venta",
                    "Esta venta ya está cancelada y no es posible imprimir su factura.");
            return;
        }
        ventaParaImpresion = vrvm.getVentaRepuesto();

        File file = SimpleDialogs.selectorRuta(event, "Seleccione donde guardar la factura", "factura.pdf",
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        if (file == null) {
            return;
        }
        try {
            GeneradorFacturasPDF.generaPDFVenta(ventaParaImpresion, file);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Impresión de factura",
                    "Ha ocurrido un error inesperado el imprimir la factura.");
            e.printStackTrace();
        }
    }

    @FXML
    private void reportesAnuales() {
        navigator.openModal(Views.CHART_VENTAS_RESPUESTOS_ANIO, "Reporte", null);
    }

    @FXML
    private void reportesMensuales() {
        navigator.openModal(Views.CHART_VENTAS_REPUESTOS_MES, "Reporte", null);
    }

    @FXML
    private void reporteMasIngresos() {
        navigator.openModal(Views.CHART_INGRESOS_REPUESTO, "Reporte de ingresos por repuesto", null);
    }

    @FXML
    private void cancelarVenta() {
        VentaRepuestoVMtabla vrvm = tablaVentas.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            NotificationHelper.mostrarAdvertencia("Cancelación de venta", "Debe seleccionar una venta para cancelarla.");
            return;
        } else if (vrvm.getVentaRepuesto().getEstadoVenta() == EstadoVentaRepuesto.CANCELADO) {
            NotificationHelper.mostrarAdvertencia("Cancelación de venta", "Esta venta ya está cancelada.");
            return;
        }

        Usuario usuarioCancelador = SessionManager.getUsuarioSesion();
        if (usuarioCancelador == null) {
            NotificationHelper.mostrarAdvertencia("Cancelación de venta", "No hay usuario en la sesión activa.");
            return;
        }

        String motivo = SimpleDialogs.motivoBorrado();
        if (motivo == null) return;

        boolean confirmacion = SimpleDialogs.confirmacion("Cancelación de venta", "Esta acción es " +
                "irreversible.\n ¿Confirmar el borrado de venta?");
        if (!confirmacion) return;

        Boolean restablecerStock = SimpleDialogs.confirmacionRestablecerStocks();
        if (restablecerStock == null) return;

        try {
            ventaRepuestoServ.cancelarVenta(vrvm.getVentaRepuesto().getId(), restablecerStock, motivo, usuarioCancelador);
            cargarPagina(paginacion.getCurrentPageIndex());
            NotificationHelper.mostrarExito("Cancelación de venta", "Se ha cancelado la venta con éxito.");
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Cancelación de venta", e.getMessage());
            throw e;
        }
    }

    private FiltroVentaRepuestoDTO recolectarFiltrosActuales() {
        try {
            return new FiltroVentaRepuestoDTO(
                    ManejadorInputs.codigoVenta(tfBuscar.getText(), false),
                    ManejadorInputs.textoGenerico(tfBuscarDni.getText(), false, "DNI/CUIL", null),
                    ManejadorInputs.dinero(tfMontoMin.getText(), false, false),
                    ManejadorInputs.dinero(tfMontoMax.getText(), false, false),
                    dateFechaMin.getValue(),
                    dateFechaMax.getValue(),
                    tomarEstados(),
                    tomaOrdenPor(),
                    comboTipoOrden.getSelectionModel().getSelectedIndex()
            );
        } catch (Exception e) {
            // Manejar la excepción, quizás mostrar una alerta
            // y devolver un DTO vacío para no romper la carga.
            NotificationHelper.mostrarAdvertencia("Filtro inválidos", e.getMessage());
            return new FiltroVentaRepuestoDTO(); // Devuelve filtros por defecto
        }
    }

    private void limpiarFiltros() {
        tfBuscar.clear();
        tfBuscarDni.clear();
        tfMontoMin.clear();
        tfMontoMax.clear();
        dateFechaMin.setValue(null);
        dateFechaMax.setValue(null);
        checkPagado.setSelected(true);
        checkPendiente.setSelected(true);
        checkCancelado.setSelected(true);
        comboOrdenarPor.getSelectionModel().select(0);
        comboTipoOrden.getSelectionModel().select(0);
    }

    private void configurarControles() {
        llenarCombos();
        dateFechaMin.setConverter(new SafeLocalDateConverter());
        dateFechaMin.setPromptText("dd/MM/yyyy");
        dateFechaMax.setConverter(new SafeLocalDateConverter());
        dateFechaMax.setPromptText("dd/MM/yyyy");

        // Listener para que al presionar Enter en el campo de búsqueda, se active el filtro
        tfBuscar.setOnAction(e -> buscarConFiltros());
    }

    private List<EstadoVentaRepuesto> tomarEstados() {
        List<EstadoVentaRepuesto> list = new ArrayList<>();
        if (checkPagado.isSelected()) {
            list.add(EstadoVentaRepuesto.PAGADO);
        }
        if (checkPendiente.isSelected()) {
            list.add(EstadoVentaRepuesto.PENDIENTE_PAGO);
        }
        if (checkCancelado.isSelected()) {
            list.add(EstadoVentaRepuesto.CANCELADO);
        }
        return list;
    }

    private void configColumnas() {
        colCodVenta.setCellValueFactory(new PropertyValueFactory<>("codVenta"));
        colEstadoVenta.setCellValueFactory(new PropertyValueFactory<>("estadoVenta"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fechaVenta"));
        colMontoVenta.setCellValueFactory(new PropertyValueFactory<>("montoVenta"));
        colFaltante.setCellValueFactory(new PropertyValueFactory<>("montoFaltante"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("clienteDNI"));

        if (colEstadoVenta != null) {
            colEstadoVenta.setCellFactory(column -> new TableCell<VentaRepuestoVMtabla, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    setStyle("");
                    if (empty || item == null) {
                        return;
                    }
                    setText(item);
                    VentaRepuestoVMtabla vm = getTableView().getItems().get(getIndex());
                    if (vm.estadoVentaProperty().get().equals(EstadoVentaRepuesto.CANCELADO.toString())) {
                        setStyle("-fx-text-fill: #952122; -fx-font-weight: bold; -fx-alignment: CENTER");
                    } else if (vm.estadoVentaProperty().get().equals(EstadoVentaRepuesto.PENDIENTE_PAGO.toString())) {
                        setStyle("-fx-text-fill: #b6aa21; -fx-font-weight: bold; -fx-alignment: CENTER");
                    } else if (vm.estadoVentaProperty().get().equals(EstadoVentaRepuesto.PAGADO.toString())) {
                        setStyle("-fx-text-fill: #026e21; -fx-font-weight: bold; -fx-alignment: CENTER");

                    }
                }
            });
        }
    }

    private void llenarCombos() {
        ObservableList<String> listaTipoOrden = FXCollections.observableArrayList();
        listaTipoOrden.add("Descendente");
        listaTipoOrden.add("Ascendente");
        comboTipoOrden.setItems(listaTipoOrden);
        comboTipoOrden.getSelectionModel().select(0);
        ObservableList<String> listaOrdenPor = FXCollections.observableArrayList();
        listaOrdenPor.add("Fecha");
        listaOrdenPor.add("Monto");
        listaOrdenPor.add("Código");
        comboOrdenarPor.setItems(listaOrdenPor);
        comboOrdenarPor.getSelectionModel().select(0);
    }

    private String tomaOrdenPor() {
        int ordenarPor = comboOrdenarPor.getSelectionModel().getSelectedIndex();
        //los valores q toma son el del atributo de Repuesto.class
        return switch (ordenarPor) {
            case 0 -> "fechaVenta";
            case 1 -> "montoTotal";
            case 2 -> "id";
            default -> "id";
        };
    }

}
