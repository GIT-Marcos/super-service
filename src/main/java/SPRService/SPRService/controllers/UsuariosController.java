package SPRService.SPRService.controllers;

import SPRService.SPRService.DTOs.filtros.FiltroUsuarioDTO;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.UsuarioViewModelTabla;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class UsuariosController implements Initializable {

    private final UsuarioServ usuarioServ;
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
    public UsuariosController(UsuarioServ usuarioServ) {
        this.usuarioServ = usuarioServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarControles();
        tabla.setItems(obsListUsuariosVM);
        verTodos();
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
        // Lógica para abrir la ventana/escena de creación de nuevo usuario.
        System.out.println("Acción: Nuevo Usuario");
        // Ejemplo: Abrir una nueva ventana de diálogo.
    }

    @FXML
    public void modificar() {
        UsuarioViewModelTabla seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Lógica para abrir la ventana/escena de modificación, pasando seleccionado.getUsuario().
            System.out.println("Acción: Modificar Usuario: " + seleccionado.getNombre());
        } else {
            // Lógica para mostrar una alerta de que no hay selección.
            System.out.println("Acción: Selecciona un usuario para modificar.");
        }
    }

    @FXML
    public void darDeBaja() {
        UsuarioViewModelTabla seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            NotificationHelper.mostrarAdvertencia("Dar de baja",
                    "Debe seleccionar un usuario para darlo de baja");
            return;
        }
        try {
            usuarioServ.darDeBaja(seleccionado.getUsuario()).ifPresent(seleccionado::actualizarDatos);
        } catch (RuntimeException e) {
            NotificationHelper.mostrarError("Dar de baja", "Ha ocurrido un error inesperado.");
            e.printStackTrace();
        }
    }
}