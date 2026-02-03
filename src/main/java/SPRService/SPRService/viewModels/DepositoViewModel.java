package SPRService.SPRService.viewModels;

import SPRService.SPRService.DTOs.filtros.FiltroRepuestoDTO;
import SPRService.SPRService.util.ResultadoPaginado;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.viewModels.tablas.RepuestoRowViewModel;
import SPRService.SPRService.entities.Repuesto;
import SPRService.SPRService.entities.Stock;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.RepuestoServ;
import SPRService.SPRService.services.StockServ;
import SPRService.SPRService.util.generadores.ExportadorTabla;
import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;

public class DepositoViewModel {

    // --- Dependencias ---
    private final RepuestoServ repuestoServ;
    private final StockServ stockServ;
    private final Navigator navigator;

    // --- Constante de paginación ---
    private static final int ITEMS_POR_PAGINA = 30;

    // --- Propiedades de Estado para la Vista ---
    private final ListProperty<RepuestoRowViewModel> repuestosViewModels =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<RepuestoRowViewModel> selectedRepuesto = new SimpleObjectProperty<>();

    // Filtros
    public final StringProperty codigoFiltro = new SimpleStringProperty("");
    public final StringProperty nombreFiltro = new SimpleStringProperty("");
    public final StringProperty marcaFiltro = new SimpleStringProperty("");
    public final BooleanProperty mostrarNormal = new SimpleBooleanProperty(true);
    public final BooleanProperty mostrarBajo = new SimpleBooleanProperty(true);

    // Ordenamiento
    public final ObservableList<String> ordenarPorOptions =
            FXCollections.observableArrayList("Detalle", "Marca", "Cod Barra", "Precio");
    public final StringProperty selectedOrdenarPor = new SimpleStringProperty(ordenarPorOptions.getFirst());
    public final ObservableList<String> tipoOrdenOptions =
            FXCollections.observableArrayList("Ascendente", "Descendente");
    public final StringProperty selectedTipoOrden = new SimpleStringProperty(tipoOrdenOptions.getFirst());

    // Exportación
    public final ObservableList<String> formatosExportacion = FXCollections.observableArrayList("CSV", "XLSX");
    public final StringProperty selectedFormatoExportacion = new SimpleStringProperty(formatosExportacion.getFirst());

    // Otros estados
    private final BooleanProperty avisoStockBajoVisible = new SimpleBooleanProperty(false);

    // ====== NUEVAS PROPIEDADES DE PAGINACIÓN ======
    private final IntegerProperty paginaActual = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPaginas = new SimpleIntegerProperty(1);
    private final LongProperty totalResultados = new SimpleLongProperty(0);

