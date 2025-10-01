package SPRService.SPRService.controllers;

import SPRService.SPRService.components.CeldaDetalleRetiro;
import SPRService.SPRService.entities.DetalleRetiro;
import SPRService.SPRService.entities.NotaRetiro;
import SPRService.SPRService.navigation.DataReceiver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class DetalleNotaRetiroController implements Initializable, DataReceiver<NotaRetiro> {

    private NotaRetiro notaRetiro = new NotaRetiro();
    private ObservableList<DetalleRetiro> detallesObsList = FXCollections.observableArrayList();

    @FXML
    private Label labNroNota;
    @FXML
    private Label labFechaNota;
    @FXML
    private ListView<DetalleRetiro> listViewDetalles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewDetalles.setItems(this.detallesObsList);
        listViewDetalles.setCellFactory(param -> new CeldaDetalleRetiro());
    }

    @Override
    public void receiveData(NotaRetiro data) {
        this.notaRetiro = data;
        this.detallesObsList.addAll(data.getDetallesRetiroList());

        labNroNota.setText(String.valueOf(data.getId()));
        labFechaNota.setText(String.valueOf(data.getFecha()));
    }
}
