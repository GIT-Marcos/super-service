package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaOperacionUniversal;
import SPRService.SPRService.navigation.DataReceiver;
import SPRService.SPRService.viewModels.celdas.ItemOperacionViewModel;
import SPRService.SPRService.viewModels.celdas.ItemServiceViewModel;
import SPRService.SPRService.viewModels.celdas.ItemVentaViewModel;
import SPRService.SPRService.viewModels.tablas.ClienteViewModelTabla;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OperacionesClienteController implements Initializable, DataReceiver<ClienteViewModelTabla> {

    private ClienteViewModelTabla vm;
    private ObservableList<ItemOperacionViewModel> obsList = FXCollections.observableArrayList();

    @FXML
    private Label lblTituloCliente;
    @FXML
    private Label lblCantVentas;
    @FXML
    private Label lblCantServices;
    @FXML
    ListView<ItemOperacionViewModel> listViewOperaciones;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewOperaciones.setItems(obsList);
        listViewOperaciones.setCellFactory(c -> new CeldaOperacionUniversal());
        String css = getClass().getResource("/styles/celda-operacion.css").toExternalForm();
        listViewOperaciones.getStylesheets().add(css);
    }

    @Override
    public void receiveData(ClienteViewModelTabla data) {
        if (data != null) {
            this.vm = data;
            lblTituloCliente.setText(data.getDni() + " | " + data.getNombre() + " " + data.getApellido());

            llenarCampos();
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Node n = ((Node) event.getSource());
        Stage s = (Stage) n.getScene().getWindow();
        s.close();
    }

    private void llenarCampos() {
        obsList.clear();
        List<ItemServiceViewModel> servVMs = this.vm.getClienteEntity().getServices().stream()
                .map(ItemServiceViewModel::new).toList();
        List<ItemVentaViewModel> venVMs = this.vm.getClienteEntity().getVentas().stream()
                .map(ItemVentaViewModel::new).toList();
        obsList.addAll(servVMs);
        obsList.addAll(venVMs);

        lblCantVentas.setText(String.valueOf(venVMs.size()));
        lblCantServices.setText(String.valueOf(servVMs.size()));
    }
}