    @Inject
    public DepositoViewModel(RepuestoServ repuestoServ, StockServ stockServ, AppCoordinator appCoordinator) {
        this.repuestoServ = repuestoServ;
        this.stockServ = stockServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    public void initialize() {
        cargarTodosRepuestos();
    }

    public void cargarTodosRepuestos() {
        codigoFiltro.setValue("");
        nombreFiltro.setValue("");
        marcaFiltro.setValue("");
        mostrarNormal.setValue(true);
        mostrarBajo.setValue(true);
        paginaActual.set(0);
        cargarPagina(0);
    }

    public void buscarConFiltros() {
        paginaActual.set(0);
        cargarPagina(0);
    }

    /**
     * Principal que carga una página específica.
     * Es llamado por el Pagination cuando cambia de página.
     */
    public void cargarPagina(int numeroPagina) {
        if (!mostrarNormal.get() && !mostrarBajo.get()) {
            repuestosViewModels.clear();
            totalPaginas.set(1);
            totalResultados.set(0);
            return;
        }

        FiltroRepuestoDTO filtro = construirFiltro();

        ResultadoPaginado<Repuesto> resultado = repuestoServ.buscarRepuestosPaginado(
                filtro, numeroPagina, ITEMS_POR_PAGINA);

        // Actualizar datos de paginación
        totalResultados.set(resultado.getCantidadResultados());
        int paginas = (int) Math.ceil((double) resultado.getCantidadResultados() / ITEMS_POR_PAGINA);
        totalPaginas.set(Math.max(1, paginas));
        paginaActual.set(numeroPagina);

        // Actualizar la tabla
        repuestosViewModels.setAll(
                resultado.getLista().stream()
                        .map(RepuestoRowViewModel::new)
                        .collect(Collectors.toList())
        );

        verificarBajoStock();
    }

    /**
     * Construye el DTO de filtro basándose en los valores actuales de la UI
     */
    private FiltroRepuestoDTO construirFiltro() {
        String colOrden = switch (selectedOrdenarPor.get()) {
            case "Marca" -> "marcaRepuesto";
            case "Cod Barra" -> "codBarra";
            case "Precio" -> "precio";
            default -> "detalle";
        };
        int tipoOrden = tipoOrdenOptions.indexOf(selectedTipoOrden.get());

        return new FiltroRepuestoDTO(
                codigoFiltro.get(),
                nombreFiltro.get(),
                marcaFiltro.get(),
                mostrarNormal.get(),
                mostrarBajo.get(),
                colOrden,
                tipoOrden
        );
    }

    /**
     * Recarga la página actual (útil después de modificaciones)
     */
    public void recargarPaginaActual() {
        cargarPagina(paginaActual.get());
    }

    public void crearNuevoRepuesto() {
        Optional<Repuesto> result = navigator.openModal(Views.GUARDAR_REPUESTO, "Nuevo repuesto", null);
        result.ifPresent(nuevoRepuesto -> {
            // Recargar la primera página para mostrar el nuevo repuesto
            paginaActual.set(0);
            cargarPagina(0);
        });
    }

    public void modificarRepuesto(RepuestoRowViewModel vm) {
        Optional<Repuesto> result = navigator.openModal(
                Views.GUARDAR_REPUESTO, "Modificar repuesto", vm.getRepuestoOriginal());
        result.ifPresent(r -> {
            vm.updateFrom(r);
            verificarBajoStock();
        });
    }

    public void borrarRepuesto(RepuestoRowViewModel vm) {
        repuestoServ.borrarRepuesto(vm.getRepuestoOriginal());
        // Recargar la página actual para reflejar el cambio
        recargarPaginaActual();
    }

    public void ingresarStock(RepuestoRowViewModel vm, Double cantidad) {
        Repuesto r = vm.getRepuestoOriginal();
        Stock stock = stockServ.agregarExistente(r.getStock(), cantidad);
        r.setStock(stock);
        vm.updateFrom(r);
        verificarBajoStock();
    }

    public void generarReporteMasRetiradosParaVenta() {
        navigator.openModal(Views.CHART_MAS_RETIRADOS, "Repuestos más retirados", null);
    }

    public void exportarTabla(ActionEvent event) {
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
    private void verificarBajoStock() {
        avisoStockBajoVisible.set(repuestoServ.contarStockBajo() > 0);
    }

    // --- Getters para las Propiedades ---
    public ListProperty<RepuestoRowViewModel> repuestosViewModelsProperty() {
        return repuestosViewModels;
    }

    public ObjectProperty<RepuestoRowViewModel> selectedRepuestoProperty() {
        return selectedRepuesto;
    }

    public BooleanProperty avisoStockBajoVisibleProperty() {
        return avisoStockBajoVisible;
    }

    // ====== GETTERS DE PAGINACIÓN ======
    public IntegerProperty paginaActualProperty() {
        return paginaActual;
    }

    public IntegerProperty totalPaginasProperty() {
        return totalPaginas;
    }

    public LongProperty totalResultadosProperty() {
        return totalResultados;
    }

    public int getItemsPorPagina() {
        return ITEMS_POR_PAGINA;
    }
}