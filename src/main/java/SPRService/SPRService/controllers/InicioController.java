package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.DefaultNavigator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import SPRService.SPRService.util.alertas.Alertas;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InicioController implements Initializable {

    private final Navigator navigator;

    @FXML
    private Pane pane;

    @Inject
    public InicioController(Provider<FXMLLoader> fxmlLoaderProvider) {
        this.navigator = new DefaultNavigator(fxmlLoaderProvider);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigator.bind(pane);
        navigator.navigateTo(Views.LOGIN);
    }

    @FXML
    private void iniciarSesion() {
        navigator.navigateTo(Views.LOGIN);
    }

    @FXML
    private void nuevoUsuario() {
        navigator.navigateTo(Views.CARGAR_USUARIO);
    }

    @FXML
    private void salir(ActionEvent event) {
        if (!Alertas.confirmacion("Salir del programa",
                "¿Está seguro que desea salir del programa?"))
            return;

        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

}
