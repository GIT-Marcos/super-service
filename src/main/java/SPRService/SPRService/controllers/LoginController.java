package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.services.UsuarioServ;
import SPRService.SPRService.util.EMailSender;
import SPRService.SPRService.util.SimpleDialogs;
import SPRService.SPRService.util.alertas.NotificationHelper;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SessionManager;
import org.apache.commons.mail.EmailException;
import org.hibernate.HibernateException;

import java.net.URL;
import java.util.Optional;
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

    // ================= INIT =================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // ================= ACCIÓN PRINCIPAL =================

    @FXML
    private void iniciarSesion() {
        if (flagDebug) {
            loginDebug();
            return;
        }

        Optional<LoginFormData> datosValidados = validarFormulario();
        if (datosValidados.isEmpty()) return;

        LoginFormData data = datosValidados.get();

        autenticarUsuario(data);
    }

    @FXML
    public void recuperarContra() {
        String direccion = SimpleDialogs.pedirMailParaRecuperarContrasenia();
        if (direccion == null) return;

        enviarCorreoRecuperacion(direccion);
    }

    // ================= VALIDACIÓN =================

    private Optional<LoginFormData> validarFormulario() {
        LoginFormData data = new LoginFormData();
        StringBuilder errores = new StringBuilder("Por favor corrija los siguientes errores:\n");
        boolean hayErrores = false;

        // Validar nombre de usuario
        try {
            data.nombreUsuario = ManejadorInputs.nombreUsuario(tfNombreUsuario.getText());
            marcarCampoError(tfNombreUsuario, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfNombreUsuario, true);
            errores.append("- Usuario: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        // Validar contraseña
        try {
            data.contrasenia = ManejadorInputs.contrasenia(tfContrasenia.getText().trim());
            marcarCampoError(tfContrasenia, false);
        } catch (IllegalArgumentException e) {
            marcarCampoError(tfContrasenia, true);
            errores.append("- Contraseña: ").append(e.getMessage()).append("\n");
            hayErrores = true;
        }

        if (hayErrores) {
            NotificationHelper.mostrarError("Error de Validación", errores.toString());
            return Optional.empty();
        }

        return Optional.of(data);
    }

    // ================= AUTENTICACIÓN =================

    private void autenticarUsuario(LoginFormData data) {
        try {
            Usuario usuario = usuarioServ.loguear(data.nombreUsuario, data.contrasenia);
            SessionManager.iniciarSesion(usuario);

            limpiarCampos();
            appCoordinator.onLoginSuccess();

            NotificationHelper.mostrarExito("Inicio sesión", "Sesión iniciada con éxito.");

        } catch (IllegalArgumentException | HibernateException e) {
            NotificationHelper.mostrarAdvertencia("Inicio sesión", e.getMessage());
        } catch (Exception e) {
            NotificationHelper.mostrarError("Inicio sesión", "Ha ocurrido un error inesperado.");
            e.printStackTrace();
        }
    }

    private void loginDebug() {
        appCoordinator.onLoginSuccess();
        NotificationHelper.mostrarExito("Inicio sesión", "Sesión iniciada con éxito (modo debug).");
    }

    // ================= RECUPERACIÓN =================

    private void enviarCorreoRecuperacion(String direccion) {
        try {
            eMailSender.enviarMailRecuperacionContrasenia(direccion);
            NotificationHelper.mostrarExito("Recuperar contraseña",
                    "El correo de recuperación se ha enviado con éxito.");
        } catch (EmailException e) {
            NotificationHelper.mostrarError("Recuperar contraseña",
                    "Fallo al enviar el correo. Verifique su conexión o configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= UI =================

    private void marcarCampoError(Node node, boolean esError) {
        if (esError) {
            if (!node.getStyleClass().contains("error-border")) {
                node.getStyleClass().add("error-border");
            }
        } else {
            node.getStyleClass().remove("error-border");
        }
    }

    private void limpiarCampos() {
        tfNombreUsuario.clear();
        tfContrasenia.clear();

        // Limpiar estilos de error
        marcarCampoError(tfNombreUsuario, false);
        marcarCampoError(tfContrasenia, false);
    }

    // ================= DTO INTERNO =================

    private static class LoginFormData {
        String nombreUsuario;
        String contrasenia;
    }
}