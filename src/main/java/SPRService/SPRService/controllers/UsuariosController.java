package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.UsuarioViewModelTabla;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class UsuariosController implements Initializable {

    private final UsuarioServ usuarioServ;
    private final Navigator navigator;
    private ObservableList<UsuarioViewModelTabla> obsListUsuariosVM = FXCollections.observableArrayList();

    @FXML
    private TextField tfUsuario;
    @FXML
    private TextField tfCorreo;
    @FXML
    private CheckComboBox<RolUsuario> cbRoles;
    @FXML
    private TableView<UsuarioViewModelTabla> tabla;
    @FXML
    private TableColumn<UsuarioViewModelTabla, String> colUsuario;
    @FXML
    private TableColumn<UsuarioViewModelTabla, String> colCorreo;
    @FXML
    private TableColumn<UsuarioViewModelTabla, String> colRol;
    @FXML
    private TableColumn<UsuarioViewModelTabla, String> colEstado;
    @FXML
    private CheckBox chkActivos, chkInactivos;

    @Inject
    public UsuariosController(UsuarioServ usuarioServ, AppCoordinator coordinator) {
        this.usuarioServ = usuarioServ;
        this.navigator = coordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();
        tabla.setItems(obsListUsuariosVM);
        verTodos();
    }

    @FXML
    public void verTodos() {
        tfUsuario.clear();
        tfCorreo.clear();
        cbRoles.getCheckModel().clearChecks();
        List<UsuarioViewModelTabla> nuevosUsuarios = usuarioServ.verTodos().stream()
                .map(UsuarioViewModelTabla::new)
                .collect(Collectors.toList());

        obsListUsuariosVM.clear();
        obsListUsuariosVM.setAll(nuevosUsuarios);
    }

    @FXML
    public void buscar() {
        if (!chkActivos.isSelected() && !chkInactivos.isSelected()) {
            obsListUsuariosVM.clear();
            return;
        }
        String nombre = tfUsuario.getText().strip();
        String correo = tfCorreo.getText().strip();
        Set<RolUsuario> rolesSeleccionados = new HashSet<>(cbRoles.getCheckModel().getCheckedItems());
        FiltroUsuarioDTO filtros = new FiltroUsuarioDTO(nombre, correo, rolesSeleccionados,
                chkActivos.isSelected(), chkInactivos.isSelected());
        List<UsuarioViewModelTabla> usuariosBuscados = usuarioServ.buscar(filtros).stream()
                .map(UsuarioViewModelTabla::new)
                .collect(Collectors.toList());
        obsListUsuariosVM.setAll(usuariosBuscados);
    }

    @FXML
    public void nuevo() {
        Optional<UsuarioViewModelTabla> result = navigator.openModal(Views.CARGAR_USUARIO, "Crear usuario", null);
        result.ifPresent(obsListUsuariosVM::addFirst);
    }

    @FXML
    public void modificar() {
        UsuarioViewModelTabla seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            NotificationHelper.mostrarAdvertencia("Modificar usuario",
                    "Selecciona un usuario de la tabla para modificarlo.");
            return;
        }

        Optional<UsuarioViewModelTabla> result = navigator.openModal(Views.MODIFICAR_USUARIO, "Detalles/Modificar usuario", seleccionado);
        result.ifPresent(vm -> obsListUsuariosVM.set(obsListUsuariosVM.indexOf(seleccionado), vm));
    }

    @FXML
    public void darDeBaja() {
        UsuarioViewModelTabla seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            NotificationHelper.mostrarAdvertencia("Dar de baja",
                    "Debe seleccionar un usuario para darlo de baja");
            return;
        }
        if (!seleccionado.getUsuario().getActivo()) {
            NotificationHelper.mostrarAdvertencia("Dar de baja",
                    "Este usuario ya está INACTIVO.");
            return;
        }
        if (SimpleDialogs.confirmacion("Dar de baja",
                "¿Está seguro que quiere dar de baja a este usuario?")) {
            try {
                usuarioServ.darDeBaja(seleccionado.getUsuario()).ifPresent(seleccionado::actualizarDatos);
            } catch (RuntimeException e) {
                NotificationHelper.mostrarError("Dar de baja", "Ha ocurrido un error inesperado.");
                e.printStackTrace();
            }
        }
    }

    private void configurarControles() {
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cbRoles.getItems().setAll(RolUsuario.values());
        cbRoles.getCheckModel().checkAll();

        // --- Lógica para el color del estado ---
        colEstado.setCellFactory(column -> {
            return new TableCell<UsuarioViewModelTabla, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // Obtener el ViewModel asociado a esta fila
                        UsuarioViewModelTabla vm = getTableView().getItems().get(getIndex());
                        if (vm != null) {
                            if (vm.esAnuladaProperty().get()) {
                                setStyle("-fx-text-fill: #d63031; -fx-font-weight: bold;"); // Rojo
                            } else {
                                setStyle("-fx-text-fill: #00b894; -fx-font-weight: bold;"); // Verde
                            }
                        }
                    }
                }
            };
        });
    }
}