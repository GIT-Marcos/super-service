package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SessionManager;
import org.apache.commons.mail.EmailException;
import org.hibernate.HibernateException;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final UsuarioServ usuarioServ;
    private final EMailSender eMailSender;
    //para evitar loguearse cuando se testea.
    private boolean flagDebug = false;
    private final AppCoordinator appCoordinator;

    @FXML
    private TextField tfNombreUsuario;
    @FXML
    private PasswordField tfContrasenia;

    @Inject
    public LoginController(UsuarioServ usuarioServ, EMailSender eMailSender, AppCoordinator appCoordinator) {
        this.usuarioServ = usuarioServ;
        this.eMailSender = eMailSender;
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
            NotificationHelper.mostrarExito("Inicio sesión", "Sesión iniciada con éxito.");
        } else {
            try {
                ManejadorInputs.textoGenerico(nombreUsuario, true, "Nombre de usuario",
                        30);
                ManejadorInputs.contrasenia(inputPass);
                Usuario usuario = usuarioServ.loguear(nombreUsuario, inputPass);
                SessionManager.iniciarSesion(usuario);
                appCoordinator.onLoginSuccess();
                NotificationHelper.mostrarExito("Inicio sesión", "Sesión iniciada con éxito.");
            } catch (IllegalArgumentException | HibernateException e) {
                NotificationHelper.mostrarAdvertencia("Inicio sesión", e.getMessage());
            } catch (Exception e) {
                NotificationHelper.mostrarError("Inicio sesión", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void recuperarContra() {
        String direccion = SimpleDialogs.pedirMailParaRecuperarContrasenia();
        if (direccion == null) return;
        try {
            eMailSender.enviarMailRecuperacionContrasenia(direccion);
            NotificationHelper.mostrarExito("Recuperar contraseña", "El correo de recuperación se ha enviado con éxito.");
        } catch (EmailException e) {
            NotificationHelper.mostrarError("Recuperar contraseña",
                    "Fallo al enviar el correo. Verifique su conexión o configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
