package SPRService.SPRService.viewModels;

import SPRService.SPRService.DTOs.RepuestoRetiradoReporteDTO;
import SPRService.SPRService.util.generadores.GeneradorReportes;
import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.services.StockServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.Alertas;
import SPRService.SPRService.util.generadores.ExportadorTabla;
import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Idealmente, un ViewModel no debería depender directamente de clases que muestran UI.
 * Una mejora avanzada sería crear una interfaz (IUserInteraction o IDialogService) que el ViewModel usaría,
 * y el Controller proveería la implementación concreta usando Alertas.
 */
public class DepositoViewModel {

    // --- Dependencias (Inyectadas desde el Modelo y la Infraestructura) ---
    private final RepuestoServ repuestoServ;
    private final StockServ stockServ;
    private final Navigator navigator;

    // --- Propiedades de Estado para la Vista (Data Binding) ---
    private final ListProperty<RepuestoRowViewModel> repuestosViewModels = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<RepuestoRowViewModel> selectedRepuesto = new SimpleObjectProperty<>();

    // Filtros
    public final StringProperty codigoFiltro = new SimpleStringProperty("");
    public final StringProperty nombreFiltro = new SimpleStringProperty("");
    public final StringProperty marcaFiltro = new SimpleStringProperty("");
    public final BooleanProperty mostrarNormal = new SimpleBooleanProperty(true);
    public final BooleanProperty mostrarBajo = new SimpleBooleanProperty(true);

    // Ordenamiento
    public final ObservableList<String> ordenarPorOptions = FXCollections.observableArrayList("Detalle", "Marca", "Cod Barra", "Precio");
    public final StringProperty selectedOrdenarPor = new SimpleStringProperty(ordenarPorOptions.get(0));
    public final ObservableList<String> tipoOrdenOptions = FXCollections.observableArrayList("Ascendente", "Descendente");
    public final StringProperty selectedTipoOrden = new SimpleStringProperty(tipoOrdenOptions.get(0));

    // Exportación
    public final ObservableList<String> formatosExportacion = FXCollections.observableArrayList("CSV", "XLSX");
    public final StringProperty selectedFormatoExportacion = new SimpleStringProperty(formatosExportacion.get(0));

    // Otros estados
    private final BooleanProperty avisoStockBajoVisible = new SimpleBooleanProperty(false);


