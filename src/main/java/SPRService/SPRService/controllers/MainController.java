package SPRService.SPRService.controllers;

import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import SPRService.SPRService.util.SessionManager;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final AppCoordinator appCoordinator;
    private final Navigator navigator;

    @FXML
    private Pane contentPane;
    @FXML
    private VBox vboxNavBar;
    @FXML
    private ImageView rateImg;

    @Inject
    public MainController(AppCoordinator appCoordinator) {
        this.appCoordinator = appCoordinator;
        this.navigator = this.appCoordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigator.bind(contentPane);
        navigator.navigateTo(Views.DEPOSITO);
        configurarControles();
    }

    private void configurarControles() {
        rateImg.setOnMouseClicked(p -> {
            Optional<Boolean> result = navigator.openModal(Views.RATE, "Valora el sistema", null);
            if (result.isPresent()) {
                Notifications.create()
                        .title("Gracias!!!")
                        .text("Se ha enviado su valoración\nMuchas gracias!")
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.CENTER)
                        .show();
            }
        });
    }

    @FXML
    private void irDeposito() {
        navigator.navigateTo(Views.DEPOSITO);
    }

    @FXML
    private void irNuevaVenta() {
        navigator.openModal(Views.CARGAR_VENTA, "Cargar nueva venta", null);
    }

    @FXML
    private void irVentas() {
        navigator.navigateTo(Views.VENTAS);
    }

    @FXML
    private void irNotasRetiro() {
        navigator.navigateTo(Views.NOTAS_RETIRO);
    }

    @FXML
    private void irVehiculos() {
        navigator.navigateTo(Views.VEHICULOS);
    }

    @FXML
    private void irClientes() {
        navigator.navigateTo(Views.CLIENTES);
    }

    @FXML
    private void irVerOrdenes() {
        navigator.navigateTo(Views.SERVICES);
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        if (!SessionManager.cerrarSesion()) {
            return;
        }
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();

        appCoordinator.closeSesion();
    }


}
