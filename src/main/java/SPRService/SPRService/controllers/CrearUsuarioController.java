package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.ModalController;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import SPRService.SPRService.viewModels.tablas.UsuarioViewModelTabla;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.exceptions.DuplicateUserNameException;
import SPRService.SPRService.util.ManejadorInputs;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CrearUsuarioController implements Initializable, ModalController<UsuarioViewModelTabla> {

    private final UsuarioServ usuarioServ;
    private UsuarioViewModelTabla viewModelUsuarioCreado;

    @FXML
    private TextField tfNombre, tfContrasenia, tfCorreo;
    @FXML
    private ComboBox<RolUsuario> comboRoles;

    @Inject
    public CrearUsuarioController(UsuarioServ usuarioServ) {
        this.usuarioServ = usuarioServ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        llenarComboRoles();
    }

    @Override
    public Optional<UsuarioViewModelTabla> getResult() {
        return Optional.ofNullable(viewModelUsuarioCreado);
    }

    @FXML
    private void cargarUsuario() {
        String nombre = tfNombre.getText().strip();
        String contrasenia = tfContrasenia.getText();
        String correo = tfCorreo.getText().strip();
        RolUsuario privilegio = comboRoles.getSelectionModel().getSelectedItem();

        try {
            ManejadorInputs.textoGenerico(nombre, true, "Nombre de usuario", 20);
            ManejadorInputs.eMail(correo, true);
            ManejadorInputs.contrasenia(contrasenia);

            if (!SimpleDialogs.confirmacion("Crear usuario", "¿Está seguro que desea crear un nuevo usuario?"))
                return;

            Usuario usuario = new Usuario(null, nombre, correo, contrasenia, privilegio);
            usuarioServ.cargarUsuario(usuario).ifPresent(u ->
                    viewModelUsuarioCreado = new UsuarioViewModelTabla(u));
            limpiarCampos();
            NotificationHelper.mostrarExito("Crear usuario", "Se ha creado el usuario " + nombre + " con éxito.");
        } catch (IllegalArgumentException | DuplicateUserNameException e) {
            NotificationHelper.mostrarAdvertencia("Crear usuario", e.getMessage());
        } catch (Exception e) {
            NotificationHelper.mostrarError("Crear usuario", e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfCorreo.setText("");
        tfContrasenia.setText("");
        comboRoles.getSelectionModel().select(RolUsuario.GERENCIAL);
    }

    private void llenarComboRoles() {
        ObservableList<RolUsuario> datosLista = FXCollections.observableArrayList(RolUsuario.values());
        comboRoles.setItems(datosLista);
        comboRoles.getSelectionModel().select(RolUsuario.GERENCIAL);
    }
}
