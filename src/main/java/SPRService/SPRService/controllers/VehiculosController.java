package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Vehiculo;
import SPRService.SPRService.navigation.WizardStateProvider;
import SPRService.SPRService.services.VehiculoServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.ExportadorTabla;
import SPRService.SPRService.util.generadores.GeneradorReportes;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class VehiculosController implements Initializable {

    private final Navigator navigator;
    private final WizardStateProvider wizardStateProvider;
    private ObservableList<VehiculoRowViewModel> obsListViewModel = FXCollections.observableArrayList();
    private final VehiculoServ vehiculoServ;

    @FXML
    private TextField tfPatente;
    @FXML
    private TextField tfModelo;
    @FXML
    private TextField tfMarca;
    @FXML
    private TableView<VehiculoRowViewModel> tablaVehiculos;
    @FXML
    private TableColumn<VehiculoRowViewModel, String> colPatente;
    @FXML
    private TableColumn<VehiculoRowViewModel, String> colMarca;
    @FXML
    private TableColumn<VehiculoRowViewModel, String> colModelo;
    @FXML
    private TableColumn<VehiculoRowViewModel, Double> colCil;
    @FXML
    private TableColumn<VehiculoRowViewModel, String> colAnio;
    @FXML
    private TableColumn<VehiculoRowViewModel, String> colColor;
    @FXML
    private ComboBox<String> comboFormato;

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
        tablaVehiculos.setItems(obsListViewModel);
        crearFilas(vehiculoServ.verTodosActivos());

        obsListViewModel.addAll();
    }

    @FXML
    private void buscarConFiltros() {
        List<Vehiculo> vehiculos = vehiculoServ.buscarPor(tfPatente.getText().strip(),
                tfModelo.getText().strip(), tfMarca.getText().strip());
        crearFilas(vehiculos);
    }

    @FXML
    private void todosLosVehiculos() {
        crearFilas(vehiculoServ.verTodosActivos());
    }

    @FXML
    private void cargarVehiculo() {
        Optional<VehiculoVM> result = navigator.openModal(Views.CARGAR_VEHICULO,
                "Cargar nuevo vehículo", null);
        result.ifPresent(vehiculoVM -> obsListViewModel.addFirst(
                new VehiculoRowViewModel(vehiculoVM.obtenerEntidadActualizada())));
    }

    @FXML
    private void modificarVehiculo() {
        VehiculoRowViewModel vrvm = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            Alertas.aviso("Modificar vehículo", "Debe seleccionar un vehículo para modificarlo.");
            return;
        }
        wizardStateProvider.startEditVehicleWizard(vrvm.getVehiculo());
        Optional<VehiculoVM> result = navigator.openModal(Views.CARGAR_VEHICULO, "Modificar Vehículo", null);
        if (result.isPresent()) {
            int i = obsListViewModel.indexOf(vrvm);
            obsListViewModel.set(i, new VehiculoRowViewModel(result.get().obtenerEntidadActualizada()));
        }
    }

    @FXML
    private void verDetalles() {
        VehiculoRowViewModel vrvm = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            Alertas.aviso("Detalles de vehículo", "Debe seleccionar un vehículo para ver " +
                    "sus detalles.");
            return;
        }
        navigator.openModal(Views.DETALLE_VEHICULO, "Detalles de vehículo", vrvm.getVehiculo());
    }

    @FXML
    private void eliminarVehiculo() {
        VehiculoRowViewModel vrvm = tablaVehiculos.getSelectionModel().getSelectedItem();
        if (vrvm == null) {
            Alertas.aviso("Eliminación de vehículo", "Debe seleccionar un vehículo para poder " +
                    "eliminarlo.");
            return;
        }
        Vehiculo v = vrvm.getVehiculo();
        boolean r = Alertas.confirmacion("Eliminación de vehículo",
                "¿Confirmar eliminación de vehículo?");
        if (!r) return;
        try {
            vehiculoServ.borradoLogico(v);
            obsListViewModel.remove(vrvm);
            Alertas.exito("Eliminación de vehículo", "Se ha eliminado el vehículo con éxito.");
        } catch (RuntimeException e) {
            e.printStackTrace();
            Alertas.error("Eliminación de vehículo", "Ha ocurrido un error al eliminar el vehículo.");

        }
    }

    @FXML
    private void exportarTabla(ActionEvent event) {
        if (obsListViewModel.isEmpty()) {
            Alertas.aviso("Exportar tabla actual", "No hay repuestos en la tabla catual para exportar.");
            return;
        }

        FileChooser.ExtensionFilter filter;
        String defaultFileName;
        if (comboFormato.getValue().equals("CSV")) {
            filter = new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv");
            defaultFileName = "tabla_repuestos.csv";
        } else {
            filter = new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx");
            defaultFileName = "tabla_repuestos.xlsx";
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
    private void generarReporteMasRegistrados(ActionEvent event) {
        LocalDate fechaMin;
        LocalDate fechaMax;

        Integer cantidad = SimpleDialogs.inputEntero();
        if (cantidad == null) return;

        Optional<LocalDate[]> result = navigator.openModal(Views.SELECTOR_FECHA_REPORTE,
                "Generar reporte", null);
        if (result.isPresent()) {
            fechaMin = result.get()[0];
            fechaMax = result.get()[1];
        } else {
            return;
        }

        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta para la generación del reporte",
                "Reporte modelos más registrados.jpg",
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) {
            return;
        }
        GeneradorReportes.modelosMasRegistrados(file, vehiculoServ.generarReporteModelosMasRegistrados( cantidad,
                fechaMin, fechaMax
        ));
    }

    private void crearFilas(List<Vehiculo> vehiculos) {
        obsListViewModel.clear();
        for (Vehiculo v : vehiculos) {
            obsListViewModel.add(new VehiculoRowViewModel(v));
        }
    }

    private void configColumnas() {
        colPatente.setCellValueFactory(new PropertyValueFactory<>("patente"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colCil.setCellValueFactory(new PropertyValueFactory<>("cilindrada"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
    }

    private void llenarCombos() {
        ObservableList<String> obsFormato = FXCollections.observableArrayList();
        obsFormato.add("CSV");
        obsFormato.add("XLSX");
        comboFormato.setItems(obsFormato);
        comboFormato.getSelectionModel().select(0);
    }

}
