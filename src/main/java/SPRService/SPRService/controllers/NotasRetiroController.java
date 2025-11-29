package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.filtros.FiltroNotaRetiro;
import SPRService.SPRService.util.ResultadoPaginado;
import SPRService.SPRService.viewModels.tablas.NotaRetiroViewModel;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.NotaRetiroServ;
import SPRService.SPRService.util.SafeLocalDateConverter;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

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
        result.ifPresent(nota -> notasObsList.addFirst(new NotaRetiroViewModel(nota)));
    }

    @FXML
    private void verDetalles() {
        NotaRetiroViewModel vm = tablaNotas.getSelectionModel().getSelectedItem();
        if (vm == null) {
            mostrarNotificacion("Ver detalles", "Seleccione una nota para ver detalles.", true);
            return;
        }
        navigator.openModal(Views.DETALLE_NOTA_RETIRO, "Detalles de Nota #" + vm.idNotaProperty().getValue(), vm);
    }

    @FXML
    private void cancelarNota() {
        NotaRetiroViewModel vm = tablaNotas.getSelectionModel().getSelectedItem();
        if (vm == null) {
            mostrarNotificacion("Cancelar Nota", "Seleccione una nota para cancelarla.", true);
            return;
        }
        if (vm.esAnuladaProperty().get()) {
            mostrarNotificacion("Cancelar Nota", "La nota seleccionada ya está anulada.", true);
            return;
        }
        if (!Alertas.confirmacion("Cancelar nota de retiro",
                "Esta acción es irreversible y el stock se restablecerá.\n¿Confirmar cancelación?")) {
            return;
        }
        try {
            notaRetiroServ.cancelarNota(vm.getNotaOriginal());
            mostrarNotificacion("Éxito", "Nota cancelada correctamente.", false);
            cargarPagina(paginacion.getCurrentPageIndex());
        } catch (RuntimeException e) {
            Notifications.create()
                    .title("Error al cancelar")
                    .text(e.getMessage())
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.CENTER)
                    .showError();
            e.printStackTrace();
        }
    }

    @FXML
    private void generarTicket() {

    }

    private void mostrarNotificacion(String titulo, String texto, boolean esAdvertencia) {
        Notifications n = Notifications.create()
                .title(titulo)
                .text(texto)
                .hideAfter(Duration.seconds(3))
                .position(Pos.CENTER);

        if (esAdvertencia) n.showWarning();
        else n.showInformation();
    }
}