    @Inject
    public DepositoViewModel(RepuestoServ repuestoServ, StockServ stockServ, AppCoordinator appCoordinator) {
        this.repuestoServ = repuestoServ;
        this.stockServ = stockServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    public void initialize() {
        cargarTodosRepuestos();
    }

    // --- Acciones (Métodos públicos llamados por el Controller) ---

    public void cargarTodosRepuestos() {
        List<Repuesto> todos = repuestoServ.verTodos();
        actualizarTabla(todos);
    }

    public void buscarConFiltros() {
        if (!mostrarNormal.get() && !mostrarBajo.get()) {
            repuestosViewModels.clear();
            return;
        }

        String colOrden = switch (selectedOrdenarPor.get()) {
            case "Marca" -> "marca";
            case "Cod Barra" -> "codBarra";
            case "Precio" -> "precio";
            default -> "detalle";
        };
        int tipoOrden = tipoOrdenOptions.indexOf(selectedTipoOrden.get());

        List<Repuesto> resultado = repuestoServ.buscarConCriteria(
                codigoFiltro.get(), nombreFiltro.get(), marcaFiltro.get(),
                mostrarNormal.get(), mostrarBajo.get(), colOrden, tipoOrden
        );
        actualizarTabla(resultado);
    }

    public void crearNuevoRepuesto() {
        Optional<Repuesto> result = navigator.openModal(Views.GUARDAR_REPUESTO, "Nuevo repuesto", null);
        result.ifPresent(nuevoRepuesto -> {
            repuestosViewModels.addFirst(new RepuestoRowViewModel(nuevoRepuesto));
            verificarBajoStock();
        });
    }

    public void modificarRepuesto() {
        RepuestoRowViewModel rrvm = selectedRepuesto.get();
        if (rrvm == null) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Modificar repuesto")
                    .text("Debe seleccionar un repuesto para modificar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        Optional<Repuesto> result = navigator.openModal(Views.GUARDAR_REPUESTO, "Modificar repuesto", rrvm.getRepuestoOriginal());
        result.ifPresent(r -> {
            rrvm.updateFrom(r);
            verificarBajoStock();
        });
    }

    public void borrarRepuesto() {
        RepuestoRowViewModel seleccionado = selectedRepuesto.get();
        if (seleccionado == null) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Borrar repuesto")
                    .text("Debe seleccionar un repuesto para borrar.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        Repuesto repBorrar = seleccionado.getRepuestoOriginal();
        if (!Alertas.confirmacion("Borrar repuesto", "Esta acción es irreversible.\n¿Desea continuar con el borrado?") ||
                !Alertas.confirmacion("Borrar repuesto", "¿Confirmar borrado de:\n" + repBorrar.getDetalle() + " ?")) {
            return;
        }

        try {
            repuestoServ.borrarRepuesto(repBorrar);
            repuestosViewModels.remove(seleccionado);
            verificarBajoStock();
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Borrar repuesto")
                    .text("Se ha borrado el repuesto con éxito.")
                    .position(Pos.BOTTOM_RIGHT)
                    .showInformation();
        } catch (RuntimeException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Borrar repuesto")
                    .text("Ha ocurrido un error al borrar el repuesto.")
                    .position(Pos.CENTER)
                    .showError();
        }
    }

    public void ingresarStock() {
        RepuestoRowViewModel repuestoSeleccionado = selectedRepuesto.get();
        if (repuestoSeleccionado == null) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Ingresar stock")
                    .text("Debe seleccionar un repuesto para ingresarle stock.")
                    .position(Pos.CENTER)
                    .showWarning();
            return;
        }

        try {
            Double cantidad = SimpleDialogs.inputStock();
            if (cantidad == null) return;

            Repuesto r = repuestoSeleccionado.getRepuestoOriginal();
            Stock stock = stockServ.agregarExistente(r.getStock(), cantidad);
            r.setStock(stock);
            repuestoSeleccionado.updateFrom(r);
            verificarBajoStock();
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Ingresar stock")
                    .text("Se ha agregado stock con éxito.")
                    .position(Pos.CENTER)
                    .showInformation();
        } catch (IllegalArgumentException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Ingresar stock")
                    .text(e.getMessage())
                    .position(Pos.BOTTOM_RIGHT)
                    .showWarning();
        } catch (RuntimeException e) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Ingresar stock")
                    .text("Ha ocurrido un error al agregar stock.")
                    .position(Pos.CENTER)
                    .showError();
        }
    }

    // TODO: el vm no debe conocer las clases de javaFX
    public void generarReporteMasRetiradosParaVenta(ActionEvent event) {
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
                "reporte productos más retirados.jpg",
                new FileChooser.ExtensionFilter("Imágenes JPG (*.jpg, *.jpeg)", "*.jpg", "*.jpeg"));
        if (file == null) return;
        List<RepuestoRetiradoReporteDTO> reportesDTOs = repuestoServ.repuestosMasRetiradosParaVenta(cantidad,
                fechaMin, fechaMax);
        GeneradorReportes.repuestosMasRetiradosEnMes(file, reportesDTOs, fechaMin, fechaMax);
    }

    // TODO: el vm no debe conocer las clases de javaFX
    public void exportarTabla(ActionEvent event) {
        if (repuestosViewModels.isEmpty()) {
            Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .title("Generar tabla")
                    .text("No hay repuestos para generar la tabla.")
                    .position(Pos.CENTER)
                    .showInformation();
            return;
        }

        FileChooser.ExtensionFilter filter;
        String defaultFileName;

        if (selectedFormatoExportacion.get().equals("CSV")) {
            filter = new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv");
            defaultFileName = "tabla_repuestos.csv";
        } else {
            filter = new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx");
            defaultFileName = "tabla_repuestos.xlsx";
        }

        File file = SimpleDialogs.selectorRuta(event, "Seleccione la ruta", defaultFileName, filter);
        if (file == null) return;

        if (selectedFormatoExportacion.get().equals("CSV")) {
            ExportadorTabla.exportarRepuestosCSV(repuestosViewModels, file);
        } else {
            ExportadorTabla.exportarRepuestosXLSX(repuestosViewModels, file);
        }
    }

    public void reporteDeUso() {
        navigator.openModal(Views.CHART_USO_REPUESTOS, "Reporte de uso", null);
    }

    // --- Lógica Privada ---
    private void actualizarTabla(List<Repuesto> listaRepuestos) {
        repuestosViewModels.setAll(
                listaRepuestos.stream()
                        .map(RepuestoRowViewModel::new)
                        .collect(Collectors.toList())
        );
        verificarBajoStock();
    }

    private void verificarBajoStock() {
        avisoStockBajoVisible.set(repuestoServ.contarStockBajo() > 0);
    }

    // --- Getters para las Propiedades (para el binding en el Controller) ---
    public ListProperty<RepuestoRowViewModel> repuestosViewModelsProperty() {
        return repuestosViewModels;
    }

    public ObjectProperty<RepuestoRowViewModel> selectedRepuestoProperty() {
        return selectedRepuesto;
    }

    public BooleanProperty avisoStockBajoVisibleProperty() {
        return avisoStockBajoVisible;
    }
}
