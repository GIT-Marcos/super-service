package SPRService.SPRService.controllers;

import SPRService.SPRService.services.UsuarioServ;
import com.google.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.exceptions.DuplicateUserException;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.alertas.Alertas;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ResourceBundle;

public class CrearUsuarioController implements Initializable {

    private final UsuarioServ usuarioServ;

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

    private void llenarComboRoles() {
        ObservableList<RolUsuario> datosLista = FXCollections.observableArrayList(RolUsuario.values());
        comboRoles.setItems(datosLista);
        comboRoles.getSelectionModel().select(RolUsuario.GERENCIAL);
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

            boolean resultado = Alertas.confirmacion("Confirmación", "¿Está seguro que desea " +
                    "cargar el usuario " + nombre + "?");
            if (!resultado) {
                return;
            }
            Usuario usuario = new Usuario(null, nombre, correo, contrasenia, privilegio);
            usuarioServ.cargarUsuario(usuario);
            limpiarCampos();
            Notifications.create()
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .title("Crear usuario")
                    .text("Se ha creado el usuario " + nombre + " con éxito.")
                    .showInformation();
        } catch (IllegalArgumentException | DuplicateUserException e) {
            Notifications.create()
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(3))
                    .title("Crear usuario")
                    .text(e.getMessage())
                    .showWarning();
        } catch (Exception e) {
            Notifications.create()
                    .position(Pos.CENTER)
                    .hideAfter(Duration.seconds(5))
                    .title("Crear usuario")
                    .text("Ha ocurrido un error inesperado al crear el usuario.")
                    .showError();
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        tfNombre.setText("");
        tfCorreo.setText("");
        tfContrasenia.setText("");
        comboRoles.getSelectionModel().select(RolUsuario.GERENCIAL);
    }

}
