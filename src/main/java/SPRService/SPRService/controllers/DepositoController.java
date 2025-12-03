package SPRService.SPRService.controllers;

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
import javafx.stage.FileChooser;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositoController implements Initializable {

    private final DepositoViewModel viewModel;

    @Inject
    public DepositoController(DepositoViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // --- Componentes FXML (sin cambios) ---
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Configurar la UI que no se puede hacer con binding
        configColumnas();
        seteaEstiloTabla();

        // 2. Enlazar (bind) la UI a las propiedades del ViewModel
        bindViewModel();

        // 3. Cargar los datos iniciales
        viewModel.initialize();
    }

    private void bindViewModel() {
        // Enlazar la tabla
        tablaRepuestos.itemsProperty().bind(viewModel.repuestosViewModelsProperty());
        viewModel.selectedRepuestoProperty().bind(tablaRepuestos.getSelectionModel().selectedItemProperty());

        // Enlazar filtros (bidireccional para que el ViewModel sepa los cambios de la UI)
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
    }

    // --- Métodos de acción (simples delegaciones) ---
    @FXML
    private void buscarConFiltros() {
        viewModel.buscarConFiltros();
    }

    @FXML
    private void todosRepuestos() {
        viewModel.cargarTodosRepuestos();
    }

    @FXML
    private void nuevoRepuesto() {
        viewModel.crearNuevoRepuesto();
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
        if (!SimpleDialogs.confirmacion("Borrar repuesto", "Esta acción es irreversible.\n¿Desea continuar con el borrado?"))
            return;
        if (!SimpleDialogs.confirmacion("Borrar repuesto", "¿Confirmar borrado de:\n" + vm.getNombre() + " ?") )
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
    private void masRetiradosParaVenta(ActionEvent event) {
        if (tablaRepuestos.getItems().isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Exportar tabla", "No hay repuestos para generar la tabla.");
            return;
        }
        viewModel.generarReporteMasRetiradosParaVenta(event);
    }

    @FXML
    private void usoDeRepuestos() {
        viewModel.reporteDeUso();
    }

    @FXML
    private void generar(ActionEvent event) {
        viewModel.exportarTabla(event);
    }

    // --- Configuración de la Vista (se queda en el Controller) ---
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

            // 1. Define la lógica de actualización de estilo en un listener.
            // Usamos InvalidationListener porque no nos importa el valor viejo/nuevo,
            // solo saber que "algo cambió".
            final javafx.beans.InvalidationListener listener = observable -> {
                RepuestoRowViewModel item = row.getItem();
                // Asegúrate de que la fila no esté vacía y tenga un item
                if (item != null) {
                    if (item.getCantidad() <= item.getCantidadMinima()) {
                        row.setStyle("-fx-background-color: lightcoral;");
                    } else {
                        row.setStyle(""); // Restablece el estilo si el stock es normal
                    }
                } else {
                    // Si la fila está vacía, asegúrate de que no tenga estilo
                    row.setStyle("");
                }
            };

            // 2. Escucha los cambios en el "item" que se muestra en la fila.
            // Esto es crucial para agregar/quitar listeners de las propiedades del item.
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                // Si había un item antiguo, deja de escuchar sus propiedades
                if (oldItem != null) {
                    oldItem.cantidadProperty().removeListener(listener);
                    oldItem.cantidadMinimaProperty().removeListener(listener);
                }
                // Si hay un item nuevo, empieza a escuchar sus propiedades
                if (newItem != null) {
                    newItem.cantidadProperty().addListener(listener);
                    newItem.cantidadMinimaProperty().addListener(listener);

                    // Llama al listener una vez para establecer el color inicial
                    listener.invalidated(null);
                } else {
                    // Si la fila se queda vacía, limpia el estilo
                    row.setStyle("");
                }
            });

            return row;
        });
    }
}
