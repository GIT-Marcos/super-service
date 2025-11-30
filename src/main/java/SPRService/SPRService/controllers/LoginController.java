package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.services.UsuarioServ;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SessionManager;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.hibernate.HibernateException;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final UsuarioServ usuarioServ;
    //para evitar loguearse cuando se testea.
    private boolean flagDebug = false;
    private final AppCoordinator appCoordinator;

    @FXML
    private TextField tfNombreUsuario;
    @FXML
    private PasswordField tfContrasenia;

    @Inject
    public LoginController(UsuarioServ usuarioServ, AppCoordinator appCoordinator) {
        this.usuarioServ = usuarioServ;
        this.appCoordinator = appCoordinator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void iniciarSesion() {
        String nombreUsuario = tfNombreUsuario.getText().trim();
        String inputPass = tfContrasenia.getText().trim();
        if (flagDebug) {
            appCoordinator.onLoginSuccess();
            Notifications.create()
                    .position(Pos.BOTTOM_RIGHT)
                    .hideAfter(Duration.seconds(3))
                    .title("Inicio sesión")
                    .text("Sesión iniciada con éxito.")
                    .showInformation();
        } else {
            try {
                ManejadorInputs.textoGenerico(nombreUsuario, true, "Nombre de usuario",
                        30);
                ManejadorInputs.contrasenia(inputPass);
                Usuario usuario = usuarioServ.loguear(nombreUsuario, inputPass);
                SessionManager.iniciarSesion(usuario);
                appCoordinator.onLoginSuccess();
                Notifications.create()
                        .position(Pos.BOTTOM_RIGHT)
                        .hideAfter(Duration.seconds(3))
                        .title("Inicio sesión")
                        .text("Sesión iniciada con éxito.")
                        .showInformation();
            } catch (IllegalArgumentException | HibernateException e) {
                Notifications.create()
                        .position(Pos.CENTER)
                        .hideAfter(Duration.seconds(3))
                        .title("Inicio sesión")
                        .text(e.getMessage())
                        .showWarning();
            } catch (Exception e) {
                Notifications.create()
                        .position(Pos.CENTER)
                        .hideAfter(Duration.seconds(5))
                        .title("Inicio sesión")
                        .text("Ha ocurrido un error inesperado al iniciar sesión.")
                        .showError();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void recuperarContra() {

    }


}
