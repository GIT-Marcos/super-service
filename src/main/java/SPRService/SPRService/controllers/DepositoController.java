package SPRService.SPRService.controllers;

import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.DepositoViewModel;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositoController implements Initializable {

    private final DepositoViewModel viewModel;

    @Inject
    public DepositoController(DepositoViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    private TableView<RepuestoRowViewModel> tablaRepuestos;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colCodBarra, colDetalle, colMarca, colPrecio;
    @FXML
    private TableColumn<RepuestoRowViewModel, String> colCantidad, colCantidadMinima, colUniMedida;
    @FXML
    private ComboBox<String> comboOrdenarPor, comboTipoOrden, comboFormatos;
    @FXML
    private CheckBox checkMostrarNormal, checkMostrarBajo;
    @FXML
    private TextField tfCodigo, tfNombre, tfMarca;
    @FXML
    private Label labelAvisoStock;
    @FXML
    private Pagination paginacion;
    @FXML
    private Button btnBaja, btnRepMasRetirados, btnRepUsos;

    private void configPermisos() {
        RolUsuario rol = SessionManager.getRolUsuario();
        if (rol == RolUsuario.OPERATIVO_DEPOSITO) {
            btnBaja.setDisable(true);
            btnRepUsos.setDisable(true);
            btnRepMasRetirados.setDisable(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configColumnas();
        seteaEstiloTabla();
        configurarPaginacion();
        bindViewModel();
        viewModel.initialize();
        configPermisos();
    }

    private void bindViewModel() {
        // Enlazar la tabla
        tablaRepuestos.itemsProperty().bind(viewModel.repuestosViewModelsProperty());
        viewModel.selectedRepuestoProperty().bind(tablaRepuestos.getSelectionModel().selectedItemProperty());

        // Enlazar filtros
        tfCodigo.textProperty().bindBidirectional(viewModel.codigoFiltro);
        tfNombre.textProperty().bindBidirectional(viewModel.nombreFiltro);
        tfMarca.textProperty().bindBidirectional(viewModel.marcaFiltro);
        checkMostrarNormal.selectedProperty().bindBidirectional(viewModel.mostrarNormal);
        checkMostrarBajo.selectedProperty().bindBidirectional(viewModel.mostrarBajo);

        // Enlazar combos
        comboOrdenarPor.setItems(viewModel.ordenarPorOptions);
        comboOrdenarPor.valueProperty().bindBidirectional(viewModel.selectedOrdenarPor);
        comboTipoOrden.setItems(viewModel.tipoOrdenOptions);
        comboTipoOrden.valueProperty().bindBidirectional(viewModel.selectedTipoOrden);
        comboFormatos.setItems(viewModel.formatosExportacion);
        comboFormatos.valueProperty().bindBidirectional(viewModel.selectedFormatoExportacion);

        // Enlazar otros elementos
        labelAvisoStock.visibleProperty().bind(viewModel.avisoStockBajoVisibleProperty());

        // Enlazar paginación
        paginacion.pageCountProperty().bind(viewModel.totalPaginasProperty());
        paginacion.currentPageIndexProperty().addListener((obs, oldPage, newPage) -> {
            if (newPage != null && !newPage.equals(oldPage)) {
                viewModel.cargarPagina(newPage.intValue());
            }
        });
    }

    private void configurarPaginacion() {
        paginacion.setPageCount(1);
        paginacion.setCurrentPageIndex(0);
        paginacion.setMaxPageIndicatorCount(10); // Número de botones de página visibles

        // PageFactory para crear el contenido de cada página
        // Retornamos un nodo vacío, ya que la tabla se actualiza vía binding
        paginacion.setPageFactory(pageIndex -> {
            // No necesitamos hacer nada aquí porque el listener
            // de currentPageIndex ya llama a cargarPagina()
            return new Label(""); // Nodo invisible
        });
    }

    // --- Métodos de acción ---
    @FXML
    private void buscarConFiltros() {
        paginacion.setCurrentPageIndex(0); // Resetear a primera página
        viewModel.buscarConFiltros();
    }

    @FXML
    private void todosRepuestos() {
        paginacion.setCurrentPageIndex(0); // Resetear a primera página
        viewModel.cargarTodosRepuestos();
    }

    @FXML
    private void nuevoRepuesto() {
        viewModel.crearNuevoRepuesto();
        // Después de crear, ir a la primera página
        paginacion.setCurrentPageIndex(0);
    }

    @FXML
    private void modRepuesto() {
        RepuestoRowViewModel vm = tablaRepuestos.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Detalles/Modificar",
                    "Debe seleccionar un repuesto.");
            return;
        }
        viewModel.modificarRepuesto(vm);
    }

    @FXML
    private void borrarRepuesto() {
        RepuestoRowViewModel vm = tablaRepuestos.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Borrar", "Seleccione un repuesto para borrarlo.");
            return;
        }
        if (!SimpleDialogs.confirmacion("Borrar repuesto",
                "Esta acción es irreversible.\n¿Desea continuar con el borrado?"))
            return;
        if (!SimpleDialogs.confirmacion("Borrar repuesto",
                "¿Confirmar borrado de:\n" + vm.getNombre() + " ?"))
            return;

        try {
            viewModel.borrarRepuesto(vm);
            NotificationHelper.mostrarExito("Borrar repuesto", "Se ha borrado el repuesto con éxito.");
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Borrar repuesto", "Ha ocurrido un error al borrar.");
            e.printStackTrace();
        }
    }

    @FXML
    private void ingresarStock() {
        RepuestoRowViewModel vm = tablaRepuestos.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Ingresar stock",
                    "Debe seleccionar un repuesto para ingresarle stock.");
            return;
        }

        Double cantidad = SimpleDialogs.inputStock();
        if (cantidad == null) return;
        try {
            viewModel.ingresarStock(vm, cantidad);
            NotificationHelper.mostrarExito("Ingreso de stock", "Se ha ingresado el stock con éxito.");
        } catch (IllegalArgumentException e) {
            NotificationHelper.mostrarAdvertencia("Ingreso de stock", e.getMessage());
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Ingreso de stock", "Ha ocurrido un error inesperado.");
            e.printStackTrace();
        }
    }

    @FXML
    private void masRetiradosParaVenta() {
        viewModel.generarReporteMasRetiradosParaVenta();
    }

    @FXML
    private void usoDeRepuestos() {
        viewModel.reporteDeUso();
    }

    @FXML
    private void generar(ActionEvent event) {
        viewModel.exportarTabla(event);
    }

    private void configColumnas() {
        colCodBarra.setCellValueFactory(new PropertyValueFactory<>("coBarra"));
        colDetalle.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidadMinima.setCellValueFactory(new PropertyValueFactory<>("cantidadMinima"));
        colUniMedida.setCellValueFactory(new PropertyValueFactory<>("uniMedida"));
    }

    private void seteaEstiloTabla() {
        tablaRepuestos.setRowFactory(tv -> {
            TableRow<RepuestoRowViewModel> row = new TableRow<>();

            final javafx.beans.InvalidationListener listener = observable -> {
                RepuestoRowViewModel item = row.getItem();
                if (item != null) {
                    if (item.getCantidad() <= item.getCantidadMinima()) {
                        row.setStyle("-fx-background-color: lightcoral;");
                    } else {
                        row.setStyle("");
                    }
                } else {
                    row.setStyle("");
                }
            };

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (oldItem != null) {
                    oldItem.cantidadProperty().removeListener(listener);
                    oldItem.cantidadMinimaProperty().removeListener(listener);
                }
                if (newItem != null) {
                    newItem.cantidadProperty().addListener(listener);
                    newItem.cantidadMinimaProperty().addListener(listener);
                    listener.invalidated(null);
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });
    }
}