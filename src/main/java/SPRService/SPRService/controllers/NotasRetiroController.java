package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.util.generadores.GeneradorTXT;
import SPRService.SPRService.util.generadores.Impresor;
import SPRService.SPRService.viewModels.tablas.NotaRetiroViewModel;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class NotasRetiroController implements Initializable {

    private final NotaRetiroServ notaRetiroServ;
    private final Navigator navigator;
    private final ObservableList<NotaRetiroViewModel> notasObsList = FXCollections.observableArrayList();
    private static final int ITEMS_POR_PAGINA_NOTAS = 40;

    @FXML
    private TableView<NotaRetiroViewModel> tablaNotas;
    @FXML
    private TableColumn<NotaRetiroViewModel, Long> colNotaId;
    @FXML
    private TableColumn<NotaRetiroViewModel, String> colNotaTipo;
    @FXML
    private TableColumn<NotaRetiroViewModel, String> colNotaFecha;
    @FXML
    private TableColumn<NotaRetiroViewModel, String> colNotaEstado;
    @FXML
    private Pagination paginacion;
    @FXML
    private DatePicker dateFechaMin, dateFechaMax;
    @FXML
    private CheckBox chkVentas, chkService, chkActivas, chkInactivas, chkOtro;

    @Inject
    public NotasRetiroController(NotaRetiroServ notaRetiroServ, AppCoordinator appCoordinator) {
        this.notaRetiroServ = notaRetiroServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarDatePickers();
        configurarTabla();
        // Configurar Paginación (esto disparará la primera carga)
        paginacion.setPageFactory(this::cargarPagina);
    }

    private void configurarDatePickers() {
        if (dateFechaMin != null) dateFechaMin.setConverter(new SafeLocalDateConverter());
        if (dateFechaMax != null) dateFechaMax.setConverter(new SafeLocalDateConverter());
    }

    private void configurarTabla() {
        tablaNotas.setItems(this.notasObsList);

        if (colNotaId != null)
            colNotaId.setCellValueFactory(cell -> cell.getValue().idNotaProperty().asObject());

        if (colNotaTipo != null)
            colNotaTipo.setCellValueFactory(cell -> cell.getValue().tipoProperty());

        if (colNotaFecha != null)
            colNotaFecha.setCellValueFactory(cell -> cell.getValue().fechaProperty());

        if (colNotaEstado != null) {
            colNotaEstado.setCellValueFactory(cell -> cell.getValue().estadoProperty());

            colNotaEstado.setCellFactory(column -> new TableCell<NotaRetiroViewModel, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        NotaRetiroViewModel vm = getTableView().getItems().get(getIndex());
                        if (vm.esAnuladaProperty().get()) {
                            setStyle("-fx-text-fill: #d63031; -fx-font-weight: bold;"); // Rojo
                        } else {
                            setStyle("-fx-text-fill: #00b894; -fx-font-weight: bold;"); // Verde
                        }
                    }
                }
            });
        }
    }

    /**
     * Invocado por el control de Paginación.
     * Construye el DTO FiltroNotaRetiro y consulta al servicio.
     */
    private Node cargarPagina(int indicePagina) {
        // 1. Obtener fechas
        LocalDate fechaMin = (dateFechaMin != null) ? dateFechaMin.getValue() : null;
        LocalDate fechaMax = (dateFechaMax != null) ? dateFechaMax.getValue() : null;

        // 2. Obtener estados de los CheckBox (Null safety)
        boolean verVentas = chkVentas != null && chkVentas.isSelected();
        boolean verService = chkService != null && chkService.isSelected();
        boolean verOtro = chkOtro != null && chkOtro.isSelected();
        boolean verActivas = chkActivas != null && chkActivas.isSelected();
        boolean verInactivas = chkInactivas != null && chkInactivas.isSelected();

        // 3. OPTIMIZACIÓN: Si no hay nada seleccionado, no llamar a la BD.
        boolean ningunTipoSeleccionado = !verVentas && !verService && !verOtro;
        boolean ningunEstadoSeleccionado = !verActivas && !verInactivas;

        if (ningunTipoSeleccionado || ningunEstadoSeleccionado) {
            notasObsList.clear();
            paginacion.setPageCount(1);
            return new VBox();
        }

        // 4. Construir el SET de Tipos de Uso
        Set<NotaRetiro.TipoUsoRetiro> tiposSeleccionados = new HashSet<>();
        if (verVentas) tiposSeleccionados.add(NotaRetiro.TipoUsoRetiro.VENTA);
        if (verService) tiposSeleccionados.add(NotaRetiro.TipoUsoRetiro.SERVICE);
        if (verOtro) tiposSeleccionados.add(NotaRetiro.TipoUsoRetiro.OTRO);

        // 5. Determinar filtro de Estado (Boolean o Null)
        Boolean estadoFiltro = null;
        if (verActivas && !verInactivas) {
            estadoFiltro = Boolean.TRUE;
        } else if (!verActivas) {
            estadoFiltro = Boolean.FALSE;
        }

        // 6. CREAR EL DTO
        FiltroNotaRetiro filtro = new FiltroNotaRetiro(
                fechaMin,
                fechaMax,
                estadoFiltro,
                tiposSeleccionados
        );

        // 7. Consultar Servicio usando el DTO
        ResultadoPaginado<NotaRetiro> resultado = notaRetiroServ.buscarPaginado(
                filtro,
                indicePagina,
                ITEMS_POR_PAGINA_NOTAS
        );

        // 8. Actualizar UI (Paginación y Tabla)
        long totalItems = resultado.getCantidadResultados();
        long totalPaginas = (totalItems + ITEMS_POR_PAGINA_NOTAS - 1) / ITEMS_POR_PAGINA_NOTAS;
        paginacion.setPageCount(totalPaginas == 0 ? 1 : (int) totalPaginas);

        List<NotaRetiroViewModel> listaViewModels = resultado.getLista().stream()
                .map(NotaRetiroViewModel::new)
                .collect(Collectors.toList());

        notasObsList.setAll(listaViewModels);

        return new VBox();
    }

    @FXML
    private void aplicarFiltros() {
        paginacion.setCurrentPageIndex(0);
        if (paginacion.getCurrentPageIndex() == 0) {
            cargarPagina(0);
        }
    }

    @FXML
    private void verTodas() {
        // Resetear controles
        if (dateFechaMin != null) dateFechaMin.setValue(null);
        if (dateFechaMax != null) dateFechaMax.setValue(null);
        if (chkVentas != null) chkVentas.setSelected(true);
        if (chkService != null) chkService.setSelected(true);
        if (chkOtro != null) chkOtro.setSelected(true);
        if (chkActivas != null) chkActivas.setSelected(true);
        if (chkInactivas != null) chkInactivas.setSelected(true);
        aplicarFiltros();
    }

    @FXML
    private void nuevaNota() {
        Optional<NotaRetiro> result = navigator.openModal(Views.CARGAR_NOTA, "Nueva Nota de Retiro", null);
        result.ifPresent(nota -> cargarPagina(paginacion.getCurrentPageIndex()));
    }

    @FXML
    private void verDetalles() {
        NotaRetiroViewModel vm = tablaNotas.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Ver detalles", "Debe seleccionar una nota para ver sus detalles.");
            return;
        }

        Optional<NotaRetiro> result = notaRetiroServ.verDetalle(vm.idNotaProperty().get());
        result.ifPresent(n ->
                navigator.openModal(Views.DETALLE_NOTA_RETIRO, "Detalles de Nota #" + vm.idNotaProperty().getValue(),
                        new NotaRetiroViewModel(n)));
    }

    @FXML
    private void cancelarNota() {
        NotaRetiroViewModel vm = tablaNotas.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Cancelar nota", "Debe seleccionar una nota para cancelarla.");
            return;
        }
        if (vm.esAnuladaProperty().get()) {
            NotificationHelper.mostrarAdvertencia("Cancelar nota", "No se puede cancelar una nota ya cancelada.");
            return;
        }
        if (!SimpleDialogs.confirmacion("Cancelar nota de retiro",
                "Esta acción es irreversible y el stock se restablecerá.\n¿Confirmar cancelación?")) {
            return;
        }
        try {
            notaRetiroServ.cancelarNota(vm.idNotaProperty().get());
            NotificationHelper.mostrarExito("Nota cancelada", "Se ha cancelado la nota con éxito.");
            cargarPagina(paginacion.getCurrentPageIndex());
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Error al cancelar", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void generarTicket(ActionEvent event) {
        NotaRetiroViewModel vm = tablaNotas.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Generar ticket", "Debe seleccionar una nota para generar el ticket.");
            return;
        }
        Optional<NotaRetiro> result = notaRetiroServ.verDetalle(vm.idNotaProperty().get());
        if (result.isEmpty()) {
            NotificationHelper.mostrarAdvertencia("Generar ticket", "No se encontrado la nota.");
            return;
        }

        File file;
        if (SimpleDialogs.confirmacion("Generar ticket", "¿Quiere generar el ticket en la ruta predeterminada?")) {
            file = new File("C:\\Users\\Usuario\\Desktop\\nota retiro.txt");
        } else {
            file = SimpleDialogs.selectorRuta(event, "Seleccione donde quiere guardar la nota",
                    "nota retiro.txt",
                    new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));
        }
        if (file == null) return;
        GeneradorTXT.generaNotaRetiro(result.get().getDetallesRetiro().stream().toList(), file);

        if (SimpleDialogs.confirmacion("Imprimir ticket", "¿Desea imprimir el ticket generado?"))
            Impresor.imprimirConSistema(file);
    }

}