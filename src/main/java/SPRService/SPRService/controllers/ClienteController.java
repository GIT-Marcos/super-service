package SPRService.SPRService.controllers;

import SPRService.SPRService.entities.Cliente;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.ClienteServ;
import SPRService.SPRService.util.ResultadoPaginado;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.ClienteViewModelTabla;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {

    private static final int ITEMS_POR_PAGINA = 30;
    private final ClienteServ clienteServ;
    private final Navigator navigator;
    private final ObservableList<ClienteViewModelTabla> obsListClientes = FXCollections.observableArrayList();
    private int paginaActual = 0;
    private int totalPaginas = 1;

    @FXML
    private TextField tfDNI, tfApellido, tfNombre;
    @FXML
    private CheckBox cbActivos, cbInactivos;
    @FXML
    private TableView<ClienteViewModelTabla> tablaClientes;
    @FXML
    private TableColumn<ClienteViewModelTabla, String> colDNI, colApellido, colNombre, colEstado;
    @FXML
    private Pagination paginacion;

    @Inject
    public ClienteController(ClienteServ clienteServ, AppCoordinator appCoordinator) {
        this.clienteServ = clienteServ;
        this.navigator = appCoordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configColumnas();
        configurarPaginacion();
        tablaClientes.setItems(obsListClientes);

        // Cargar primera página
        cargarPagina(0);
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
        String dni = tfDNI.getText() != null ? tfDNI.getText().strip() : "";
        String apellido = tfApellido.getText() != null ? tfApellido.getText().strip() : "";
        String nombre = tfNombre.getText() != null ? tfNombre.getText().strip() : "";
        boolean verActivos = cbActivos.isSelected();
        boolean verBaja = cbInactivos.isSelected();

        ResultadoPaginado<Cliente> resultado = clienteServ.buscarPaginado(
                dni, apellido, nombre, verActivos, verBaja, numeroPagina, ITEMS_POR_PAGINA);

        // Actualizar datos de paginación
        int paginas = (int) Math.ceil((double) resultado.getCantidadResultados() / ITEMS_POR_PAGINA);
        totalPaginas = Math.max(1, paginas);
        paginaActual = numeroPagina;

        // Actualizar el control de paginación
        paginacion.setPageCount(totalPaginas);

        // Actualizar la tabla
        obsListClientes.clear();
        for (Cliente c : resultado.getLista()) {
            obsListClientes.add(new ClienteViewModelTabla(c));
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
    private void todosLosClientes() {
        tfDNI.clear();
        tfApellido.clear();
        tfNombre.clear();
        cbActivos.setSelected(true);
        cbInactivos.setSelected(true);

        paginacion.setCurrentPageIndex(0);
        cargarPagina(0);
    }

    @FXML
    private void buscarConFiltros() {
        if (!cbInactivos.isSelected() && !cbActivos.isSelected()) {
            obsListClientes.clear();
            return;
        }
        paginacion.setCurrentPageIndex(0);
        cargarPagina(0);
    }

    @FXML
    private void nuevoCliente() {
        Optional<Cliente> optional = navigator.openModal(Views.CARGAR_CLIENTE, "Cargar nuevo cliente", null);

        if (optional.isPresent()) {
            // Ir a la primera página para ver el nuevo cliente
            paginacion.setCurrentPageIndex(0);
            cargarPagina(0);
        }
    }

    @FXML
    private void modificar() {
        ClienteViewModelTabla cvmt = tablaClientes.getSelectionModel().getSelectedItem();
        if (cvmt == null) {
            NotificationHelper.mostrarAdvertencia("Modificar cliente",
                    "Debe seleccionar un cliente para modificarlo.");
            return;
        }

        Optional<Cliente> result = clienteServ.verDatosContacto(cvmt.getClienteEntity().getId());
        result.ifPresent(c -> {
            Optional<Cliente> editResult = navigator.openModal(Views.CARGAR_CLIENTE, "Modificar cliente", c);
            // Recargar la página actual si hubo modificación
            if (editResult.isPresent()) {
                recargarPaginaActual();
            }
        });
    }

    @FXML
    private void verOperaciones() {
        ClienteViewModelTabla vm = tablaClientes.getSelectionModel().getSelectedItem();
        if (vm == null) {
            NotificationHelper.mostrarAdvertencia("Operaciones de cliente",
                    "Debe seleccionar un cliente para ver sus operaciones.");
            return;
        }

        Optional<Cliente> result = clienteServ.verOperacionesConVehiculos(vm.getClienteEntity().getId());
        result.ifPresent(c -> navigator.openModal(Views.OPERACIONES_CLIENTE, "Operaciones de cliente", c));
    }

    @FXML
    private void darDeBaja() {
        ClienteViewModelTabla cvmt = tablaClientes.getSelectionModel().getSelectedItem();
        if (cvmt == null) {
            NotificationHelper.mostrarAdvertencia("Dar de baja cliente",
                    "Debe seleccionar un cliente para darlo de baja.");
            return;
        }

        if (!SimpleDialogs.confirmacion("Dar de baja cliente", "¿Confirmar baja de cliente?")) {
            return;
        }

        try {
            clienteServ.softDeleteClient(cvmt.getClienteEntity());
            NotificationHelper.mostrarExito("Dar de baja cliente", "Se ha dado de baja el cliente con éxito.");
            // Recargar la página actual para reflejar el cambio
            recargarPaginaActual();
        } catch (Exception e) {
            NotificationHelper.mostrarError("Dar de baja cliente", "Ha ocurrido un error inesperado.");
            throw new RuntimeException(e);
        }
    }

    // ==================== CONFIGURACIÓN ====================

    private void configColumnas() {
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        if (colEstado != null) {
            colEstado.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(null);
                    setStyle("");
                    if (empty || item == null) {
                        return;
                    }
                    setText(item);
                    ClienteViewModelTabla row = getTableView().getItems().get(getIndex());
                    if (row.getClienteEntity().getActivo()) {
                        setStyle("-fx-text-fill: #028126; -fx-font-weight: bold; -fx-alignment: CENTER");
                    } else {
                        setStyle("-fx-text-fill: #912a2b; -fx-font-weight: bold; -fx-alignment: CENTER");
                    }
                }
            });
        }
    }
}