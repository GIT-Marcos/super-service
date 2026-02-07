package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.WizardStateProvider;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.ResultadoPaginado;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.ExportadorTabla;
import SPRService.SPRService.viewModels.VehiculoVM;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.viewModels.tablas.VehiculoRowViewModel;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class VehiculosController implements Initializable {

    private static final int ITEMS_POR_PAGINA = 30;
    private final Navigator navigator;
    private final WizardStateProvider wizardStateProvider;
    private final VehiculoServ vehiculoServ;

    // --- Estado ---
    private final ObservableList<VehiculoRowViewModel> obsListViewModel = FXCollections.observableArrayList();
    private int paginaActual = 0;
    private int totalPaginas = 10;

    // --- Componentes FXML ---
    @FXML
    private TextField tfPatente, tfModelo, tfMarca;
    @FXML
    private TableView<VehiculoRowViewModel> tablaVehiculos;
    @FXML
    private TableColumn<VehiculoRowViewModel, String> colPatente, colMarca, colModelo, colAnio, colColor, colFechaReg;
    @FXML
    private TableColumn<VehiculoRowViewModel, Double> colCil;
    @FXML
    private ComboBox<String> comboFormato;
    @FXML
    private Pagination paginacion;
    @FXML
    private Button btnCancelar, btnRepMod;

    @Inject
    public VehiculosController(AppCoordinator appCoordinator, WizardStateProvider wizardStateProvider,
                               VehiculoServ vehiculoServ) {
        this.navigator = appCoordinator.getMainNavigator();
        this.wizardStateProvider = wizardStateProvider;
        this.vehiculoServ = vehiculoServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configColumnas();
        llenarCombos();
        configurarPaginacion();
        tablaVehiculos.setItems(obsListViewModel);

        // Cargar primera página
        cargarPagina(0);

        configPermisos();
    }

    private void configPermisos() {
        RolUsuario rol = SessionManager.getRolUsuario();
        if (rol == RolUsuario.OPERATIVO_TALLER) {
            btnCancelar.setDisable(true);
            btnRepMod.setDisable(true);
        } else if (rol == RolUsuario.OPERATIVO_RECEPCION) {
            btnRepMod.setDisable(true);
        }
    }

    // ==================== PAGINACIÓN ====================

    private void configurarPaginacion() {
        paginacion.setPageCount(1);
        paginacion.setCurrentPageIndex(0);
        paginacion.setMaxPageIndicatorCount(5);

        // Listener para cambios de página
        paginacion.currentPageIndexProperty().addListener((obs, oldPage, newPage) -> {
            if (newPage != null && !newPage.equals(oldPage)) {
                cargarPagina(newPage.intValue());
            }
        });
    }

    /**
     * Carga una página específica de resultados
     */
    private void cargarPagina(int numeroPagina) {
        String patente = tfPatente.getText() != null ? tfPatente.getText().strip() : "";
        String modelo = tfModelo.getText() != null ? tfModelo.getText().strip() : "";
        String marca = tfMarca.getText() != null ? tfMarca.getText().strip() : "";

        ResultadoPaginado<Vehiculo> resultado = vehiculoServ.buscarPaginado(
                patente, modelo, marca, numeroPagina, ITEMS_POR_PAGINA);

        // Actualizar datos de paginación
        int paginas = (int) Math.ceil((double) resultado.getCantidadResultados() / ITEMS_POR_PAGINA);
        totalPaginas = Math.max(1, paginas);
        paginaActual = numeroPagina;

        // Actualizar el control de paginación
        paginacion.setPageCount(totalPaginas);

        // Actualizar la tabla
        obsListViewModel.clear();
        for (Vehiculo v : resultado.getLista()) {
            obsListViewModel.add(new VehiculoRowViewModel(v));
        }
    }

    /**
     * Recarga la página actual
     */
    private void recargarPaginaActual() {
        cargarPagina(paginaActual);
    }

    // ==================== ACCIONES ====================

    @FXML
    private void buscarConFiltros() {
        paginacion.setCurrentPageIndex(0);
        cargarPagina(0);
    }

    @FXML
    private void todosLosVehiculos() {
        // Limpiar filtros
        tfPatente.clear();
        tfModelo.clear();
        tfMarca.clear();

        paginacion.setCurrentPageIndex(0);
        cargarPagina(0);
    }

    @FXML
    private void cargarVehiculo() {
        Optional<VehiculoVM> result = navigator.openModal(Views.CARGAR_VEHICULO,
                "Cargar nuevo vehículo", null);

        if (result.isPresent()) {
            // Ir a la primera página para ver el nuevo vehículo
            paginacion.setCurrentPageIndex(0);
            cargarPagina(0);
        }
    }

    @FXML
    private void modificarVehiculo() {
        VehiculoRowViewModel vrvm = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            NotificationHelper.mostrarAdvertencia("Modificar vehículo",
                    "Debe seleccionar un vehículo para modificarlo.");
            return;
        }

        wizardStateProvider.startEditVehicleWizard(vrvm.getVehiculo());
        Optional<VehiculoVM> result = navigator.openModal(Views.CARGAR_VEHICULO, "Modificar Vehículo", null);

        if (result.isPresent()) {
            // Actualizar la fila en lugar de recargar toda la página
            int i = obsListViewModel.indexOf(vrvm);
            if (i >= 0) {
                obsListViewModel.set(i, new VehiculoRowViewModel(result.get().obtenerEntidadActualizada()));
            }
        }
    }

    @FXML
    private void verDetalles() {
        VehiculoRowViewModel vrvm = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            NotificationHelper.mostrarAdvertencia("Detalles de vehículo",
                    "Debe seleccionar un vehículo para ver sus detalles.");
            return;
        }

        Optional<Vehiculo> result = vehiculoServ.verDetalle(vrvm.getVehiculo().getId());
        result.ifPresent(v ->
                navigator.openModal(Views.DETALLE_VEHICULO, "Detalles de vehículo", v));
    }

    @FXML
    private void eliminarVehiculo() {
        VehiculoRowViewModel vrvm = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            NotificationHelper.mostrarAdvertencia("Eliminación de vehículo",
                    "Debe seleccionar un vehículo para poder eliminarlo.");
            return;
        }

        Vehiculo v = vrvm.getVehiculo();
        boolean confirmar = SimpleDialogs.confirmacion("Eliminación de vehículo",
                "¿Confirmar eliminación de vehículo?");
        if (!confirmar) return;

        try {
            vehiculoServ.borradoLogico(v);
            // Recargar la página actual para reflejar el cambio
            recargarPaginaActual();
            NotificationHelper.mostrarExito("Eliminación de vehículo",
                    "Se ha eliminado el vehículo con éxito.");
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Eliminación de vehículo",
                    "Ha ocurrido un error al eliminar el vehículo.");
            e.printStackTrace();
        }
    }

    @FXML
    private void exportarTabla(ActionEvent event) {
        if (obsListViewModel.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Exportar tabla actual",
                    "No hay vehículos en la tabla actual para exportar.");
            return;
        }

        FileChooser.ExtensionFilter filter;
        String defaultFileName;
        if (comboFormato.getValue().equals("CSV")) {
            filter = new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv");
            defaultFileName = "tabla_vehiculos.csv";
        } else {
            filter = new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx");
            defaultFileName = "tabla_vehiculos.xlsx";
        }

        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta", defaultFileName, filter);
        if (file == null) return;

        if (comboFormato.getValue().equals("CSV")) {
            ExportadorTabla.exportarVehiculosCSV(obsListViewModel, file);
        } else {
            ExportadorTabla.exportarVehiculosXLSX(obsListViewModel, file);
        }
    }

    @FXML
    private void generarReporteMasRegistrados() {
        navigator.openModal(Views.CHART_VEHICULOS, "Reporte de modelos más registrados", null);
    }

    // ==================== CONFIGURACIÓN ====================

    private void configColumnas() {
        colPatente.setCellValueFactory(new PropertyValueFactory<>("patente"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCil.setCellValueFactory(new PropertyValueFactory<>("cilindrada"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colFechaReg.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
    }

    private void llenarCombos() {
        ObservableList<String> obsFormato = FXCollections.observableArrayList("CSV", "XLSX");
        comboFormato.setItems(obsFormato);
        comboFormato.getSelectionModel().select(0);
    }
}