package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.DefaultNavigator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.services.UsuarioServ;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import SPRService.SPRService.entities.Usuario;
import SPRService.SPRService.util.ManejadorInputs;
import SPRService.SPRService.util.SessionManager;
import SPRService.SPRService.util.alertas.Alertas;
import org.hibernate.HibernateException;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private final UsuarioServ usuarioServ;
    //para evitar loguearse cuando se testea.
    private boolean flagDebug = false;
    private final AppCoordinator appCoordinator;
    // navegador solo para moverse en el login
    private final Navigator localNavigator;

    @FXML
    private GridPane pane;
    @FXML
    private TextField tfNombreUsuario;
    @FXML
    private PasswordField tfContrasenia;
    @FXML
    private Label labelRecuContra;

    @Inject
    public LoginController(UsuarioServ usuarioServ, AppCoordinator appCoordinator,
                           Provider<FXMLLoader> fxmlLoaderProvider) {
        this.usuarioServ = usuarioServ;
        this.appCoordinator = appCoordinator;
        this.localNavigator = new DefaultNavigator(fxmlLoaderProvider);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ¡IMPORTANTE! El navegador local necesita saber la ventana para los modales.
        // Lo hacemos cuando la escena esté disponible.
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                localNavigator.setOwnerWindow(newScene.getWindow());
            }
        });
    }

    @FXML
    private void iniciarSesion(ActionEvent event) {
        String nombreUsuario = tfNombreUsuario.getText().trim();
        String inputPass = tfContrasenia.getText().trim();
        if (flagDebug) {
            appCoordinator.onLoginSuccess();

            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
        } else {
            try {
                ManejadorInputs.textoGenerico(nombreUsuario, true, 3, 30);
                ManejadorInputs.contrasenia(inputPass, true);
                Usuario usuario = usuarioServ.loguear(nombreUsuario, inputPass);
                SessionManager.iniciarSesion(usuario);
                appCoordinator.onLoginSuccess();

                Node n = ((Node) event.getSource());
                Stage s = (Stage) n.getScene().getWindow();
                s.close();
            } catch (IllegalArgumentException | HibernateException e) {
                Alertas.aviso("Inicio sesión", e.getMessage());
            }
        }
    }

    @FXML
    private void crearUsuario() {
        localNavigator.openModal(Views.CREAR_USUARIO, "Crear usuario", null);
    }

    @FXML
    private void salir(ActionEvent event) {
        boolean confir = Alertas.confirmacion("Salir del programa",
                "¿Está seguro que desea salir del programa?");
        if (!confir) {
            return;
        }
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    @FXML
    public void clickedRecuperarContrasenia(MouseEvent mouseEvent) {
        labelRecuContra.setUnderline(true);
    }


}
