package SPRService.SPRService.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class StepClienteController implements Initializable {

    @FXML
    private TextField tfDni;
    @FXML
    private ListView<String> lvClientes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void cargarNuevo() {

    }
}
