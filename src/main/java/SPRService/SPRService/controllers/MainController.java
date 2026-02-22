package SPRService.SPRService.controllers;

import SPRService.SPRService.enums.RolUsuario;
import SPRService.SPRService.navigation.AppCoordinator;
import SPRService.SPRService.navigation.Navigator;
import SPRService.SPRService.navigation.Views;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import SPRService.SPRService.util.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final AppCoordinator appCoordinator;
    private final Navigator navigator;

    @FXML
    private Pane contentPane;
    @FXML
    private ToggleButton btnDeposito, btnNotas, btnVentas, btnService, btnVehiculos, btnClientes, btnUsuarios;
    @FXML
    private Button btnNuevaVenta;

    @Inject
    public MainController(AppCoordinator appCoordinator) {
        this.appCoordinator = appCoordinator;
        this.navigator = this.appCoordinator.getMainNavigator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navigator.bind(contentPane);
        gestionarPermisos();
    }

    private void gestionarPermisos() {
        if (SessionManager.haySesionActiva()) {
            RolUsuario rol = SessionManager.getRolUsuario();
            // Permisos según departamentos
            switch (rol) {
                case ADMINISTRADOR -> {
                    navigator.navigateTo(Views.DEPOSITO);
                    btnDeposito.setSelected(true);
                }
                case OPERATIVO_TALLER, JEFE_TALLER -> {
                    btnNuevaVenta.setDisable(true);
                    btnClientes.setDisable(true);
                    btnDeposito.setDisable(true);
                    btnVentas.setDisable(true);
                    btnUsuarios.setDisable(true);
                    navigator.navigateTo(Views.SERVICES);
                    btnService.setSelected(true);
                }
                case OPERATIVO_VENTAS, JEFE_VENTAS -> {
                    btnDeposito.setDisable(true);
                    btnService.setDisable(true);
                    btnVehiculos.setDisable(true);
                    btnUsuarios.setDisable(true);
                    navigator.navigateTo(Views.VENTAS);
                    btnVentas.setSelected(true);
                }
                case OPERATIVO_DEPOSITO, JEFE_DEPOSITO -> {
                    btnNuevaVenta.setDisable(true);
                    btnVentas.setDisable(true);
                    btnService.setDisable(true);
                    btnVehiculos.setDisable(true);
                    btnClientes.setDisable(true);
                    btnUsuarios.setDisable(true);
                    navigator.navigateTo(Views.DEPOSITO);
                    btnDeposito.setSelected(true);
                }
                case OPERATIVO_RECEPCION, JEFE_RECEPCION -> {
                    btnDeposito.setDisable(true);
                    btnNotas.setDisable(true);
                    btnUsuarios.setDisable(true);
                    navigator.navigateTo(Views.CLIENTES);
                    btnClientes.setSelected(true);
                }
            }
        } else {
            navigator.navigateTo(Views.DEPOSITO);
            btnDeposito.setSelected(true);
        }
    }

    @FXML
    private void valorar() {
        navigator.openModal(Views.RATE, "Valorar sistema", null);
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
    private void irUsuarios() {
        navigator.navigateTo(Views.USUARIOS);
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        if (SessionManager.cerrarSesion()) {
            Node n = ((Node) event.getSource());
            Stage s = (Stage) n.getScene().getWindow();
            s.close();
            appCoordinator.closeSesion();
        }
    }


}
